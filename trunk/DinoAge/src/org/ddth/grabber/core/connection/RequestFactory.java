/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 6, 2008 12:24:03 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.grabber.core.connection;

public interface RequestFactory<T extends State> {
	
	public Request createRequest(String link, Session<T> session);
}
