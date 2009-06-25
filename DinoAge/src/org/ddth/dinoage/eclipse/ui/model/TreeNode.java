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

	/**
	 * Add child node
	 * 
	 * @param child
	 */
	public void add(TreeNode child) {
		
	}
	
	/**
	 * Remove child node
	 * 
	 * @param child
	 */
	public void remove(TreeNode child) {
		
	}
	
	/**
	 * @return parent node
	 */
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * @return true if this node has children
	 */
	public boolean hasChildren() {
		return false;
	}
	
	public String getLabel() {
		return toString();
	}
}
