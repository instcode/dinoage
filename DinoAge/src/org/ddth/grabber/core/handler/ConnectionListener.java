/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.grabber.core.handler;

public interface ConnectionListener {

	public void notifyRequesting(String sURL);
	public void notifyFinished(String sURL, boolean isCompletedWithoutError);
}