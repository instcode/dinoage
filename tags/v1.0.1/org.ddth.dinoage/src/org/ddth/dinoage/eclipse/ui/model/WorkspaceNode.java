/****************************************************
 * $Project: DinoAge
 * $Date:: Jun 23, 2009
 * $Revision:
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.dinoage.eclipse.ui.model;

import java.util.ArrayList;
import java.util.List;


/**
 * @author khoanguyen
 *
 */
public class WorkspaceNode extends TreeNode {

	private List<TreeNode> nodes = new ArrayList<TreeNode>();
	
	public WorkspaceNode(TreeNode parent) {
		super(parent);
	}
	
	public void add(TreeNode node) {
		nodes.add(node);
	}
	
	public void remove(TreeNode node) {
		nodes.remove(node);
	}

	public boolean hasChildren() {
		return nodes.size() > 0;
	}
	
	public Object[] getChildren() {
		return nodes.toArray();
	}
}
