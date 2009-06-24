/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.yahoo.grabber;

import java.util.Properties;

import org.ddth.blogging.Blog;
import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;
import org.ddth.blogging.yahoo.YahooBlogAPI;
import org.ddth.dinoage.core.SessionProfile;
import org.ddth.dinoage.data.DataManager;

public class YahooProfile extends SessionProfile {
	
	private static final String PROFILE_URLS_BEGINNING = "profile.urls.beginning";
	
	private String profileId;
	private String beginningURL;
	private boolean isNewlyCreated = true;

	private DataManager manager = new DataManager();
	private Blog blog;
	
	@Override
	public void setProfileURL(String profileURL) {
		super.setProfileURL(profileURL);
		this.profileId = getProfileId(getProfileURL());
		this.beginningURL = YahooBlogAPI.YAHOO_360_BLOG_URL + profileId;
	}

	@Override
	protected void innerLoad(Properties properties) {
		beginningURL = properties.getProperty(PROFILE_URLS_BEGINNING);
		if (!beginningURL.startsWith("http://")) {
			saveURL(null);
		}
		isNewlyCreated = false;
	}

	@Override
	protected void innerStore(Properties properties) {
		properties.put(PROFILE_URLS_BEGINNING, beginningURL);
	}
	
	public boolean isNewlyCreated() {
		return isNewlyCreated;
	}

	public String getBeginningURL() {
		return beginningURL;
	}
	
	public void saveURL(String url) {
		if (url == null) {
			beginningURL = YahooBlogAPI.YAHOO_360_BLOG_URL + profileId;
			return;
		}
		beginningURL = url;
		isNewlyCreated = false;
	}
	
	private final String getProfileId(String profileURL) {
		int begin = YahooBlogAPI.YAHOO_360_PROFILE_URL.length();
		int end = profileURL.indexOf("?", begin);
		end = (end < 0) ? profileURL.length() : end;
		return end <= begin ? "" : profileURL.substring(begin, end);
	}
	
	public void saveBlog(Blog blog) {
		this.blog = blog;
		manager.createAuthor(blog.getAuthors().get(0));
		manager.createBlog(blog);
	}
	
	public void saveEntry(Entry entry) {
		manager.createEntry(blog.getBlogId(), entry);
		for (Comment comment : entry.getComments()) {
			manager.createComment(entry.getEntryId(), comment);
		}
	}
}
