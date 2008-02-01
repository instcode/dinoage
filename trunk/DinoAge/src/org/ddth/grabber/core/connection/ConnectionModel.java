/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.grabber.core.connection;

import org.ddth.grabber.core.handler.ConnectionListener;
import org.ddth.grabber.core.handler.NavigationHandler;

public interface ConnectionModel {
	
	/**
	 * Request the content of the given URL.
	 * 
	 * @param sURL
	 * @param contentHandler
	 */
	public void sendRequest(String sURL, NavigationHandler contentHandler);
	
	/**
	 * @param listener
	 */
	public void registerConnectionListener(ConnectionListener listener);
	
	/**
	 * @param listener
	 */
	public void unregisterConnectionListener(ConnectionListener listener);
}