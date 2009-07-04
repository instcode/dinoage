package org.ddth.dinoage.eclipse;

import org.ddth.dinoage.eclipse.ui.views.WorkspaceView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.addView(WorkspaceView.ID, IPageLayout.LEFT, 0.3f, layout.getEditorArea());
		layout.getViewLayout(WorkspaceView.ID).setCloseable(false);
		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.BOTTOM, 0.7f, layout.getEditorArea());
	}
}
