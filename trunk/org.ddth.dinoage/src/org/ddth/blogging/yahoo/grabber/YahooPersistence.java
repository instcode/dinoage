/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 1, 2008 8:23:13 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.yahoo.grabber;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.ddth.dinoage.core.LocalStorage;
import org.ddth.dinoage.core.Persistence;
import org.ddth.http.core.connection.Request;
import org.ddth.http.core.content.Content;

public class YahooPersistence extends Persistence implements LocalStorage {
	public static final String SUFFIX_LIST = "list";
	public static final String BLOG = "blog";
	public static final String ENTRY = "entry";
	public static final String GUESTBOOK = "guestbook";
	private static final String PROFILE = "profile";
	private static final String FRIENDS = "friends";
	
	private YahooProfile profile;
	
	public YahooPersistence(YahooProfile profile) {
		this.profile = profile;
	}

	@Override
	public void cacheResource(Request request, Content<?> content) {
		if (!request.getParameters().containsKey(LocalStorage.NO_CACHING_ATTR)) {
			write((InputStream)content.getContent(), getResource(request));
		}
	}

	@Override
	public File getLocalResource(Request request) {
		if (request.getParameters().containsKey(LocalStorage.RESOURCE_EXPIRED_ATTR)) {
			return null;
		}
		File resource = getResource(request);
		return resource.exists() ? resource : null;
	}
	
	private File getResource(Request request) {
		String resource = null;
		if (request.getURL().startsWith(profile.getProfileURL())) {
			resource = PROFILE + ".html";
		}
		else if (request.getURL().equals(profile.getBlogURL())) {
			resource = ENTRY + "-" + SUFFIX_LIST + ".html";
		}
		else if (request.getURL().equals(profile.getGuestbookURL())) {
			resource = GUESTBOOK + "-" + SUFFIX_LIST + ".html";
		}
		else if (request.getURL().equals(profile.getFriendsURL())) {
			resource = FRIENDS + "-" + SUFFIX_LIST + ".html";
		}
		else {
			Map<String, String> parameters = request.getParameters();
			resource = getResource(parameters);
		}
		return new File(profile.getFolder(), BLOG + File.separatorChar + resource);
	}

	private String getResource(Map<String, String> parameters) {
		String resource;
		String postId = parameters.get("p");
		String low = parameters.get("l");
		String max = parameters.get("mx");
		String popupPath = parameters.get("__popup_path__");
		String imagePath = parameters.get("__image_path__");

		if (popupPath != null) {
			resource = popupPath;
		}
		else if (imagePath != null) {
			resource = imagePath;
		}
		else {
			// Assume single blog page...
			resource = ENTRY + "-" + postId + ".html";
			// Check if it's actually in multi-pages
			if (low != null) {
				if (postId == null) {
					// 10 guestbook comments per page
					int index = (Integer.parseInt(max) - Integer.parseInt(low) + 1) / 10;
					resource = GUESTBOOK + "-" + String.valueOf(index) + ".html";
				}
				else {
					// 50 entry comments per page
					int index = Integer.parseInt(low) / 50;
					resource = ENTRY + "-" + postId + "-" + String.valueOf(index) + ".html";
				}
			}
		}
		return resource;
	}
}
