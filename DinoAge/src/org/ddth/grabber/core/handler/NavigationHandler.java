/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.grabber.core.handler;

import java.io.IOException;
import java.io.InputStream;

public interface NavigationHandler {

	public boolean handleContent(InputStream inputStream) throws IOException;
}
