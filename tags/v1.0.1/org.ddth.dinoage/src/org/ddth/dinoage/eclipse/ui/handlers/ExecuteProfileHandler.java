package org.ddth.dinoage.eclipse.ui.handlers;

import java.io.File;

import org.ddth.dinoage.DinoAge;
import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.core.BrowsingSession;
import org.ddth.dinoage.core.ConsoleLogger;
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
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
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

		BrowsingSession session = dinoage.getSession(profile);
		session.addSessionChangeListener((SessionChangeListener) view);
		
		if (session.isRunning()) {
			if (MessageDialog.openQuestion(workbenchWindow.getShell(),
					workbenchWindow.getShell().getText(),
					ResourceManager.getMessage(ResourceManager.KEY_CONFIRM_STOP_ACTIVE_PROFILE,
							new Object[] {profile.getProfileName()}))) {
				session.shutdown();
				session.removeSessionChangeListener((SessionChangeListener) view);
				dinoage.getWorkspace().saveProfile(profile);
			}
		}
		else {
			int answer = SWT.NO;
			if (session.isRestorable()) {
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

			IConsole console = getConsole(profile, session);

			if (answer == SWT.YES) {
				session.restore();
			}
			else if (answer == SWT.NO) {
				session.start();
			}
			
			if (answer != SWT.CANCEL) {
				ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
			}
		}
		return null;
	}

	/**
	 * Try to get the existing console. If it's not found, a new message console
	 * will be created and attaches to the given session.
	 * 
	 * @param profile
	 * @param session
	 * @return
	 */
	private IConsole getConsole(Profile profile, BrowsingSession session) {
		IConsole console = null;
		IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
		for (IConsole con : consoles) {
			if (con.getName() == profile.getProfileName()) {
				console = con;
				break;
			}
		}
		if (console == null) {
			console = new MessageConsole(profile.getProfileName(), null);
			final MessageConsoleStream consoleStream = ((MessageConsole)console).newMessageStream();
			ConsoleLogger logger = new ConsoleLogger() {
				@Override
				public void println(String message) {
					consoleStream.println(message);
				}
			};
			session.attach(logger);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
		}
		return console;
	}
}
