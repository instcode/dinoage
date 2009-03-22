/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.blogging.yahoo.YahooBlog;
import org.ddth.dinoage.model.Persistence;
import org.ddth.dinoage.model.Profile;
import org.ddth.dinoage.model.Workspace;
import org.ddth.http.core.ConnectionEvent;
import org.ddth.http.core.ConnectionListener;
import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.RequestFuture;
import org.ddth.http.core.content.handler.ContentHandlerDispatcher;
import org.ddth.http.impl.ThreadPoolSession;

public class YBrowsingSession extends ThreadPoolSession {
	private Log logger = LogFactory.getLog(YBrowsingSession.class);
	
	/**
	 * The boolean value is used to mark whether a URL is completed or not
	 */
	private Map<String, Boolean> requestMap = new ConcurrentHashMap<String, Boolean>();

	private String profileId;
	private Profile profile;
	private Persistence persistence;
	private Workspace workspace;
	private ConnectionListener listener;

	public YBrowsingSession(Profile profile, Workspace workspace, ConnectionListener listener, ContentHandlerDispatcher dispatcher) {
		super(dispatcher);
		this.workspace = workspace;
		this.listener = listener;
		this.profile = profile;
		this.profileId = getProfileId(profile.getProfileURL());
		this.persistence = new Persistence(workspace.getProfileFolder(profile));
		
		String[] completedURLs = profile.getCompletedURLs();
		for (String completedURL : completedURLs) {
			requestMap.put(completedURL, Boolean.TRUE);
		}
		String[] outgoingURLs = profile.getOutgoingURLs();
		for (String outgoingURL : outgoingURLs) {
			requestMap.put(outgoingURL, Boolean.FALSE);
		}
	}
	
	@Override
	public void start() {
		super.start();		
		queue(new Request(YahooBlog.YAHOO_360_BLOG_URL + profileId));
		queue(new Request(YahooBlog.YAHOO_360_GUESTBOOK_URL + profileId));
		
		String[] outgoingURLs = profile.getOutgoingURLs();
		for (String outgoingURL : outgoingURLs) {
			queue(new Request(outgoingURL));
		}
		workspace.saveProfile(profile);
	}

	public void save(InputStream inputStream, int category, String tail) {
		persistence.write(inputStream, category, tail);
	}

	public void reset() {
		requestMap.clear();
	}

	public boolean isNewlyCreated() {
		return (requestMap.size() == 0);
	}

	public String getProfileId() {
		return profileId;
	}

	@Override
	public void notifyFinished(ConnectionEvent event) {
		logger.debug(event.getRequest().getURL() + "... Done!");
		requestMap.put(event.getRequest().getURL(), Boolean.TRUE);
		syncState();
		workspace.saveProfile(profile);
		listener.notifyFinished(event);
	}

	@Override
	public void notifyRequesting(ConnectionEvent event) {
		logger.debug("Requesting: " + event.getRequest().getURL() + "...");
		listener.notifyRequesting(event);
	}

	@Override
	public void notifyResponding(ConnectionEvent event) {
		listener.notifyResponding(event);
	}

	@Override
	public RequestFuture queue(Request request) {
		RequestFuture future = null;
		
		String sURL = request.getURL();
		if (!requestMap.containsKey(sURL)) {
			requestMap.put(sURL, Boolean.FALSE);
			future = super.queue(request);
		}
		return future;
	}

	/**
	 * Synchronize between the state and its profile 
	 */
	private void syncState() {
		List<String> completedURLs = new ArrayList<String>();
		List<String> outgoingURLs = new ArrayList<String>();
		Iterator<String> iterator = requestMap.keySet().iterator();
		while (iterator.hasNext()) {
			String sURL = iterator.next();
			Boolean requested = requestMap.get(sURL);
			if (requested.booleanValue()) {
				completedURLs.add(sURL);
			}
			else {
				outgoingURLs.add(sURL);
			}
		}
		profile.setCompletedURLs(completedURLs.toArray(new String[completedURLs.size()]));
		profile.setOutgoingURLs(outgoingURLs.toArray(new String[outgoingURLs.size()]));
	}

	private String getProfileId(String profileURL) {
		int begin = YahooBlog.YAHOO_360_PROFILE_URL.length();
		int end = profileURL.indexOf("?", begin);
		end = (end < 0) ? profileURL.length() : end;
		return end <= begin ? "" : profileURL.substring(begin, end);
	}
}
