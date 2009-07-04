/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core;

import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.RequestFuture;

/**
 * The start entry point for all other stuffs. All you need is holding a session
 * object and invoking {@link #queue(Request)} to queue your requests. The engine
 * should execute the request and return the output to you in any minute after. No,
 * I really meant any millisecond =)). 
 * 
 * @author khoa.nguyen
 *
 */
public interface Session {

	/**
	 * Queue the given request.
	 * 
	 * @param request
	 * 		Request to be made. Must not be null.
	 * @return
	 * 		A future object which contains all information about
	 * the state of its own request. This might be null if the
	 * session is stopped.
	 * 
	 * @exception IllegalArgumentException if the input request is null.
	 * 
	 */
	public abstract RequestFuture queue(Request request);

	/**
	 * Start the session. Let the crawling begin!  
	 */
	public abstract void start();

	/**
	 * Shutdown the session. No more queue put/poll until next {@link #start()}.
	 */
	public abstract void shutdown();

	/**
	 * Check if the session is running.
	 * 
	 * @return
	 * 		true if the session is actually running :D
	 */
	public abstract boolean isRunning();
}