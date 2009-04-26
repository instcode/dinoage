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

public abstract class ThreadPoolSession implements ConnectionListener, Session {
	private Log logger = LogFactory.getLog(ThreadPoolSession.class);
	
	private ConnectionModel connectionModel = new ThreadPoolConnectionModel(this);
	private ContentHandlerDispatcher dispatcher;
	private Map<String, RequestFuture> queue = new ConcurrentHashMap<String, RequestFuture>();
	private List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();

	public ThreadPoolSession(ContentHandlerDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
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

	public void registerConnectionListener(ConnectionListener listener) {
		listeners.add(listener);
	}
	
	public void unregisterConnectionListener(ConnectionListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void start() {
		connectionModel.open();
	}

	@Override
	public boolean isRunning() {
		return connectionModel.running();
	}
	
	@Override
	public void shutdown() {
		// Force abort current request
		for (RequestFuture future : queue.values()) {
			future.cancel(true);
		}
		connectionModel.close();
	}

	@Override
	public void notifyRequesting(ConnectionEvent event) {
		logger.debug("Requesting: " + event.getRequest().getURL() + "...");
		for (ConnectionListener listener : listeners) {
			listener.notifyRequesting(event);
		}
	}

	@Override
	public void notifyFinished(ConnectionEvent event) {
		logger.debug("Requesting: " + event.getRequest().getURL() + "... Done!");
		queue.remove(event.getRequest().getURL());
		for (ConnectionListener listener : listeners) {
			listener.notifyFinished(event);
		}
	}

	@Override
	public void notifyResponding(ConnectionEvent event) {
		logger.debug("Handling: " + event.getRequest().getURL() + "...");
		for (ConnectionListener listener : listeners) {
			listener.notifyResponding(event);
		}
		Content<?> content = dispatcher.handle(event.getRequest(), event.getResponse());
		content(content);
	}
	
	protected abstract void content(Content<?> content);
}
