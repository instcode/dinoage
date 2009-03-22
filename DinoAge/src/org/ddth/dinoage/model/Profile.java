package org.ddth.dinoage.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.ddth.dinoage.ResourceManager;

public class Profile {

	private static final String URL_SEPARATOR = ",";
	private static final String BACKUP_URLS_COMPLETED = "backup.urls.completed";
	private static final String BACKUP_URLS_OUTGOING = "backup.urls.outgoing";
	private static final String BACKUP_GUESTBOOK_ENABLE = "backup.guestbook.enable";
	private static final String BACKUP_ENTRY_ENABLE = "backup.entry.enable";
	private static final String PROFILE_URL = "profile.url";
	private static final String PROFILE_NAME = "profile.name";
	
	private String profileURL;
	private String profileName;
	private boolean isBackupGuestbook;
	private boolean isBackupEntry;
	private String[] outgoingURLs = new String[0];
	private String[] completedURLs = new String[0];

	public void populate(Profile profile) {
		setProfileName(profile.getProfileName());
		setProfileURL(profile.getProfileURL());
		setBackupEntry(profile.isBackupEntry());
		setBackupGuestbook(profile.isBackupGuestbook());
		setCompletedURLs(profile.getCompletedURLs());
		setOutgoingURLs(profile.getOutgoingURLs());
	}
	
	public void load(File profileFile) throws IOException {
		InputStream inputStream = new FileInputStream(profileFile);
		
		Properties properties = new Properties();
		properties.load(inputStream);
		
		setProfileName(properties.getProperty(PROFILE_NAME, ""));
		setProfileURL(properties.getProperty(PROFILE_URL, ""));
		setBackupEntry(Boolean.parseBoolean(properties.getProperty(BACKUP_ENTRY_ENABLE, "false")));
		setBackupGuestbook(Boolean.parseBoolean(properties.getProperty(BACKUP_GUESTBOOK_ENABLE, "false")));
		
		String[] completedURLs = parseForURLs(properties.getProperty(BACKUP_URLS_COMPLETED, ""));
		setCompletedURLs(completedURLs);
		
		String[] outgoingURLs = parseForURLs(properties.getProperty(BACKUP_URLS_OUTGOING, ""));
		setOutgoingURLs(outgoingURLs);
	}

	private String[] parseForURLs(String value) {
		if (value == null || value.trim().length() == 0) {
			return new String[0];
		}
		return value.split(URL_SEPARATOR);
	}
	
	public OutputStream store(File profileFile) throws IOException {
		OutputStream outputStream = new FileOutputStream(profileFile);
		
		Properties properties = new Properties();
		properties.put(PROFILE_NAME, profileName);
		properties.put(PROFILE_URL, profileURL);
		properties.put(BACKUP_ENTRY_ENABLE, String.valueOf(isBackupEntry));
		properties.put(BACKUP_GUESTBOOK_ENABLE, String.valueOf(isBackupGuestbook));

		if (completedURLs.length > 0) {
			StringBuilder completedURL = new StringBuilder(completedURLs.length * 16);
			for (int i = 0; i < completedURLs.length; i++) {
				String sURL = completedURLs[i];
				completedURL.append(sURL + (i < completedURLs.length - 1 ? URL_SEPARATOR : ""));
			}
			properties.put(BACKUP_URLS_COMPLETED, completedURL.toString());
		}
		
		if (outgoingURLs.length > 0) {
			StringBuilder outgoingURL = new StringBuilder(outgoingURLs.length * 16);
			for (int i = 0; i < outgoingURLs.length; i++) {
				String sURL = outgoingURLs[i];
				outgoingURL.append(sURL + (i < outgoingURLs.length - 1 ? URL_SEPARATOR : ""));
			}
			properties.put(BACKUP_URLS_OUTGOING, outgoingURL.toString());
		}
		
		// Store all properties
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

	public void setBackupEntry(boolean isBackupEntry) {
		this.isBackupEntry = isBackupEntry;
	}

	public void setBackupGuestbook(boolean isBackupGuestbook) {
		this.isBackupGuestbook = isBackupGuestbook;
	}

	public boolean isBackupEntry() {
		return isBackupEntry;
	}

	public boolean isBackupGuestbook() {
		return isBackupGuestbook;
	}

	public String[] getOutgoingURLs() {
		return outgoingURLs;
	}
	
	public void setOutgoingURLs(String[] outgoingURLs) {
		this.outgoingURLs = outgoingURLs;
	}
	
	public String[] getCompletedURLs() {
		return completedURLs;
	}
	
	public void setCompletedURLs(String[] completedURLs) {
		this.completedURLs = completedURLs;
	}
}