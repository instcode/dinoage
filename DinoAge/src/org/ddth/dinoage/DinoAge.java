/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.dinoage.grabber.BrowsingSession;
import org.ddth.dinoage.grabber.yahoo.YBrowsingSession;
import org.ddth.dinoage.model.Profile;
import org.ddth.dinoage.model.ProfileLoader;
import org.ddth.dinoage.model.Workspace;
import org.ddth.dinoage.model.WorkspaceChangeListener;
import org.ddth.dinoage.model.WorkspaceManager;
import org.ddth.dinoage.ui.widget.ChooseWorkspaceDlg;
import org.ddth.http.core.Session;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class DinoAge {
	private Log logger = LogFactory.getLog(DinoAge.class);
	private List<Session> sessions = new CopyOnWriteArrayList<Session>();
	private Workspace workspace;
	private ProfileLoader profileLoader = new YBrowsingSession.YProfileLoader();
	private WorkspaceChangeListener listener;

	public boolean isRunning() {
		boolean isRunning = false;
		for (Session session : sessions) {
			if (session.isRunning()) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
	
	public void stop() {
		for (Session session : sessions) {
			session.shutdown();
		}
		sessions.clear();
	}
	
	public Workspace getWorkspace() {
		return workspace;
	}

	public BrowsingSession createSession(Profile profile) {
		//FIXME It's better to lookup an existing session other than to create/add a new one
		BrowsingSession session = (profile == null) ? null : new YBrowsingSession(profile, workspace);
		sessions.add(session);
		return session;
	}

	/**
	 * Select the most recently opened workspace in the given
	 * {@link WorkspaceManager}. If new workspace is loaded
	 * successfully, current workspace will be closed. If any
	 * error happens, the current workspace won't change.<br>
	 * <br>
	 * @param workspaces
	 * @return
	 * 		<code>true</code> if new workspace is loaded successfully.
	 */
	public boolean selectWorkspace(WorkspaceManager workspaces) {
		// Save current workspace...
		Workspace currentWorkspace = workspace;
		try {
			if (workspaces.getSelection() != null) {
				workspace = new Workspace(new File(workspaces.getSelection()), profileLoader);
				workspace.loadWorkspace();
				return true;
			}
		}
		catch (IOException e) {
			logger.error("Error when selecting workspace", e);
			workspace = currentWorkspace;
			currentWorkspace = null;
		}
		finally {
			// Close current workspace if it's okay
			if (currentWorkspace != null) {
				currentWorkspace.closeWorkspace();
			}
		}
		return false;
	}
	
	/**
	 * Bring up a Workspace Chooser dialog which doesn't associate with
	 * any parent window.<br>
	 * <br> 
	 * @see #chooseWorkspace(Shell, WorkspaceManager)
	 */
	public boolean chooseWorkspace(WorkspaceManager workspaces) {
		return chooseWorkspace(null, workspaces);
	}
	
	/**
	 * Show Workspace Chooser dialog and preinitialize recent workspaces
	 * by using the given {@link WorkspaceManager} information.<br>
	 * <br>
	 * @param parent A parent shell of this dialog
	 * @param workspaces A manager that contains a list of recently opened
	 * workspaces.
	 * @return
	 * 		<code>true</code> if there is at least one workspace
	 * loaded successfully. 		
	 */
	public boolean chooseWorkspace(Shell parent, WorkspaceManager workspaces) {
		ChooseWorkspaceDlg dlg = new ChooseWorkspaceDlg(parent, workspaces);
		boolean success = false;
		if (dlg.open() == SWT.OK) {
			success = selectWorkspace(workspaces);
		}
		return success;
	}
}