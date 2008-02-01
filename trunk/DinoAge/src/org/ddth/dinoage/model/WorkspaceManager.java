/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 30, 2008 12:34:20 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.ddth.dinoage.ResourceManager;

public class WorkspaceManager {
	
	private static final String WORKSPACE_SEPARATOR = "\n";
	private static final String PROPERTY_WORKSPACE_RECENTS = "workspace.recents";
	
	private List<String> workspaces = new ArrayList<String>();
	private String selection;
	private String configureFile;
	
	public WorkspaceManager(String configureFile) {
		this.configureFile = configureFile;
	}
	
	public void loadConfiguration() throws IOException {
		InputStream inputStream = null;
		try {
			File profileFolder = new File(configureFile);
			if (!profileFolder.exists()) {
				return;
			}
			
			Properties props = new Properties();
			inputStream = new FileInputStream(profileFolder);
			props.load(inputStream);
			
			String recentWorkspaces = props.getProperty(PROPERTY_WORKSPACE_RECENTS, "");
			StringTokenizer tokenizer = new StringTokenizer(recentWorkspaces, WORKSPACE_SEPARATOR);
			while (tokenizer.hasMoreElements()) {
				String directory = tokenizer.nextToken();
				File workspaceFolder = new File(directory);
				workspaces.add(workspaceFolder.getAbsolutePath());
			}
			selection = workspaces.size() > 0 ? workspaces.get(0) : null;
		}
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
	
	public void saveConfiguration() throws IOException {
		OutputStream outputStream = null;
		try {
			File profileFolder = new File(configureFile);
			Properties props = new Properties();
			StringBuilder recentWorkspaces = new StringBuilder();
			if (selection != null) {
				recentWorkspaces.append(selection + WORKSPACE_SEPARATOR);
			}
			String[] workspaces = getWorkspaces();
			for (String workspace : workspaces) {
				if (!workspace.equals(selection)) {
					recentWorkspaces.append(workspace + WORKSPACE_SEPARATOR);
				}
			}
			props.put(PROPERTY_WORKSPACE_RECENTS, recentWorkspaces.toString());
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

	public String getSelection() {
		return selection;
	}

	public String[] getWorkspaces() {
		return workspaces.toArray(new String[workspaces.size()]);
	}

	public String setWorkspace(String workspacePath) {
		File workspaceFolder = new File(workspacePath);
		if (!workspaceFolder.exists() || !workspaceFolder.isDirectory()) {
			return null;
		}
		if (selection == null || !selection.equals(workspaceFolder.getAbsolutePath())) {
			selection = workspaceFolder.getAbsolutePath();
		}
		return selection;
	}
}
