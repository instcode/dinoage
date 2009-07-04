package org.ddth.dinoage.eclipse.ui.handlers;

import org.ddth.dinoage.core.Profile;
import org.ddth.dinoage.core.Workspace;
import org.ddth.dinoage.eclipse.Activator;
import org.ddth.dinoage.eclipse.ui.model.ProfileNode;
import org.ddth.dinoage.eclipse.ui.views.WorkspaceView;
import org.ddth.dinoage.eclipse.ui.wizard.DinoAgeProfileDlg;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class AddProfileHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		Workspace workspace = Activator.getDefault().getDinoAge().getWorkspace();
		Profile profile = workspace.createEmptyProfile();
		DinoAgeProfileDlg dlg = new DinoAgeProfileDlg(workbenchWindow.getShell(), workspace, profile);
		if (dlg.open() == Window.OK) {
			IViewPart view = workbenchWindow.getActivePage().findView(WorkspaceView.ID);
			if (view != null) {
				StructuredSelection selection = new StructuredSelection(new ProfileNode(null, profile));
				view.getSite().getSelectionProvider().setSelection(selection);
			}
		}
		return null;
	}
}
