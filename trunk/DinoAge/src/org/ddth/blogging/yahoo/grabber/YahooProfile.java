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
import org.ddth.blogging.yahoo.YahooBlogAPI;
import org.ddth.dinoage.core.DataLoadEvent;
import org.ddth.dinoage.core.ProfileChangeEvent;
import org.ddth.dinoage.core.DataLoadMonitor;
import org.ddth.dinoage.core.SessionProfile;

public class YahooProfile extends SessionProfile implements DataLoadMonitor {
	
	private static final String PROFILE_URLS_BEGINNING = "profile.urls.beginning";
	
	private String profileId;
	private String beginningURL;
	private YahooPersistence persistence;
	private Blog blog;

	@Override
	public void setProfileURL(String profileURL) {
		profileId = YahooBlogAPI.parseProfileId(profileURL);
		beginningURL = YahooBlogAPI.YAHOO_360_BLOG_URL + profileId;
		super.setProfileURL(YahooBlogAPI.YAHOO_360_PROFILE_URL + profileId);
	}

	@Override
	public void load(File profileFile) throws IOException {
		super.load(profileFile);
		persistence = new YahooPersistence(profileFile, this);
	}
	
	@Override
	protected void load(Properties properties) {
		saveURL(properties.getProperty(PROFILE_URLS_BEGINNING));
	}

	@Override
	protected void store(Properties properties) {
		properties.put(PROFILE_URLS_BEGINNING, beginningURL);
	}
	
	public boolean isNewlyCreated() {
		return (YahooBlogAPI.YAHOO_360_BLOG_URL + profileId).equals(beginningURL);
	}

	public String getBeginningURL() {
		return beginningURL;
	}
	
	public void saveURL(String url) {
		if (url == null || !beginningURL.startsWith("http://")) {
			beginningURL = YahooBlogAPI.YAHOO_360_BLOG_URL + profileId;
			return;
		}
		beginningURL = url;
	}

	public void save(YBlogContent blogContent) {
		if (persistence.save(blogContent)) {
			fireProfileChanged(new ProfileChangeEvent(
				this, blogContent.getBlog(), ProfileChangeEvent.PROFILE_FIRST_LOADED));
		}
		blog = blogContent.getBlog();
	}

	public void save(YBlogEntryContent blogEntry) {
		if (persistence.save(blogEntry)) {
			fireProfileChanged(new ProfileChangeEvent(
				this, blogEntry.getEntry(), ProfileChangeEvent.PROFILE_CHANGED));
		}
		if (blog != null) {
			blog.addEntry(blogEntry.getEntry());
		}
	}
	
	public void load() {
		if (blog != null) {
			fireProfileChanged(new ProfileChangeEvent(
					this, blog, ProfileChangeEvent.PROFILE_FIRST_LOADED));
		}
		else {
			blog = persistence.load(profileId);
		}
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
