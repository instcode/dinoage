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

import org.ddth.blogging.yahoo.YahooBlog;
import org.ddth.blogging.yahoo.YahooBlogAPI;
import org.ddth.blogging.yahoo.YahooBlogEntry;
import org.ddth.dinoage.core.BrowsingSession;
import org.ddth.dinoage.core.LocalStorage;
import org.ddth.dinoage.core.Profile;
import org.ddth.dinoage.core.ProfileFactory;
import org.ddth.http.core.connection.Request;
import org.ddth.http.core.content.Content;

/**
 * @author khoa.nguyen
 *
 */
public class YBrowsingSession extends BrowsingSession {
	
	public static class YProfileLoader implements ProfileFactory { 
		public Profile createProfile() {
			YahooProfile profile = new YahooProfile();
			profile.setProfileURL(YahooBlogAPI.YAHOO_360_PROFILE_URL);
			return profile;
		}
		
		public Profile loadProfile(File profileFile) throws IOException {
			Profile profile = new YahooProfile();
			profile.load(profileFile);
			return profile;
		}
	}

	private YahooProfile profile;

	public YBrowsingSession(YahooProfile profile) {
		super(YahooBlogAPI.YAHOO_360_CONTENT_DISPATCHER, profile.getLocalStorage(), profile);
		this.profile = profile;
	}

	@Override
	public void start() {
		super.start();
		consoleLogger.println("Session started.");
		queue(new Request(profile.getBlogURL()));
		queue(new Request(profile.getProfileURL()));
		queue(new Request(profile.getFriendsURL()));
	}

	protected void process(Content<?> content) {
		String requestURL = null;
		if (content instanceof YBlogContent) {
			YBlogContent blogContent = (YBlogContent) content;
			YahooBlog blog = blogContent.getBlog();
			profile.add(blog);
			if (blog != null) {
				Request avatarRequest = new Request(blog.getAuthor().getAvatar());
				avatarRequest.getParameters().put("__image_path__", "avatar.jpg");
				queue(avatarRequest);
				requestURL = blog.getFirstEntryURL();
			}
		}
		else if (content instanceof YBlogEntryContent) {
			YBlogEntryContent blogEntry = (YBlogEntryContent) content;
			YahooBlogEntry entry = blogEntry.getEntry();
			String popupURL = entry.getPopupURL();
			if (popupURL != null && !popupURL.isEmpty()) {
				// Low resolution image
				Request loresImageRequest = new Request(entry.getImageURL());
				loresImageRequest.getParameters().put("__image_path__", "lores_" + entry.getEntryId() + ".jpg");
				queue(loresImageRequest);
				// High resolution image
				Request popupHiresImageRequest = new Request(popupURL);
				popupHiresImageRequest.getParameters().put("__popup_path__", "hires_" + entry.getEntryId() + ".html");
				queue(popupHiresImageRequest);
			}
			// A guestbook entry without any comment is indeed invalid.
			// This would be caused by session protection mechanism of
			// Yahoo. We shouldn't save this content to file because it
			// will be supposed of being a valid cache and prohibit the
			// browsing session from retrieving the expected content.
			if (entry.getEntryId() == 0 && entry.getComments().size() == 0) {
				Request request = new Request(entry.getNextURL());
				request.getParameters().put(LocalStorage.RESOURCE_EXPIRED_ATTR, "");
				queue(request);
			}
			else {
				profile.add(entry);
				requestURL = entry.getNextURL();
			}
		}
		else if (content instanceof YEntryImageContent) {
			YEntryImageContent entryImageContent = (YEntryImageContent) content;
			Request request = new Request(entryImageContent.getImageURL());
			request.getParameters().put("__image_path__", "hires_" + entryImageContent.getPostId() + ".jpg");
			queue(request);
		}
		if (requestURL != null && !requestURL.isEmpty()) {
			queue(new Request(requestURL));
		}
	}
	
}
