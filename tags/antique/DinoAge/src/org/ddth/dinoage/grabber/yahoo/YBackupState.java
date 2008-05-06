/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.model.Profile;
import org.ddth.grabber.core.connection.Request;
import org.ddth.grabber.core.connection.Session;
import org.ddth.grabber.core.connection.State;
import org.ddth.grabber.core.handler.ConnectionListener;

public class YBackupState implements State, ConnectionListener {
	private Log logger = LogFactory.getLog(YBackupState.class);
	
	/**
	 * The boolean value is used to mark whether a URL is completed or not
	 */
	private Map<String, Boolean> requestMap = new ConcurrentHashMap<String, Boolean>();
	private Queue<Request> queue = new ConcurrentLinkedQueue<Request>();

	private String profileId;
	private Profile profile;

	public YBackupState(Profile profile) {
		this.profile = profile;
		this.profileId = getProfileId(profile.getProfileURL());
		
		String[] completedURLs = profile.getCompletedURLs();
		for (String completedURL : completedURLs) {
			requestMap.put(completedURL, Boolean.TRUE);
		}
		String[] outgoingURLs = profile.getOutgoingURLs();
		for (String outgoingURL : outgoingURLs) {
			requestMap.put(outgoingURL, Boolean.FALSE);
		}
	}
	
	private String getProfileId(String profileURL) {
		int begin = ResourceManager.KEY_PROFILE_URL.length();
		int end = profileURL.indexOf("?", begin);
		end = (end == -1) ? profileURL.length() : end;
		return profileURL.substring(begin, end);
	}

	public void initialize(Session<YBackupState> session) {
		String[] outgoingURLs = profile.getOutgoingURLs();
		for (String outgoingURL : outgoingURLs) {
			session.queueRequest(outgoingURL);
		}
	}

	public Profile getProfile() {
		return profile;
	}
	
	public void write(ByteArrayInputStream byteArrayInputStream, int id) {
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
	
	public void notifyFinished(String sURL, boolean isCompletedWithoutError) {
		logger.debug(sURL + "... Done!");
		requestMap.put(sURL, Boolean.TRUE);
	}

	public void notifyRequesting(String sURL) {
		logger.debug("Requesting: " + sURL + "...");
	}

	public Request poll() {
		return queue.poll();
	}

	public boolean queue(Request request) {
		boolean isQueued = false;
		String sURL = request.getURL();
		if (!requestMap.containsKey(sURL)) {
			if (request.getProcessor() != null) {
				isQueued = queue.offer(request);
			}
			requestMap.put(sURL, Boolean.FALSE);
		}
		return isQueued;
	}

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
}
