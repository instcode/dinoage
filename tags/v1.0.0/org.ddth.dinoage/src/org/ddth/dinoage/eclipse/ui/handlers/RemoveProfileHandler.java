package org.ddth.dinoage.eclipse.ui.handlers;

import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.core.Workspace;
import org.ddth.dinoage.eclipse.Activator;
import org.ddth.dinoage.eclipse.ui.model.ProfileNode;
import org.ddth.dinoage.eclipse.ui.views.WorkspaceView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class RemoveProfileHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		IViewPart view = workbenchWindow.getActivePage().findView(WorkspaceView.ID);
		if (view != null) {
			ISelection selection = view.getSite().getSelectionProvider().getSelection();
			ProfileNode profile = (ProfileNode)((IStructuredSelection)selection).getFirstElement();
			if (MessageDialog.openQuestion(workbenchWindow.getShell(),
					workbenchWindow.getShell().getText(),
					ResourceManager.getMessage(ResourceManager.KEY_CONFIRM_REMOVE_WORKSPACE_PROFILE,
							new Object[] {profile.getData().getProfileName()}))) {
				Workspace workspace = Activator.getDefault().getDinoAge().getWorkspace();
				workspace.removeProfile(profile.getData());
			}
		}
		return null;
	}
}
