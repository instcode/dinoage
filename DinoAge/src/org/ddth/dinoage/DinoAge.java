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
import org.ddth.dinoage.model.Workspace;
import org.ddth.dinoage.model.WorkspaceManager;
import org.ddth.dinoage.ui.DinoAgeWindow;
import org.ddth.dinoage.ui.widget.ChooseWorkspaceDlg;
import org.ddth.http.core.Session;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class DinoAge {
	private Log logger = LogFactory.getLog(DinoAge.class);
	private List<Session> sessions = new CopyOnWriteArrayList<Session>();
	private Workspace workspace;
	private DinoAgeWindow mainWindow;

	public static final void main(String[] args) {
		ResourceManager.createResources();
		DinoAge dinoage = new DinoAge();
		dinoage.run();
		ResourceManager.disposeResources();
 	}

	private void run() {
		if (!chooseWorkspace()) {
			System.exit(0);
		}
		
		mainWindow = new DinoAgeWindow(this);
		mainWindow.open();
	}

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

	public DinoAgeWindow getMainWindow() {
		return mainWindow;
	}
	
	public boolean chooseWorkspace() {
		return chooseWorkspace(null);
	}
	
	/**
	 * Bring up workspace chooser. Update configuration file after user
	 * selects a workspace.
	 * 
	 * @param parent
	 * @return
	 */
	public boolean chooseWorkspace(Shell parent) {
		boolean success = false;
		try {
			String recentWorkspaces = DinoAgeSettings.getInstance().getRecentWorkspaces();
			WorkspaceManager workspaces = new WorkspaceManager();
			workspaces.setRecentWorkspaces(recentWorkspaces);
			ChooseWorkspaceDlg dlg = new ChooseWorkspaceDlg(parent, workspaces);
			if (dlg.open() == SWT.OK) {
				DinoAgeSettings.getInstance().setRecentWorkspaces(workspaces.getRecentWorkspaces());
				DinoAgeSettings.getInstance().saveConfiguration();
				if (workspace != null) {
					workspace.closeWorkspace();
				}
				workspace = new Workspace(new File(workspaces.getSelection()), new YBrowsingSession.YProfileLoader());
				workspace.loadWorkspace();
				success = true;
			}
		}
		catch (IOException e) {
			logger.error(e);
		}
		return success;
	}

	public BrowsingSession createSession(Profile profile) {
		//FIXME It's better to lookup an existing session other than to create/add a new one
		BrowsingSession session = (profile == null) ? null : new YBrowsingSession(profile, getWorkspace());
		session.registerConnectionListener(getMainWindow());
		sessions.add(session);
		return session;
	}
}