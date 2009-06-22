package org.ddth.dinoage.eclipse.ui.handlers;

import org.ddth.dinoage.eclipse.Activator;
import org.ddth.dinoage.eclipse.ui.model.WorkbenchProfile;
import org.ddth.dinoage.model.Workspace;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class RemoveProfileHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		ISelection selection = window.getActivePage().getSelection();
		if (selection != null & selection instanceof IStructuredSelection) {
			Workspace workspace = Activator.getDefault().getDinoAge().getWorkspace();
			WorkbenchProfile profile = (WorkbenchProfile)((IStructuredSelection)selection).getFirstElement();
			workspace.removeProfile(null);
		}
		return null;
	}
}
