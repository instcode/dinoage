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
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.ddth.blogging.Blog;
import org.ddth.blogging.yahoo.YahooBlogAPI;
import org.ddth.dinoage.core.DataLoadEvent;
import org.ddth.dinoage.core.DataLoadMonitor;
import org.ddth.dinoage.core.ProfileChangeEvent;
import org.ddth.dinoage.core.SessionProfile;

public class YahooProfile extends SessionProfile implements DataLoadMonitor {
	
	private static final String PROFILE_URLS_BEGINNING = "profile.urls.beginning";
	
	private String profileId;
	private String recentURL;
	private String startingURL;
	private YahooPersistence persistence;
	private Blog blog;
	
	@Override
	public void setProfileURL(String profileURL) {
		profileId = YahooBlogAPI.parseProfileId(profileURL);
		startingURL = YahooBlogAPI.YAHOO_360_BLOG_URL + profileId;
		recentURL = startingURL;
		super.setProfileURL(YahooBlogAPI.YAHOO_360_PROFILE_URL + profileId);
	}

	@Override
	public void load(File profileFile) throws IOException {
		super.load(profileFile);
		persistence = new YahooPersistence(profileFile.getParentFile(), this);
	}
	
	@Override
	protected void load(Properties properties) {
		recentURL = properties.getProperty(PROFILE_URLS_BEGINNING, startingURL);
	}

	@Override
	protected void store(Properties properties) {
		properties.put(PROFILE_URLS_BEGINNING, recentURL);
	}
	
	public boolean isNewlyCreated() {
		return (getStartingURL()).equals(recentURL);
	}

	public String getRecentURL() {
		return recentURL;
	}

	public String getStartingURL() {
		return startingURL;
	}
	
	public void saveRequestingURL(String url) {
		if (url == null || url.isEmpty()) {
			recentURL = getStartingURL();
		}
		else if (url.indexOf(YahooBlogAPI.YAHOO_360_HOST) > 0 && !url.contains("slideshow")) {
			// Only accept url from Yahoo 360 domain & not the slideshow one
			recentURL = url;
		}
		persistence.clean(recentURL);
	}

	public void add(YBlogContent blogContent) {
		if (persistence.save(blogContent)) {
			fireProfileChanged(new ProfileChangeEvent(
				this, blogContent.getBlog(), ProfileChangeEvent.PROFILE_FIRST_LOADED));
		}
		blog = blogContent.getBlog();
	}

	/**
	 * This will save the given blog entry to external memory.
	 * 
	 * @param blogEntry
	 */
	public void add(YBlogEntryContent blogEntry) {
		if (persistence.save(blogEntry)) {
			if (blog != null && blog.addEntry(blogEntry.getEntry())) {
				fireProfileChanged(new ProfileChangeEvent(
						this, blogEntry.getEntry(), ProfileChangeEvent.PROFILE_CHANGED));
			}
		}
	}

	public void loadResourcesFromCache() {
		if (blog != null) {
			fireProfileChanged(new ProfileChangeEvent(
					this, blog, ProfileChangeEvent.PROFILE_FIRST_LOADED));
		}
		else {
			blog = persistence.load(profileId);
		}
	}

	public File getLocalResource(Map<String, String> parameters) {
		return persistence.getResource(parameters);
	}

	public void add(String imageName, InputStream content) {
		persistence.save(imageName, content);
	}

	public void loaded(DataLoadEvent event) {
		switch (event.getType()) {
		case DataLoadEvent.STEP_LOADED:
			Object data = event.getData();
			if (data instanceof Blog) {
				fireProfileChanged(new ProfileChangeEvent(
						this, data, ProfileChangeEvent.PROFILE_FIRST_LOADED));				
			}
			else {
				fireProfileChanged(new ProfileChangeEvent(
						this, data, ProfileChangeEvent.PROFILE_CHANGED));
			}
			break;
		}
	}
}
