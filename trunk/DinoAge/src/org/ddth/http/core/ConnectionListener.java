/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core;

/**
 * Listen to the request events. Wow, it's fantastic, like the singing of a
 * nightingale =)) I am a coder bie^'t la`m tho* =))
 * 
 * @author khoa.nguyen
 * 
 */
public interface ConnectionListener {

	/**
	 * Notify about which request is being made.
	 * 
	 * @param event
	 *            The event, of course :D
	 */
	public void notifyRequesting(ConnectionEvent event);

	/**
	 * Notify about which response is being processed.
	 * 
	 * @param event
	 *            The event, and this is the second time I have to type it!!
	 */
	public void notifyResponding(ConnectionEvent event);

	/**
	 * Notify about which request has been completed gracefully :-)
	 * 
	 * @param event
	 *            Don't know what it is! :-w
	 */
	public void notifyFinished(ConnectionEvent event);
}