package org.ddth.dinoage.eclipse.ui.model;

import org.ddth.dinoage.model.Profile;
import org.ddth.dinoage.model.Workspace;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchAdapter;

public class WorkbenchNode extends WorkbenchAdapter {

	private WorkbenchNode parent;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object o) {
		if (o instanceof Workspace) {
			return ((Workspace)o).getProfiles().toArray();
		}
		return NO_CHILDREN;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		if (object instanceof WorkbenchProfile)
			imageKey = ISharedImages.IMG_OBJ_FOLDER;
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(imageKey);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
	 */
	public String getLabel(Object o) {
		if (o instanceof Profile) {
			return ((Profile)o).getProfileName();
		}
		return o == null ? "" : o.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
	 */
	public Object getParent(Object o) {
		return parent;
	}
}
