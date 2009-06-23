package org.ddth.dinoage.eclipse.ui.model;

public class TreeNode {
	private static final Object[] NO_CHILDREN = new Object[0];
	private TreeNode parent;

	public TreeNode(TreeNode parent) {
		this.parent = parent;
	}

	public Object[] getChildren() {
		return NO_CHILDREN;
	}

	public void add(TreeNode child) {
		
	}
	
	public void remove(TreeNode child) {
		
	}
	
	/**
	 * @return
	 */
	public Object getParent() {
		return parent;
	}

	/**
	 * @return
	 */
	public boolean hasChildren() {
		return false;
	}
	
	public String getName() {
		return toString();
	}
}
