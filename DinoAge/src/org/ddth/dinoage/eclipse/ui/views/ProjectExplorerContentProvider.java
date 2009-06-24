package org.ddth.dinoage.eclipse.ui.views;

import java.util.Collection;

import org.ddth.dinoage.core.Profile;
import org.ddth.dinoage.core.Workspace;
import org.ddth.dinoage.core.WorkspaceChangeEvent;
import org.ddth.dinoage.core.WorkspaceChangeListener;
import org.ddth.dinoage.eclipse.ui.model.ProfileNode;
import org.ddth.dinoage.eclipse.ui.model.TreeNode;
import org.ddth.dinoage.eclipse.ui.model.WorkspaceNode;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

public class ProjectExplorerContentProvider implements ITreeContentProvider, WorkspaceChangeListener {

	private TreeViewer viewer;
	private TreeNode root = new WorkspaceNode(null);

	public void workspaceChanged(WorkspaceChangeEvent event) {
		switch (event.getType()) {
		case WorkspaceChangeEvent.WORKSPACE_RELOADED_CHANGE:
			createNodes(((Workspace) event.getData()).getProfiles());
			break;
			
		case WorkspaceChangeEvent.PROFILE_ADDED_CHANGE:
			addNode(root, (Profile) event.getData());
			break;
			
		case WorkspaceChangeEvent.PROFILE_REMOVED_CHANGE:
			removeNode(root, (Profile) event.getData());
			break;
		}
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;
		
		Workspace oldWorkspace = (Workspace) oldInput;
		Workspace newWorkspace = (Workspace) newInput;
		
		if (oldWorkspace != newWorkspace) {
			if (oldWorkspace != null) {
				oldWorkspace.removeWorkspaceChangeListener(this);
			}
			if (newWorkspace != null) {
				newWorkspace.addWorkspaceChangeListener(this);
				createNodes(newWorkspace.getProfiles());
			}
		}
	}

	private void removeNode(TreeNode parent, Profile profile) {
		TreeNode node = new ProfileNode(parent, profile);
		parent.remove(node);
		viewer.remove(node);
		viewer.refresh();
	}
	
	private void addNode(TreeNode parent, Profile profile) {
		TreeNode node = new ProfileNode(parent, profile);
		parent.add(node);
		viewer.add(parent, node);
		viewer.refresh();
	}
	
	private void createNodes(Collection<Profile> profiles) {
		int index = 0;
		TreeNode parent = new WorkspaceNode(null);
		TreeNode[] nodes = new TreeNode[profiles.size()];
		for (Profile profile : profiles) {
			nodes[index] = new ProfileNode(parent, profile);
			parent.add(nodes[index++]);
		}
		root = parent;
		viewer.add(parent, nodes);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		return ((TreeNode)parentElement).getChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		return ((TreeNode)element).getParent();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return ((TreeNode)element).hasChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return root.getChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		root = null;
	}
}
