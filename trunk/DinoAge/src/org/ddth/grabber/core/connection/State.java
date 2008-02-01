/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 1, 2008 7:45:55 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.grabber.core.connection;

import java.util.Map;

import org.ddth.grabber.core.handler.NavigationHandler;

public interface State {
	public Map<String, Boolean> getCompletedMap();
	public Map<String, NavigationHandler> getOutgoingMap();
}
