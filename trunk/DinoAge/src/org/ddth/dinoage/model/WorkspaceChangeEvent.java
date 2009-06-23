/****************************************************
 * $Project: DinoAge
 * $Date:: Jun 23, 2009
 * $Revision:
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.dinoage.model;


/**
 * @author khoanguyen
 *
 */
public class WorkspaceChangeEvent {
	
	public static final int PROFILE_ADDED_CHANGE = 1;
	public static final int PROFILE_REMOVED_CHANGE = 2;
	public static final int WORKSPACE_RELOADED_CHANGE = 3;
	
	private Workspace workspace;
	private Object data;
	private int type;
	
	/**
	 * 
	 */
	public WorkspaceChangeEvent(Workspace workspace, Object data, int type) {
		this.workspace = workspace;
		this.data = data;
		this.type = type;
	}
	
	/**
	 * @return The workspace
	 */
	public Workspace getWorkspace() {
		return workspace;
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
