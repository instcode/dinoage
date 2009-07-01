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
public class DataLoadEvent {
	
	public static final int STEP_LOADED = 2;
	
	private Object data;
	private int type;
	
	/**
	 * 
	 */
	public DataLoadEvent(Object data, int type) {
		this.data = data;
		this.type = type;
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
