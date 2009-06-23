package org.ddth.dinoage.eclipse.ui.views;

import java.util.Collection;

import org.ddth.dinoage.core.WorkspaceChangeEvent;
import org.ddth.dinoage.core.WorkspaceChangeListener;
import org.ddth.dinoage.eclipse.ui.model.WorkbenchNode;
import org.ddth.dinoage.model.Profile;
import org.ddth.dinoage.model.Workspace;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class ProjectExplorerContentProvider extends BaseWorkbenchContentProvider implements WorkspaceChangeListener {

	private TreeViewer viewer;
	
	public ProjectExplorerContentProvider() {
		IAdapterFactory factory = new IAdapterFactory() {
			private Class[] clazzes = new Class[] {IWorkbenchAdapter.class};
			
			public Object getAdapter(Object adaptableObject, Class adapterType) {
				if (adaptableObject instanceof Workspace) {
					return new WorkbenchNode();
				}
				if (adaptableObject instanceof Profile) {
					return new WorkbenchNode();
				}
				return null;
			}

			public Class[] getAdapterList() {
				return clazzes;
			}
		};
		
		Platform.getAdapterManager().registerAdapters(factory, Workspace.class);
		Platform.getAdapterManager().registerAdapters(factory, Profile.class);
	}

	public void workspaceChanged(WorkspaceChangeEvent event) {
		switch (event.getType()) {
		case WorkspaceChangeEvent.WORKSPACE_RELOADED_CHANGE:
			viewer.refresh();
			break;
			
		case WorkspaceChangeEvent.PROFILE_ADDED_CHANGE:
			viewer.refresh(event.getData(), true);
			break;
			
		case WorkspaceChangeEvent.PROFILE_REMOVED_CHANGE:
			viewer.refresh(event.getData(), false);
			break;
		}
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);
		this.viewer = (TreeViewer) viewer;
		
		Workspace oldWorkspace = (Workspace) oldInput;
		Workspace newWorkspace = (Workspace) newInput;
		
		if (oldWorkspace != newWorkspace) {
			if (oldWorkspace != null) {
				oldWorkspace.removeWorkspaceChangeListener(this);
			}
			if (newWorkspace != null) {
				newWorkspace.addWorkspaceChangeListener(this);
				Collection<Profile> profiles = newWorkspace.getProfiles();
				for (Profile profile : profiles) {
					this.viewer.add(newWorkspace, profile);
				}
			}
		}
	}
}
