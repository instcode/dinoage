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
	
	private Properties props;
	private String configFile;

	public DinoAgeSettings(String configFile) {
		this.configFile = configFile;
	}
	
	public void loadConfiguration() throws IOException {
		InputStream inputStream = null;
		try {
			props = new Properties();
			File profileFolder = new File(configFile);
			if (!profileFolder.exists()) {
				return;
			}
			inputStream = new FileInputStream(profileFolder);
			props.load(inputStream);
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
			File profileFolder = new File(configFile);
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
	
	/**
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	int getInt(String key, int defaultValue) {
		String valueText = props.getProperty(key, "");
		int value;
		try {
			return Integer.parseInt(valueText);
		}
		catch (NumberFormatException e) {
			value = defaultValue;
		}
		return value;
	}
}
