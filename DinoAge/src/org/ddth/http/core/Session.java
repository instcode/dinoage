package org.ddth.http.core;

import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.RequestFuture;

public interface Session {

	/**
	 * @param request
	 * @return
	 */
	public abstract RequestFuture queue(Request request);

	/**
	 * 
	 */
	public abstract void start();

	/**
	 * 
	 */
	public abstract void shutdown();

	/**
	 * @return
	 */
	public abstract boolean isRunning();

}