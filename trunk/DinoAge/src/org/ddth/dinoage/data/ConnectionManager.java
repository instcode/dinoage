/****************************************************
 * $Project: DinoAge
 * $Date:: Mar 21, 2008
 * $Revision: 
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.dinoage.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.ddth.dinoage.DinoAgeSettings;
import org.ddth.dinoage.ResourceManager;

/**
 * @author khoa.nguyen
 * 
 */
public class ConnectionManager {
	private Connection connection = null;
	private static ConnectionManager manager = new ConnectionManager();

	public static ConnectionManager getInstance() {
		return manager;
	}

	public static InputStream getDbScriptStream() {
		String scriptFile = "dinoage_" + DinoAgeSettings.getInstance().getDbType() + ".sql";
		return ResourceManager.class.getClassLoader().getResourceAsStream(scriptFile);
	}

	private ConnectionManager() {
		try {
			Class.forName(DinoAgeSettings.getInstance().getDriverClass())
					.newInstance();
			connection = DriverManager.getConnection(DinoAgeSettings
					.getInstance().getDbConnectionURL(), DinoAgeSettings
					.getInstance().getDbUsername(), DinoAgeSettings
					.getInstance().getDbPassword());
			executeSQLScript(connection, getDbScriptStream());
		}
		catch (Exception e) {
		}
	};

	public void closeManager() throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public void close(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
		}
	}

	/**
	 * Executes a SQL script.
	 * 
	 * @param con
	 *            database connection.
	 * @param resource
	 *            an input stream for the script to execute.
	 * @throws IOException
	 *             if an IOException occurs.
	 * @throws SQLException
	 *             if an SQLException occurs.
	 */
	private void executeSQLScript(Connection con, InputStream resource)
			throws IOException, SQLException {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(resource));
			boolean done = false;
			while (!done) {
				StringBuilder command = new StringBuilder();
				while (true) {
					String line = in.readLine();
					if (line == null) {
						done = true;
						break;
					}
					// Ignore comments and blank lines.
					if (isSQLCommandPart(line)) {
						command.append(" ").append(line);
					}
					if (line.trim().endsWith(";")) {
						command.deleteCharAt(command.length() - 1);
						break;
					}
				}
				// Send command to database.
				if (!done && !command.toString().equals("")) {
					Statement stmt = con.createStatement();
					stmt.execute(command.toString());
					stmt.close();
				}
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Returns true if a line from a SQL schema is a valid command part.
	 * 
	 * @param line
	 *            the line of the schema.
	 * @return true if a valid command part.
	 */
	private boolean isSQLCommandPart(String lines) {
		String line = lines.trim();
		if (line.equals("")) {
			return false;
		}
		// Check to see if the line is a comment. Valid comment types:
		// "//" is HSQLDB
		// "--" is DB2 and Postgres
		// "#" is MySQL
		// "REM" is Oracle
		// "/*" is SQLServer
		return !(line.startsWith("//") || line.startsWith("--")
				|| line.startsWith("#") || line.startsWith("REM")
				|| line.startsWith("/*") || line.startsWith("*"));
	}
}