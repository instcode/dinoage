/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.impl;

import org.ddth.http.core.ConnectionEvent;
import org.ddth.http.core.ConnectionListener;
import org.ddth.http.core.Session;
import org.ddth.http.core.connection.ConnectionModel;
import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.RequestFuture;
import org.ddth.http.core.content.handler.ContentHandlerDispatcher;
import org.ddth.http.impl.connection.ThreadPoolConnectionModel;

public class ThreadPoolSession implements ConnectionListener, Session {
	private ConnectionModel connectionModel = new ThreadPoolConnectionModel(this);
	private ContentHandlerDispatcher dispatcher;

	public ThreadPoolSession(ContentHandlerDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public RequestFuture queue(Request request) {
		return connectionModel.sendRequest(request);
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
		connectionModel.close();
	}

	@Override
	public void notifyRequesting(ConnectionEvent event) {
		
	}

	@Override
	public void notifyFinished(ConnectionEvent event) {
		
	}

	@Override
	public void notifyResponding(ConnectionEvent event) {
		dispatcher.handle(event.getRequest(), event.getResponse());
	}
}
