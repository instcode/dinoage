/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.yahoo.grabber;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.blogging.yahoo.YahooBlog;
import org.ddth.blogging.yahoo.YahooBlogAPI;
import org.ddth.blogging.yahoo.YahooBlogEntry;
import org.ddth.dinoage.core.BrowsingSession;
import org.ddth.dinoage.core.Profile;
import org.ddth.dinoage.core.ProfileFactory;
import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.RequestFuture;
import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.handler.ContentHandler;
import org.ddth.http.impl.content.StreamContent;
import org.ddth.http.impl.content.WebpageContent;

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
	
	private Log logger = LogFactory.getLog(YBrowsingSession.class);
	private YahooProfile profile;

	public YBrowsingSession(YahooProfile profile) {
		super(YahooBlogAPI.YAHOO_360_CONTENT_DISPATCHER);
		this.profile = profile;
	}

	@Override
	protected Request[] getRestorable() {
		return new Request[] { new Request(profile.getRecentURL()) };
	}

	@Override
	public boolean isRestorable() {
		return !profile.isNewlyCreated();
	}
	
	@Override
	public void start() {
		super.start();
		queue(new Request(profile.getStartingURL()));
	}
	
	/**
	 * Check if the result of current request is available locally
	 * 
	 * @param request
	 * @return
	 */
	private File getLocalResource(Request request) {
		YahooProfile yahooProfile = profile;
		return yahooProfile.getLocalResource(request.getParameters());
	}
	
	@Override
	public RequestFuture queue(Request request) {
		if (!isRunning()) {
			return null;
		}
		File resource = getLocalResource(request);
		if (resource != null) {
			Content<?> content = parseForContent(request, resource);
			handle(request, content);
			return null;
		}
		profile.saveRequestingURL(request.getURL());
		return super.queue(request);
	}
	
	@Override
	protected void handle(Request request, Content<?> content) {
		try {
			Request nextRequest = request;
			Content<?> currContent = content;
			while (currContent != null) {
				nextRequest = processContent(nextRequest, currContent);
				if (nextRequest == null || !isRunning()) {
					break;
				}
				File entryFile = getLocalResource(nextRequest);
				if (entryFile == null) {
					queue(nextRequest);
					break;	
				}
				currContent = parseForContent(nextRequest, entryFile);
				File delete = new File(entryFile.getParent(), "delete.me");
				// Delete *delete.me* to make sure the followed renameTo works
				delete.delete();
				entryFile.renameTo(delete);
			}
		}
		catch (Exception e) {
			// Something went wrong or there's nothing left to do...
			logger.debug("Stopping current session...", e);
			shutdown();
		}
	}
	
	private Request processContent(Request request, Content<?> content) {
		Request nextRequest = null;
		if (content instanceof YBlogContent) {
			YBlogContent blogContent = (YBlogContent) content;
			profile.add(blogContent);
			YahooBlog blog = blogContent.getBlog();
			if (blog != null) {
				nextRequest = new Request(blog.getFirstEntryURL());
			}
		}
		else if (content instanceof YBlogEntryContent) {
			YBlogEntryContent blogEntry = (YBlogEntryContent) content;
			YahooBlogEntry entry = blogEntry.getEntry();
			String popupURL = entry.getPopupURL();
			if (popupURL != null && !popupURL.isEmpty()) {
				Request loresPictureRequest = new Request(entry.getImageURL());
				if (getLocalResource(loresPictureRequest) == null) {
					super.queue(loresPictureRequest);
				}
				Request hiresPictureRequest = new Request(popupURL);
				if (getLocalResource(hiresPictureRequest) == null) {
					super.queue(hiresPictureRequest);
				}
			}
			profile.add(blogEntry);
			String nextURL = entry.getNextURL();
			if (nextURL != null && !nextURL.isEmpty()) {
				nextRequest = new Request(nextURL);
			}
		}
		else if (content instanceof YEntryImageContent) {
			nextRequest = new Request(((YEntryImageContent) content).getImageURL());
		}
		else if (content instanceof StreamContent) {
			String imageName = request.getParameters().get("fragment");
			if (imageName != null && imageName.length() > 0) {
				profile.add(imageName, (InputStream)content.getContent());
			}
		}
		return nextRequest;
	}
	
	private Content<?> parseForContent(Request request, File cacheFile) {
		logger.info("Found local resource for " + request.getURL() + " in " + cacheFile);
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(cacheFile);
			WebpageContent webContent = new WebpageContent(inputStream, "utf-8");
			ContentHandler handler = YahooBlogAPI.YAHOO_360_CONTENT_DISPATCHER.findHandler(request);
			return handler.handle(webContent);
		}
		catch (IOException e) {
			logger.debug("Error parsing local resource", e);
		}
		finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				}
				catch (IOException e) {
				}
			}
		}
		return null;
	}
}
