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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.blogging.yahoo.grabber.YBrowsingSession;
import org.ddth.dinoage.core.BrowsingSession;
import org.ddth.dinoage.core.Profile;
import org.ddth.dinoage.core.ProfileFactory;
import org.ddth.dinoage.core.Workspace;
import org.ddth.dinoage.core.WorkspaceManager;
import org.ddth.dinoage.eclipse.ui.widget.ChooseWorkspaceDlg;
import org.ddth.http.core.Session;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class DinoAge {
	private Log logger = LogFactory.getLog(DinoAge.class);
	private Map<Profile, BrowsingSession> sessions = new HashMap<Profile, BrowsingSession>();
	private Workspace workspace;
	private ProfileFactory profileLoader = new YBrowsingSession.YProfileLoader();

	public boolean isRunning() {
		boolean isRunning = false;
		for (Session session : sessions.values()) {
			if (session.isRunning()) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
	
	public void stop() {
		for (Session session : sessions.values()) {
			session.shutdown();
		}
		sessions.clear();
	}
	
	public Workspace getWorkspace() {
		return workspace;
	}

	public BrowsingSession createSession(Profile profile) {
		BrowsingSession session = sessions.get(profile);
		if (session == null) {
			session = new YBrowsingSession(profile, workspace);
			sessions.put(profile, session);
		}
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