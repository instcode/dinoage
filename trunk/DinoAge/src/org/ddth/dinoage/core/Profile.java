package org.ddth.dinoage.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.ddth.dinoage.ResourceManager;

public abstract class Profile {

	private static final String PROFILE_NAME = "profile.name";
	private static final String PROFILE_URL = "profile.url";

	private String profileURL;
	private String profileName;
	private List<ProfileChangeListener> listeners = new ArrayList<ProfileChangeListener>();

	public void populate(Profile profile) {
		setProfileName(profile.getProfileName());
		setProfileURL(profile.getProfileURL());
	}
	
	protected abstract void innerLoad(Properties properties);
	protected abstract void innerStore(Properties properties);

	public void addProfileChangeListener(ProfileChangeListener listener) {
		listeners.add(listener);
	}
	
	public boolean removeProfileChangeListener(ProfileChangeListener listener) {
		return listeners.remove(listener);
	}
	
	protected void fireProfileChanged(ProfileChangeEvent event) {
		for (ProfileChangeListener listener : listeners) {
			listener.profileChanged(event);
		}
	}
	
	public void load(File profileFile) throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(profileFile));
		
		setProfileName(properties.getProperty(PROFILE_NAME, ""));
		setProfileURL(properties.getProperty(PROFILE_URL, ""));
		innerLoad(properties);
	}

	public OutputStream store(File profileFile) throws IOException {
		Properties properties = new Properties();
		
		properties.put(PROFILE_NAME, profileName);
		properties.put(PROFILE_URL, profileURL);
		innerStore(properties);
		
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
}