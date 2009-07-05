package org.ddth.dinoage.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.ddth.dinoage.ResourceManager;

public abstract class Profile {

	private static final String PROFILE_NAME = "profile.name";
	private static final String PROFILE_URL = "profile.url";

	
	private File folder;
	private String profileURL;
	private String profileName;

	public void populate(Profile profile) {
		setProfileName(profile.getProfileName());
		setProfileURL(profile.getProfileURL());
	}
	
	protected abstract void load(Properties properties);
	protected abstract void store(Properties properties);

	public void load(File profileFile) throws IOException {
		this.folder = profileFile.getParentFile();
		Properties properties = new Properties();
		properties.load(new FileInputStream(profileFile));
		
		setProfileName(properties.getProperty(PROFILE_NAME, ""));
		setProfileURL(properties.getProperty(PROFILE_URL, ""));
		load(properties);
	}

	public File getFolder() {
		return folder;
	}

	public OutputStream store(File profileFile) throws IOException {
		Properties properties = new Properties();
		
		properties.put(PROFILE_NAME, profileName);
		properties.put(PROFILE_URL, profileURL);
		store(properties);
		
		// Store all properties
		OutputStream outputStream = new FileOutputStream(profileFile);
		properties.store(outputStream, ResourceManager.getMessage(
				ResourceManager.KEY_PROFILE_RESUME_FILE_HEADER, new String [] {profileName, profileURL}));
		return outputStream;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profile) {
		if (profile == null || profile.trim().length() == 0) {
			throw new IllegalArgumentException("Profile cannot be empty!");
		}
		this.profileName = profile.trim();
	}

	public String getProfileURL() {
		return profileURL;
	}

	public void setProfileURL(String profileURL) {
		if (profileURL == null || profileURL.trim().length() == 0) {
			throw new IllegalArgumentException("ProfileURL cannot be empty!");
		}
		this.profileURL = profileURL;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ((obj != null) && (obj instanceof Profile)) {
		    return folder.equals(((Profile)obj).folder);
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return folder.hashCode();
	}
}