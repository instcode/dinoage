/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo;

import java.io.ByteArrayInputStream;
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
	private Map<String, Boolean> requestedMap = new ConcurrentHashMap<String, Boolean>();
	private Queue<Request> queue = new ConcurrentLinkedQueue<Request>();

	private String profileId;

	private String getProfileId(String profileURL) {
		int begin = ResourceManager.KEY_PROFILE_URL.length();
		int end = profileURL.indexOf("?", begin);
		end = (end == -1) ? profileURL.length() : end;
		return profileURL.substring(begin, end);
	}

	public void initialize(Session<YBackupState> session, Profile profile) {
		this.profileId = getProfileId(profile.getProfileURL());
		String[] completedURLs = profile.getCompletedURLs().split(",");
		for (String completedURL : completedURLs) {
			requestedMap.put(completedURL, Boolean.TRUE);
		}
		
		String[] outgoingURLs = profile.getOutgoingURLs().split(",");
		
		for (String outgoingURL : outgoingURLs) {
			session.queueRequest(outgoingURL);
			requestedMap.put(outgoingURL, Boolean.FALSE);
		}
	}

	public void notifyFinished(String sURL, boolean isCompletedWithoutError) {
		logger.debug(sURL + "... Done!");
		requestedMap.put(sURL, Boolean.TRUE);
	}

	public void notifyRequesting(String sURL) {
		logger.debug(sURL + " <-- Requesting...");
	}

//	public String a() {
//		Iterator<String> completedURLs = completedURLMap.keySet().iterator();
//		while (completedURLs.hasNext()) {
//			String sURL = completedURLs.next();
//			// Only "real" completed URL
//			if (completedURLMap.get(sURL).booleanValue()) {
//				writer.write("\t" + sURL + (completedURLs.hasNext() ? ",\\\n" : "\n"));
//			}
//		}
//
//		Iterator<String> outgoingURLs = outgoingURLMap.keySet().iterator();
//		while (outgoingURLs.hasNext()) {
//			String sURL = outgoingURLs.next();
//			writer.write("\t" + sURL + (outgoingURLs.hasNext() ? ",\\\n" : "\n"));
//		}
//	}

	public void reset() {
		queue.clear();
		requestedMap.clear();
	}

	public boolean isNewlyCreated() {
		return (requestedMap.size() == 0);
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getProfileId() {
		return profileId;
	}

	public Request poll() {
		return queue.poll();
	}

	public boolean queue(Request request) {
		boolean isQueued = false;
		if (!requestedMap.containsKey(request.getURL())) {
			isQueued = queue.offer(request);
			requestedMap.put(request.getURL(), Boolean.TRUE);
		}
		return isQueued;
	}

	public void write(ByteArrayInputStream byteArrayInputStream, int guestbook) {
		// TODO Auto-generated method stub
		
	}
}
