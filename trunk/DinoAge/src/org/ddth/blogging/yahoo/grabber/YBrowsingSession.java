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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.blogging.yahoo.YahooBlog;
import org.ddth.blogging.yahoo.YahooBlogEntry;
import org.ddth.blogging.yahoo.grabber.handler.YBlogEntryContentHandler;
import org.ddth.blogging.yahoo.grabber.handler.YEntryListContentHandler;
import org.ddth.dinoage.core.BrowsingSession;
import org.ddth.dinoage.core.Profile;
import org.ddth.dinoage.core.ProfileFactory;
import org.ddth.dinoage.core.SessionProfile;
import org.ddth.dinoage.core.Workspace;
import org.ddth.http.core.connection.Request;
import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.handler.ContentHandlerDispatcher;

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

	private static final ContentHandlerDispatcher YAHOO_360_CONTENT_DISPATCHER = new ContentHandlerDispatcher();
	static {
		YAHOO_360_CONTENT_DISPATCHER.registerHandler("http://.*/blog-.*\\?.*p=(\\d+).*", new YBlogEntryContentHandler());
		YAHOO_360_CONTENT_DISPATCHER.registerHandler("http://.*/blog-[^?]*", new YEntryListContentHandler());
	}

	public YBrowsingSession(Profile profile, Workspace workspace) {
		super((SessionProfile) profile, workspace, YAHOO_360_CONTENT_DISPATCHER);
	}

	@Override
	protected void content(Content<?> content) {
		String nextURL = null;
		YahooProfile yahooProfile = (YahooProfile) profile;
		try {
			if (content instanceof YBlogContent) {
				YBlogContent blogContent = (YBlogContent) content;
				yahooProfile.save(blogContent);
				YahooBlog blog = blogContent.getBlog();
				if (blog != null) {
					nextURL = blog.getFirstEntryURL();
				}
			} else {
				YBlogEntryContent blogEntry = (YBlogEntryContent) content;
				YahooBlogEntry entry = blogEntry.getEntry();
				nextURL = entry.getNextURL();
				yahooProfile.save(blogEntry);
			}

			if (nextURL != null && nextURL.startsWith("http://")) {
				queue(new Request(nextURL));
			}
			else {
				throw new IllegalStateException("No next URL");
			}
		}
		catch (Exception e) {
			// Something went wrong or there's nothing left to do...
			logger.debug("Stopping current session...", e);
			shutdown();
		}
	}
}
