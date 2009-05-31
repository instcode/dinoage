/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 29, 2008 9:32:44 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class DinoAgeSettings {
	private static final String PROPERTY_WORKSPACE_RECENTS = "workspace.recents";
	private static final String PROPERTY_WORKSPACE_LOAD_PROFILES = "workspace.profile.load";
	private static final String PROPERTY_DB_TYPE = "db.type";
	private static final String PROPERTY_DB_JDBC_DRIVER = "db.jdbc";
	private static final String PROPERTY_DB_CONNECTION_URL = "db.url";
	private static final String PROPERTY_DB_USERNAME = "db.username";
	private static final String PROPERTY_DB_PASSWORD = "db.password";

	private static final String DEFAULT_DB_TYPE = "derby";
	private static final String DEFAULT_DB_JDBC_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String DEFAULT_DB_CONNECTION_URL = "jdbc:derby:database;create=true;";
	private static final String DEFAULT_DB_USERNAME = "";
	private static final String DEFAULT_DB_PASSWORD = "";
	
	private Properties props;
	private File profileFolder = new File("dinoage.conf");

	private static final DinoAgeSettings instance = new DinoAgeSettings();
	
	private DinoAgeSettings() {
		InputStream inputStream = null;
		try {
			props = new Properties();
			if (!profileFolder.exists()) {
				return;
			}
			inputStream = new FileInputStream(profileFolder);
			props.load(inputStream);
		}
		catch (IOException e) {
		}
		finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				}
				catch (IOException e) {
				}
			}
		}
	}
	
	public static DinoAgeSettings getInstance() {
		return instance;
	}

	public void saveConfiguration() throws IOException {
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(profileFolder);
			props.store(outputStream,
					ResourceManager.getMessage(ResourceManager.KEY_SYSTEM_CONFIG_FILE_HEADER));
		}
		finally {
			// Make sure the output stream is closed gracefully ;-)
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}
	
	public boolean isLoadProfiles() {
		return Boolean.parseBoolean(props.getProperty(PROPERTY_WORKSPACE_LOAD_PROFILES, "true"));
	}
	
	public void setLoadProfiles(boolean yes) {
		props.setProperty(PROPERTY_WORKSPACE_LOAD_PROFILES, String.valueOf(yes));
	}
	
	public void setRecentWorkspaces(String value) {
		props.setProperty(PROPERTY_WORKSPACE_RECENTS, value);
	}
	
	public String getRecentWorkspaces() {
		return props.getProperty(PROPERTY_WORKSPACE_RECENTS, "");
	}
	
	public String getDbType() {
		return props.getProperty(PROPERTY_DB_TYPE, DEFAULT_DB_TYPE);
	}
	
	public String getDriverClass() {
		return props.getProperty(PROPERTY_DB_JDBC_DRIVER, DEFAULT_DB_JDBC_DRIVER);
	}
	
	public String getDbConnectionURL() {
		return props.getProperty(PROPERTY_DB_CONNECTION_URL, DEFAULT_DB_CONNECTION_URL);
	}
	
	public String getDbUsername() {
		return props.getProperty(PROPERTY_DB_USERNAME, DEFAULT_DB_USERNAME);
	}
	
	public String getDbPassword() {
		return props.getProperty(PROPERTY_DB_PASSWORD, DEFAULT_DB_PASSWORD);
	}
}
