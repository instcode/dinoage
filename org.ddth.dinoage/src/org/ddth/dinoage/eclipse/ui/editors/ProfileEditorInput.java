package org.ddth.dinoage.eclipse.ui.editors;

import org.ddth.dinoage.core.Profile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class ProfileEditorInput implements IEditorInput {
	private Profile profile;

	public ProfileEditorInput(Profile profile) {
		this.profile = profile;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return profile.getProfileName();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return profile.getProfileURL();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == Profile.class) {
			return profile;
		}
		return null;
	}

	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (!(obj instanceof ProfileEditorInput))
			return false;
		ProfileEditorInput other = (ProfileEditorInput) obj;
		return profile.equals(other.profile);
	}

	public int hashCode() {
		return profile.hashCode();
	}
}
