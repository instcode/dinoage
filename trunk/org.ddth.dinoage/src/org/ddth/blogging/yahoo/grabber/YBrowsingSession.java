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
import org.ddth.dinoage.core.SessionProfile;
import org.ddth.dinoage.core.Workspace;
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
			return new YahooProfile();
		}
		
		public Profile loadProfile(File profileFile) throws IOException {
			Profile profile = new YahooProfile();
			profile.load(profileFile);
			return profile;
		}
	}
	
	private Log logger = LogFactory.getLog(YBrowsingSession.class);

	public YBrowsingSession(Profile profile, Workspace workspace) {
		super((SessionProfile) profile, workspace, YahooBlogAPI.YAHOO_360_CONTENT_DISPATCHER);
	}

	/**
	 * Check if the result of current request is available locally
	 * 
	 * @param request
	 * @return
	 */
	private File getLocalResource(Request request) {
		YahooProfile yahooProfile = (YahooProfile) profile;
		return yahooProfile.getLocalResource(request.getParameters());
	}
	
	@Override
	public RequestFuture queue(Request request) {
		File resource = getLocalResource(request);
		if (resource != null) {
			Content<?> content = parseForContent(request, resource);
			handle(request, content);
			return null;
		}
		return super.queue(request);
	}
	
	@Override
	protected void handle(Request request, Content<?> content) {
		try {
			Request nextRequest = request;
			Content<?> currContent = content;
			while (currContent != null) {
				String nextURL = processContent(nextRequest, currContent);
				if (nextURL == null || nextURL.isEmpty()) {
					break;
				}
				nextRequest = new Request(nextURL);
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
	
	private String processContent(Request request, Content<?> content) {
		String nextURL = null;
		YahooProfile yahooProfile = (YahooProfile) profile;			
		if (content instanceof YBlogContent) {
			YBlogContent blogContent = (YBlogContent) content;
			yahooProfile.add(blogContent);
			YahooBlog blog = blogContent.getBlog();
			if (blog != null) {
				nextURL = blog.getFirstEntryURL();
			}
		}
		else if (content instanceof YBlogEntryContent) {
			YBlogEntryContent blogEntry = (YBlogEntryContent) content;
			YahooBlogEntry entry = blogEntry.getEntry();
			String popupURL = entry.getPopupURL();
			if (popupURL != null && !popupURL.isEmpty()) {
				Request loresPictureRequest = new Request(entry.getImageURL());
				if (getLocalResource(loresPictureRequest) == null) {
					queue(loresPictureRequest);
				}
				Request hiresPictureRequest = new Request(popupURL);
				if (getLocalResource(hiresPictureRequest) == null) {
					queue(hiresPictureRequest);
				}
			}
			nextURL = entry.getNextURL();
			yahooProfile.add(blogEntry);
		}
		else if (content instanceof YEntryImageContent) {
			nextURL = ((YEntryImageContent) content).getImageURL();
		}
		else if (content instanceof StreamContent) {
			String imageName = request.getParameters().get("fragment");
			yahooProfile.add(imageName, (InputStream)content.getContent());
		}
		return nextURL;
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
