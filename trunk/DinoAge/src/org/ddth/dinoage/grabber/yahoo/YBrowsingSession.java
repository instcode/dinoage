/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.dinoage.model.Profile;
import org.ddth.dinoage.model.Workspace;
import org.ddth.dinoage.model.Persistence;
import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.RequestFuture;
import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.handler.ContentHandlerDispatcher;
import org.ddth.http.impl.ThreadPoolSession;
import org.ddth.http.impl.content.NavigationContent;

/**
 * @author khoa.nguyen
 *
 */
public class YBrowsingSession extends ThreadPoolSession {
	private static final int BLOG_ENTRY = 0;
	private static final String[] CATEGORIES = {"entry"};
	
	private Log logger = LogFactory.getLog(YBrowsingSession.class);

	private Map<String, RequestFuture> requests = new ConcurrentHashMap<String, RequestFuture>();

	private YahooProfile profile;
	private Persistence persistence;
	private Workspace workspace;

	public YBrowsingSession(Profile profile, Workspace workspace, ContentHandlerDispatcher dispatcher) {
		super(dispatcher);
		this.workspace = workspace;
		this.profile = (YahooProfile)profile;
		this.persistence = new Persistence(workspace.getProfileFolder(profile), CATEGORIES);
	}

	/**
	 * 
	 */
	public void reset() {
		requests.clear();
	}

	/**
	 * @return
	 */
	public YahooProfile getProfile() {
		return profile;
	}
	
	@Override
	public void start() {
		super.start();
		queue(new Request(profile.getBeginningURL()));
		workspace.saveProfile(profile);
	}

	@Override
	public RequestFuture queue(Request request) {
		RequestFuture future = null;
		String sURL = request.getURL();
		if (sURL != null && !requests.containsKey(sURL)) {
			future = super.queue(request);
			requests.put(sURL, future);
		}
		return future;
	}

	@Override
	protected void content(Content<?> content) {
		String nextURL = null;
		if (content instanceof NavigationContent) {
			NavigationContent navigationContent = (NavigationContent)content;
			String[] urls = navigationContent.getNextURLs();
			nextURL = urls.length > 0 ? urls[0] : null;
			persistence.write(navigationContent.getContent().getContent(), BLOG_ENTRY, "list");
		}
		else {
			YBlogEntryContent blogEntry = (YBlogEntryContent) content;
			nextURL = blogEntry.getEntry().getNextURL();
			persistence.write(
					blogEntry.getContent().getContent(),
					BLOG_ENTRY,
					String.valueOf(blogEntry.getEntry().getPost().getPostId()));
		}
		if (nextURL != null && nextURL.startsWith("http://")) {
			queue(new Request(nextURL));
			profile.saveURL(nextURL);
			workspace.saveProfile(profile);
		}
		else {
			// Something went wrong or there's nothing left to do...
			logger.debug("Stopping current session...");
			shutdown();
		}
	}
}
