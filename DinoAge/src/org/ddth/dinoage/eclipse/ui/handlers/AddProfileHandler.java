package org.ddth.dinoage.eclipse.ui.handlers;

import org.ddth.dinoage.core.Workspace;
import org.ddth.dinoage.eclipse.Activator;
import org.ddth.dinoage.eclipse.ui.model.ProfileNode;
import org.ddth.dinoage.eclipse.ui.views.WorkspaceView;
import org.ddth.dinoage.eclipse.ui.widget.DinoAgeProfileDlg;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class AddProfileHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		Workspace workspace = Activator.getDefault().getDinoAge().getWorkspace();
		DinoAgeProfileDlg dlg = new DinoAgeProfileDlg(workbenchWindow.getShell(), workspace);
		if (dlg.open() == SWT.OK) {
			IViewPart view = workbenchWindow.getActivePage().findView(WorkspaceView.ID);
			if (view != null) {
				StructuredSelection selection = new StructuredSelection(new ProfileNode(null, dlg.getProfile()));
				view.getSite().getSelectionProvider().setSelection(selection);
			}
		}
		return null;
	}
}
