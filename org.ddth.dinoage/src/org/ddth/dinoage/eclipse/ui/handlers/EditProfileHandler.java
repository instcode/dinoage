package org.ddth.dinoage.eclipse.ui.handlers;

import org.ddth.dinoage.core.Workspace;
import org.ddth.dinoage.eclipse.Activator;
import org.ddth.dinoage.eclipse.ui.model.ProfileNode;
import org.ddth.dinoage.eclipse.ui.views.WorkspaceView;
import org.ddth.dinoage.eclipse.ui.widget.DinoAgeProfileDlg;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class EditProfileHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage activePage = workbenchWindow.getActivePage();
		IViewPart view = activePage.findView(WorkspaceView.ID);
		if (view != null) {
			IStructuredSelection selection = (IStructuredSelection) view.getSite().getSelectionProvider().getSelection();
			ProfileNode profile = (ProfileNode)selection.getFirstElement();
			Workspace workspace = Activator.getDefault().getDinoAge().getWorkspace();
			DinoAgeProfileDlg dlg = new DinoAgeProfileDlg(workbenchWindow.getShell(), workspace);
			if (dlg.edit(profile.getData()) == SWT.OK) {
				
			}
		}
		return null;
	}
}
