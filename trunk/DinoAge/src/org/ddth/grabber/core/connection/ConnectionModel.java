/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.grabber.core.connection;

import org.ddth.grabber.core.handler.ConnectionListener;

public interface ConnectionModel {
	
	/**
	 * @param request
	 */
	public void sendRequest(Request request);
	
	/**
	 * @param listener
	 */
	public void registerConnectionListener(ConnectionListener listener);
	
	/**
	 * @param listener
	 */
	public void unregisterConnectionListener(ConnectionListener listener);
}