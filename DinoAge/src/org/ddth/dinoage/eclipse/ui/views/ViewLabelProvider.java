package org.ddth.dinoage.eclipse.ui.views;

import org.ddth.dinoage.eclipse.ui.model.ProfileNode;
import org.ddth.dinoage.eclipse.ui.model.TreeNode;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ViewLabelProvider extends LabelProvider {

	public String getText(Object obj) {
		return ((TreeNode)obj).getName();
	}

	public Image getImage(Object obj) {
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		if (obj instanceof ProfileNode)
			imageKey = ISharedImages.IMG_OBJ_FOLDER;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}
}
