package org.ddth.dinoage.eclipse.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class ProfileEditorInput implements IEditorInput {
	private String profileName;

	public ProfileEditorInput(String profile) {
		this.profileName = profile;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return profileName;
	}

	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getToolTipText() {
		// TODO Auto-generated method stub
		return profileName;
	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (!(obj instanceof ProfileEditorInput))
			return false;
		ProfileEditorInput other = (ProfileEditorInput) obj;
		return profileName.equals(other.profileName);
	}

	public int hashCode() {
		return profileName.hashCode();
	}
}
