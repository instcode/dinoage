package org.ddth.dinoage.eclipse;

import org.ddth.dinoage.eclipse.ui.views.WorkspaceView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.addView(WorkspaceView.ID, IPageLayout.LEFT, 0.3f, layout.getEditorArea());
		layout.getViewLayout(WorkspaceView.ID).setCloseable(false);
//		IFolderLayout folder = layout.createFolder("profiles", IPageLayout.TOP, 0.5f, layout.getEditorArea());
//		folder.addPlaceholder(BlogView.ID + ":*");
//		folder.addView(BlogView.ID);
	}
}
