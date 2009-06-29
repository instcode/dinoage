package org.ddth.dinoage.eclipse.ui.handlers;

import org.ddth.dinoage.eclipse.ui.editors.ProfileEditor;
import org.ddth.dinoage.eclipse.ui.editors.ProfileEditorInput;
import org.ddth.dinoage.eclipse.ui.model.ProfileNode;
import org.ddth.dinoage.eclipse.ui.views.WorkspaceView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenProfileHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
		IViewPart view = activePage.findView(WorkspaceView.ID);
		if (view != null) {
			IStructuredSelection selection = (IStructuredSelection) view.getSite().getSelectionProvider().getSelection();
			ProfileNode profile = (ProfileNode)selection.getFirstElement();
			try {
				activePage.openEditor(new ProfileEditorInput(profile.getData()), ProfileEditor.ID);
			}
			catch (PartInitException e) {
			}
		}
		return null;
	}
}
