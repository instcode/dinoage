package org.ddth.dinoage.eclipse.ui.providers;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class ProjectExplorerContentProvider implements IStructuredContentProvider, ITreeContentProvider {
	private TreeParent root = new TreeParent("");

	public ProjectExplorerContentProvider() {
		
		TreeObject to1 = new TreeObject("Map");
		TreeObject to2 = new TreeObject("Fault Segment");
		TreeParent p1 = new TreeParent("Project 1");
		p1.addChild(to1);
		p1.addChild(to2);
		
		TreeObject to3 = new TreeObject("Map");
		TreeObject to4 = new TreeObject("Fault Segment");
		TreeParent p2 = new TreeParent("Project 2");
		p2.addChild(to3);
		p2.addChild(to4);

		add(p1);
		add(p2);
	}
	
	public Object[] getElements(Object parent) {
		if (!(parent instanceof TreeParent)) {
			return getChildren(root);
		}
		return getChildren(parent);
	}

	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject) child).getParent();
		}
		return null;
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent) parent).getChildren();
		}
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent) parent).hasChildren();
		return false;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public void add(TreeParent project) {
		root.addChild(project);
	}
	
	public void remove(TreeParent project) {
		root.removeChild(project);
	}
}
