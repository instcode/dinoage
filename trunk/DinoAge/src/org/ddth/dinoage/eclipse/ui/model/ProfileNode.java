/****************************************************
 * $Project: DinoAge
 * $Date:: Jun 23, 2009
 * $Revision:
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.dinoage.eclipse.ui.model;

import org.ddth.dinoage.core.Profile;


/**
 * @author khoanguyen
 *
 */
public class ProfileNode extends TreeNode {
	private Profile profile;
	
	public ProfileNode(TreeNode parent, Profile profile) {
		super(parent);
		this.profile = profile;
	}

	public String getLabel() {
		return profile.getProfileName();
	}

	/**
	 * @return
	 */
	public Profile getData() {
		return profile;
	}

	public boolean equals(Object paramObject) {
		return paramObject instanceof ProfileNode && profile == ((ProfileNode)paramObject).profile;
	}

	public int hashCode() {
		return profile.hashCode();
	}
}
