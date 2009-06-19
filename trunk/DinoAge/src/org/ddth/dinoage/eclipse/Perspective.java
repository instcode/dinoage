package org.ddth.dinoage.eclipse;

import org.ddth.dinoage.eclipse.ui.views.ProfileListView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.addView(ProfileListView.ID, IPageLayout.LEFT, 0.3f, layout.getEditorArea());
		layout.getViewLayout(ProfileListView.ID).setCloseable(false);
//		IFolderLayout folder = layout.createFolder("profiles", IPageLayout.TOP, 0.5f, layout.getEditorArea());
//		folder.addPlaceholder(BlogView.ID + ":*");
//		folder.addView(BlogView.ID);
	}
}
