/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core;


/**
 * @author khoa.nguyen
 *
 */
public interface ConnectionListener {

	/**
	 * @param event
	 */
	public void notifyRequesting(ConnectionEvent event);
	
	/**
	 * @param event
	 */
	public void notifyResponding(ConnectionEvent event);

	/**
	 * @param event
	 */
	public void notifyFinished(ConnectionEvent event);
}