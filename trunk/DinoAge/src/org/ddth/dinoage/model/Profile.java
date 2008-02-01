/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.grabber.yahoo.ContentHandlerFactory;
import org.ddth.grabber.core.connection.Session;
import org.ddth.grabber.core.connection.State;
import org.ddth.grabber.core.handler.NavigationHandler;

public class Profile implements State {
	private static final String BACKUP_URLS_OUTGOING = "backup.urls.outgoing";
	private static final String BACKUP_GUESTBOOK_INDEX = "backup.guestbook.index";
	private static final String BACKUP_ENTRY_INDEX = "backup.entry.index";
	private static final String BACKUP_GUESTBOOK_ENABLE = "backup.guestbook.enable";
	private static final String BACKUP_ENTRY_ENABLE = "backup.entry.enable";
	private static final String PROFILE_URL = "profile.url";
	private static final String PROFILE_NAME = "profile.name";
	private static final String BACKUP_URLS_COMPLETED = "backup.urls.completed";

	private Log logger = LogFactory.getLog(Profile.class);
	
	private Map<String, Boolean> completedURLMap = new ConcurrentHashMap<String, Boolean>();
	private Map<String, NavigationHandler> outgoingURLMap = new ConcurrentHashMap<String, NavigationHandler>();
	private String profileId;
	private String profileURL;
	private String profileName;
	private boolean isBackupGuestbook;
	private boolean isBackupEntry;
	private Persistence persistence;

	public void initialize(Persistence persistence) {
		this.persistence = persistence;
	}
	
	public static String getProfileId(String profileURL) {
		int begin = ResourceManager.KEY_PROFILE_URL.length();
		int end = profileURL.indexOf("?", begin);
		end = (end == -1) ? profileURL.length() : end;
		return profileURL.substring(begin, end);
	}
	
	public void load(File profileFile, Session session) throws IOException {
		File resumeFile = new File(profileFile, ResourceManager.RESUME_FILE_NAME);
		Properties properties = new Properties();
		properties.load(new FileInputStream(resumeFile));
		setProfile(properties.getProperty(PROFILE_NAME, ""));
		setProfileURL(properties.getProperty(PROFILE_URL, ""));
		setBackupEntry(Boolean.parseBoolean(properties.getProperty(BACKUP_ENTRY_ENABLE, "false")));
		setBackupGuestbook(Boolean.parseBoolean(properties.getProperty(BACKUP_GUESTBOOK_ENABLE, "false")));
		
		int entry = Integer.parseInt(properties.getProperty(BACKUP_ENTRY_INDEX, "0"));
		int guestbook = Integer.parseInt(properties.getProperty(BACKUP_GUESTBOOK_INDEX, "0"));
		initialize(new Persistence(resumeFile, new int[] {entry, guestbook}));
		
		String sCompletedURLs = properties.getProperty(BACKUP_URLS_COMPLETED, "");
		String[] completedURLs = sCompletedURLs.split(",");
		for (String completedURL : completedURLs) {
			completedURLMap.put(completedURL, Boolean.TRUE);
		}
		
		String sOutgoingURLs = properties.getProperty(BACKUP_URLS_OUTGOING, "");
		String[] outgoingURLs = sOutgoingURLs.split(",");
		
		for (String outgoingURL : outgoingURLs) {
			NavigationHandler contentHandler = ContentHandlerFactory.getInstance().createContentHandler(
					outgoingURL, persistence, session);
			if (contentHandler != null) {
				outgoingURLMap.put(outgoingURL, contentHandler);
			}
			else {
				logger.error(ResourceManager.getMessage(
							"Cannot create handler for outgoing {0} url. Resume information might be corrupt.",
								new String[] {outgoingURL}));
			}
		}
	}

	public void store(OutputStream outputStream) throws IOException {
		Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
		writer.write(ResourceManager.getMessage(
				ResourceManager.KEY_PROFILE_RESUME_FILE_HEADER, new String [] {profileName, profileURL}));

		writer.write("\n# Profile information\n");
		writer.write(PROFILE_NAME + "=" + profileName + "\n");
		writer.write(PROFILE_URL + "=" + profileURL + "\n");
		writer.write("\n# State information\n");
		writer.write(BACKUP_ENTRY_ENABLE + "=" + isBackupEntry + "\n");
		writer.write(BACKUP_ENTRY_INDEX + "=" + persistence.getCategoryIndex(Persistence.BLOG_ENTRY) + "\n");
		writer.write(BACKUP_GUESTBOOK_ENABLE + "=" + isBackupGuestbook + "\n");
		writer.write(BACKUP_GUESTBOOK_INDEX + "=" + persistence.getCategoryIndex(Persistence.GUESTBOOK) + "\n");
		writer.write("\n# Completed URLs\n");
		writer.write(BACKUP_URLS_COMPLETED + "=\\\n");
		Iterator<String> completedURLs = completedURLMap.keySet().iterator();
		while (completedURLs.hasNext()) {
			String sURL = completedURLs.next();
			// Only "real" completed URL
			if (completedURLMap.get(sURL).booleanValue()) {
				writer.write("\t" + sURL + (completedURLs.hasNext() ? ",\\\n" : "\n"));
			}
		}
		writer.write("\n# Outgoing URLs\n");
		writer.write(BACKUP_URLS_OUTGOING + "=\\\n");
		Iterator<String> outgoingURLs = outgoingURLMap.keySet().iterator();
		while (outgoingURLs.hasNext()) {
			String sURL = outgoingURLs.next();
			writer.write("\t" + sURL + (outgoingURLs.hasNext() ? ",\\\n" : "\n"));
		}
		writer.flush();
	}

	public Map<String, Boolean> getCompletedMap() {
		return completedURLMap;
	}

	public Map<String, NavigationHandler> getOutgoingMap() {
		return outgoingURLMap;
	}

	public void setProfile(String profile) {
		this.profileName = profile;
	}

	public String getProfile() {
		return profileName;
	}

	public boolean isNewlyCreated() {
		return (persistence == null);
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

	public String getProfileURL() {
		return profileURL;
	}

	public void setProfileURL(String profileURL) {
		this.profileURL = profileURL;
		this.profileId = getProfileId(profileURL);
	}

	public String getProfileId() {
		return profileId;
	}
}
