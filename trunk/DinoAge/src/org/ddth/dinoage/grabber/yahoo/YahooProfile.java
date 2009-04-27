/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo;

import java.util.Properties;

import org.ddth.blogging.yahoo.YahooBlog;
import org.ddth.dinoage.model.Profile;

public class YahooProfile extends Profile {
	
	private static final String PROFILE_URLS_BEGINNING = "profile.urls.beginning";
	
	private String profileId;
	private String beginningURL;
	private boolean isNewlyCreated = true;
	
	@Override
	public void setProfileURL(String profileURL) {
		super.setProfileURL(profileURL);
		this.profileId = getProfileId(getProfileURL());
		this.beginningURL = YahooBlog.YAHOO_360_BLOG_URL + getProfileId();
	}

	@Override
	protected void innerLoad(Properties properties) {
		beginningURL = properties.getProperty(PROFILE_URLS_BEGINNING, YahooBlog.YAHOO_360_BLOG_URL + getProfileId());
		if (beginningURL.trim().length() == 0) {
			beginningURL = YahooBlog.YAHOO_360_BLOG_URL + getProfileId();
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
	
	public String getProfileId() {
		return profileId;
	}

	public String getBeginningURL() {
		return beginningURL;
	}
	
	public void saveURL(String url) {
		if (url == null) {
			return;
		}
		beginningURL = url;
		isNewlyCreated = false;
	}
	
	private final String getProfileId(String profileURL) {
		int begin = YahooBlog.YAHOO_360_PROFILE_URL.length();
		int end = profileURL.indexOf("?", begin);
		end = (end < 0) ? profileURL.length() : end;
		return end <= begin ? "" : profileURL.substring(begin, end);
	}
}
