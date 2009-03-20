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
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.blogging.yahoo.YahooBlog;
import org.ddth.dinoage.model.Persistence;
import org.ddth.dinoage.model.Profile;
import org.ddth.http.core.ConnectionEvent;
import org.ddth.http.core.ConnectionListener;
import org.ddth.http.core.Session;
import org.ddth.http.core.connection.Request;

public class YBackupState implements ConnectionListener {
	private Log logger = LogFactory.getLog(YBackupState.class);
	
	/**
	 * The boolean value is used to mark whether a URL is completed or not
	 */
	private Map<String, Boolean> requestMap = new ConcurrentHashMap<String, Boolean>();
	private Queue<Request> queue = new ConcurrentLinkedQueue<Request>();

	private String profileId;
	private Profile profile;
	private Persistence persistence;

	public YBackupState(Profile profile, Persistence persistence) {
		this.profile = profile;
		this.profileId = getProfileId(profile.getProfileURL());
		this.persistence = persistence;
		
		String[] completedURLs = profile.getCompletedURLs();
		for (String completedURL : completedURLs) {
			requestMap.put(completedURL, Boolean.TRUE);
		}
		String[] outgoingURLs = profile.getOutgoingURLs();
		for (String outgoingURL : outgoingURLs) {
			requestMap.put(outgoingURL, Boolean.FALSE);
		}
	}
	
	public void initialize(Session session) {
		String[] outgoingURLs = profile.getOutgoingURLs();
		for (String outgoingURL : outgoingURLs) {
			session.queue(new Request(outgoingURL));
		}
	}

	public Profile getProfile() {
		return profile;
	}
	
	public void save(InputStream inputStream, int category, String tail) {
		persistence.write(inputStream, category, tail);
	}

	public void reset() {
		queue.clear();
		requestMap.clear();
	}

	public boolean isNewlyCreated() {
		return (requestMap.size() == 0);
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getProfileId() {
		return profileId;
	}

	@Override
	public void notifyFinished(ConnectionEvent event) {
		logger.debug(event.getRequest().getURL() + "... Done!");
		requestMap.put(event.getRequest().getURL(), Boolean.TRUE);		
	}

	@Override
	public void notifyRequesting(ConnectionEvent event) {
		logger.debug("Requesting: " + event.getRequest().getURL() + "...");
	}

	@Override
	public void notifyResponding(ConnectionEvent event) {
	}

	public boolean queue(Request request) {
		boolean isQueued = false;
		String sURL = request.getURL();
		if (!requestMap.containsKey(sURL)) {
			isQueued = queue.offer(request);
			requestMap.put(sURL, Boolean.FALSE);
		}
		return isQueued;
	}

	/**
	 * Synchronize between the state and its profile 
	 */
	public void syncState() {
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
