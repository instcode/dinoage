/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 1, 2008 8:23:13 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author khoa.nguyen
 *
 */
public abstract class SessionProfile extends Profile {
	private List<ProfileChangeListener> listeners = new ArrayList<ProfileChangeListener>();
	
	@Override
	protected void store(Properties properties) {
	}
	
	@Override
	protected void load(Properties properties) {
	}

	/**
	 * Start loading profile from beginning state.
	 */
	protected abstract void loadAll();
	
	/**
	 * Stop all loading.
	 */
	protected abstract void stopAll();
	
	/**
	 * Add listener. First listener added will trigger
	 * the current profile to load its data.
	 * 
	 * @see #loadAll()
	 * @param listener
	 */
	public void addProfileChangeListener(ProfileChangeListener listener) {
		listeners.add(listener);
		if (listeners.size() == 1) {
			loadAll();
		}
	}
	
	/**
	 * Remove listener. Last listener removed will trigger
	 * the current profile to stop loading its data.
	 * 
	 * @see #stopAll()
	 * @param listener
	 * @return
	 */
	public boolean removeProfileChangeListener(ProfileChangeListener listener) {
		boolean success = listeners.remove(listener);
		if (listeners.size() == 0) {
			stopAll();
		}
		return success;
	}
	
	protected void fireProfileChanged(ProfileChangeEvent event) {
		for (ProfileChangeListener listener : listeners) {
			listener.profileChanged(event);
		}
	}
}
