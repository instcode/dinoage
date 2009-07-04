package org.ddth.dinoage.eclipse.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.handlers.HandlerUtil;

public class ShowConsoleHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage activePage = workbenchWindow.getActivePage();
		try {
			activePage.showView(IConsoleConstants.ID_CONSOLE_VIEW);
		}
		catch (PartInitException e) {
		}
		return null;
	}
}
