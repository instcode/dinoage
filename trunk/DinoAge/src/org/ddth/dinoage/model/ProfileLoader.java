/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 29, 2008 9:32:44 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.model;

import java.io.File;
import java.io.IOException;

public interface ProfileLoader {

	/**
	 * @return
	 */
	public Profile createProfile();
	
	/**
	 * @param profileFile
	 * @return
	 * @throws IOException
	 */
	public Profile loadProfile(File profileFile) throws IOException;
}
