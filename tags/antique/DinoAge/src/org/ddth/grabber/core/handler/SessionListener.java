/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.grabber.core.handler;

public interface SessionListener {
	/**
	 * Notify session is started
	 */
	public void sessionStarted();
	
	/**
	 * Notify session is stopped
	 */
	public void sessionStopped();
}