/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.yahoo.grabber;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.ddth.blogging.Blog;
import org.ddth.blogging.yahoo.YahooBlog;
import org.ddth.blogging.yahoo.YahooBlogAPI;
import org.ddth.blogging.yahoo.YahooBlogEntry;
import org.ddth.dinoage.core.LocalStorage;
import org.ddth.dinoage.core.ProfileChangeEvent;
import org.ddth.dinoage.core.RequestStorage;
import org.ddth.dinoage.core.SessionProfile;
import org.ddth.dinoage.data.DataManager;
import org.ddth.http.core.connection.Request;

public class YahooProfile extends SessionProfile implements RequestStorage {
	
	private static final String PROFILE_RECENT_URLS = "profile.recent.urls";
	
	private File folder;	
	private YahooPersistence persistence;
	private DataManager manager;
	
	private String profileId;
	private Blog blog;
	private Request[] recentRequests = new Request[] { };

	private String blogURL;
	private String guestbookURL;
	private String friendsURL;

	public YahooProfile() {
	}

	public LocalStorage getLocalStorage() {
		return persistence;
	}

	public String getBlogURL() {
		return blogURL;
	}

	public String getGuestbookURL() {
		return guestbookURL;
	}

	public String getFriendsURL() {
		return friendsURL;
	}

	public File getFolder() {
		return folder;
	}
	
	public File getBlogFolder() {
		return new File(folder, YahooPersistence.BLOG);
	}
	
	@Override
	public void load(File profileFile) throws IOException {
		super.load(profileFile);
		this.folder = profileFile.getParentFile();
		this.persistence = new YahooPersistence(this);
		this.manager = new DataManager(new YRawDataProvider(this));
	}
	
	@Override
	protected void load(Properties properties) {
		String recentURL = properties.getProperty(PROFILE_RECENT_URLS, blogURL);
		if (!recentURL.equals(blogURL)) {
			recentRequests = new Request[] { new Request(recentURL) };
		}
	}

	@Override
	protected void store(Properties properties) {
		if (recentRequests.length > 0) {
			properties.put(PROFILE_RECENT_URLS, recentRequests[0].getURL());
		}
		else {
			properties.put(PROFILE_RECENT_URLS, blogURL);
		}
	}
	
	@Override
	public void setProfileURL(String profileURL) {
		profileId = YahooBlogAPI.parseProfileId(profileURL);
		blogURL = YahooBlogAPI.YAHOO_360_BLOG_URL + profileId;
		guestbookURL = YahooBlogAPI.YAHOO_360_GUESTBOOK_URL + profileId;
		friendsURL = YahooBlogAPI.YAHOO_360_FRIEND_URL + profileId;
		super.setProfileURL(YahooBlogAPI.YAHOO_360_PROFILE_URL + profileId);
	}

	public void add(YahooBlog blog) {
		fireProfileChanged(
				new ProfileChangeEvent(this, blog, ProfileChangeEvent.PROFILE_FIRST_LOADED));
		this.blog = blog;
	}

	public void add(YahooBlogEntry entry) {
		if (blog != null) {
			int type = ProfileChangeEvent.PROFILE_DELTA_CHANGED;
			if (blog.addEntry(entry)) {
				type = ProfileChangeEvent.PROFILE_CHANGED;
			}
			fireProfileChanged(new ProfileChangeEvent(this, entry, type));
		}
	}

	public void loadProfileFromStorage() {
		if (blog != null) {
			fireProfileChanged(new ProfileChangeEvent(
					this, blog, ProfileChangeEvent.PROFILE_FIRST_LOADED));
		}
		else {
			blog = manager.getBlog(profileId);
			manager.getEntries(profileId);
		}
	}

	@Override
	public Request[] getRequests() {
		return recentRequests;
	}
	
	@Override
	public void putRequest(Request request) {
		String url = request.getURL();
		if (url.indexOf(YahooBlogAPI.YAHOO_360_HOST) > 0 && !url.contains("slideshow")) {
			// Only accept url from Yahoo 360 domain & not the slideshow one
			recentRequests = new Request[] { request };
		}
	}
}
