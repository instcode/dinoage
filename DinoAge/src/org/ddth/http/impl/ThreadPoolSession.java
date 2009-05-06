/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.http.core.ConnectionEvent;
import org.ddth.http.core.ConnectionListener;
import org.ddth.http.core.Session;
import org.ddth.http.core.connection.ConnectionModel;
import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.RequestFuture;
import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.handler.ContentHandlerDispatcher;
import org.ddth.http.impl.connection.ThreadPoolConnectionModel;

/**
 * This is a basic implementation of {@link Session} which supports "threadpool"
 * {@link ConnectionModel}.<br>
 * <br>
 * Because of the name, we cannot change the {@link ConnectionModel} object in
 * this class :D. Of course, I know I can make it more generic, more extensible,
 * but in that case, I have to do more work :).. So, no design is the best, it's
 * just good enough :D.<br>
 * <br>
 * Actually, you can write a little code to support customizing the connection
 * model. It won't take much time because all you have to do is changing the
 * creation of {@link ConnectionModel} object line of code. I'm just too lazy to
 * do it :p.<br>
 * <br>
 * 
 * @author khoa.nguyen
 * 
 */
public abstract class ThreadPoolSession implements ConnectionListener, Session {
	private Log logger = LogFactory.getLog(ThreadPoolSession.class);
	
	/**
	 * A connection which has responsibility to send and handle any request
	 * queued by this session.   
	 */
	private ConnectionModel connectionModel = new ThreadPoolConnectionModel(this);
	
	/**
	 * The internal dispatcher which dispatches the content to a proper
	 * {@link ContentHandler}.
	 */
	private ContentHandlerDispatcher dispatcher;
	
	/**
	 * A request queue which is used for checking whether a request is on queued
	 * or not. It also tracks the status of any request made, by keeping a
	 * {@link RequestFuture} which is associated to that request. This
	 * queue is also used when we try to stop the session by enforcing all
	 * the ongoing requests to be cancelled.
	 */
	private Map<String, RequestFuture> queue = new ConcurrentHashMap<String, RequestFuture>();
	
	/**
	 * A list of ConnectionListeners which are interested in requesting events.
	 */
	private List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();

	/**
	 * Create a new session with the given content handle dispatcher.
	 * 
	 * @param dispatcher
	 * 		The dispatcher to be used in this session.
	 */
	public ThreadPoolSession(ContentHandlerDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public RequestFuture queue(Request request) {
		if (request == null) {
			throw new IllegalArgumentException("Request is null");
		}
		RequestFuture future = queue.get(request.getURL());
		if (future == null) {
			future = connectionModel.sendRequest(request);
			queue.put(request.getURL(), future);
		}
		return future;
	}

	/**
	 * Register a listener.
	 * 
	 * @param listener
	 * 		The listener to be registered.
	 */
	public void registerConnectionListener(ConnectionListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Unregister a listener.
	 * 
	 * @param listener
	 * 		The listener to be removed from the list.
	 * @return
	 * 		true if the listener is in the list.
	 */
	public boolean unregisterConnectionListener(ConnectionListener listener) {
		return listeners.remove(listener);
	}
	
	public void start() {
		connectionModel.open();
	}

	public boolean isRunning() {
		return connectionModel.running();
	}
	
	public void shutdown() {
		// Force abort current request
		for (RequestFuture future : queue.values()) {
			future.cancel(true);
		}
		connectionModel.close();
	}

	public void notifyRequesting(ConnectionEvent event) {
		logger.debug("Requesting: " + event.getRequest().getURL() + "...");
		for (ConnectionListener listener : listeners) {
			listener.notifyRequesting(event);
		}
	}

	public void notifyFinished(ConnectionEvent event) {
		logger.debug("Requesting: " + event.getRequest().getURL() + "... Done!");
		queue.remove(event.getRequest().getURL());
		for (ConnectionListener listener : listeners) {
			listener.notifyFinished(event);
		}
	}

	public void notifyResponding(ConnectionEvent event) {
		logger.debug("Handling: " + event.getRequest().getURL() + "...");
		for (ConnectionListener listener : listeners) {
			listener.notifyResponding(event);
		}
		Content<?> content = dispatcher.handle(event.getRequest(), event.getResponse());
		content(content);
	}
	
	/**
	 * Handle the final content which was pre-processed via a chain of
	 * {@link ContentHandler}s.
	 * 
	 * @param content
	 * 		The content to be processed.
	 */
	protected abstract void content(Content<?> content);
}
