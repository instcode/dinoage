package org.ddth.dinoage.eclipse.ui.handlers;

import java.io.File;

import org.ddth.dinoage.DinoAge;
import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.core.BrowsingSession;
import org.ddth.dinoage.core.Profile;
import org.ddth.dinoage.eclipse.Activator;
import org.ddth.dinoage.eclipse.ui.UniversalUtil;
import org.ddth.dinoage.eclipse.ui.model.ProfileNode;
import org.ddth.dinoage.eclipse.ui.views.WorkspaceView;
import org.ddth.http.core.SessionChangeListener;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExecuteProfileHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = HandlerUtil
				.getActiveWorkbenchWindow(event);
		IWorkbenchPage activePage = workbenchWindow.getActivePage();
		IViewPart view = activePage.findView(WorkspaceView.ID);
		if (view == null) {
			return null;
		}
		IStructuredSelection selection = (IStructuredSelection) view
				.getSite().getSelectionProvider().getSelection();
		ProfileNode node = (ProfileNode) selection.getFirstElement();
		DinoAge dinoage = Activator.getDefault().getDinoAge();
		Profile profile = node.getData();
		BrowsingSession session = dinoage.createSession(profile);
		session.addSessionChangeListener((SessionChangeListener) view);
		
		if (session.isRunning()) {
			if (MessageDialog.openQuestion(workbenchWindow.getShell(),
					workbenchWindow.getShell().getText(),
					ResourceManager.getMessage(ResourceManager.KEY_CONFIRM_STOP_ACTIVE_PROFILE,
							new Object[] {profile.getProfileName()}))) {
				session.shutdown();
				session.removeSessionChangeListener((SessionChangeListener) view);
			}
		}
		else {
			int answer = SWT.NO;
			if (!session.isRestorable()) {
				File profileFolder = dinoage.getWorkspace().getProfileFolder(
						profile);
				String message = ResourceManager.getMessage(
						ResourceManager.KEY_RESUME_RETRIEVING_CONFIRM,
						new String[] {
								profile.getProfileName(),
								profileFolder.getAbsolutePath() });
				answer = UniversalUtil.showConfirmDlg(
						workbenchWindow.getShell(),
						workbenchWindow.getShell().getText(),
						message);
			}
			if (answer == SWT.YES) {
				session.restore();
			}
			else if (answer == SWT.NO) {
				session.start();
			}
		}
		return null;
	}
}
