/****************************************************
 * $Project: DinoAge
 * $Date:: Jun 23, 2009
 * $Revision:
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.dinoage.core;

/**
 * @author khoanguyen
 *
 */
public class ProfileChangeEvent {
	
	public static final int PROFILE_CHANGED = 1;
	public static final int PROFILE_FIRST_LOADED = 2;
	public static final int PROFILE_DELTA_CHANGED = 3;
	
	private Profile profile;
	private Object data;
	private int type;
	
	/**
	 * 
	 */
	public ProfileChangeEvent(Profile profile, Object data, int type) {
		this.profile = profile;
		this.data = data;
		this.type = type;
	}
	
	/**
	 * @return The workspace
	 */
	public Profile getProfile() {
		return profile;
	}
	
	/**
	 * @return The object
	 */
	public Object getData() {
		return data;
	}
	
	/**
	 * @return The event type
	 */
	public int getType() {
		return type;
	}
}
