package org.ddth.dinoage.eclipse.ui.handlers;

import org.ddth.dinoage.eclipse.Activator;
import org.ddth.dinoage.eclipse.ui.widget.DinoAgeProfileDlg;
import org.ddth.dinoage.model.Workspace;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class AddProfileHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Workspace workspace = Activator.getDefault().getDinoAge().getWorkspace();
		DinoAgeProfileDlg dlg = new DinoAgeProfileDlg(window.getShell(), workspace);
		if (dlg.open() == SWT.OK) {
		}
		return null;
	}
}
