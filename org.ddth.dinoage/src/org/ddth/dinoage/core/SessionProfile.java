/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 1, 2008 8:23:13 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.core;

import java.util.Properties;

/**
 * @author khoa.nguyen
 *
 */
public abstract class SessionProfile extends Profile {

	@Override
	protected void store(Properties properties) {
	}
	
	@Override
	protected void load(Properties properties) {
	}

	public void loadProfileFromStorage() {
	}
}
