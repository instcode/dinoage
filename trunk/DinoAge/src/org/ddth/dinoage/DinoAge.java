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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.dinoage.grabber.yahoo.YBackupState;
import org.ddth.dinoage.model.Profile;
import org.ddth.dinoage.model.Workspace;
import org.ddth.dinoage.model.WorkspaceManager;
import org.ddth.dinoage.ui.DinoAgeChooseWorkspaceDlg;
import org.ddth.dinoage.ui.DinoAgeWindow;
import org.ddth.http.core.Session;
import org.ddth.http.core.content.handler.ChainContentHandler;
import org.ddth.http.core.content.handler.ContentHandlerDispatcher;
import org.ddth.http.impl.ThreadPoolSession;
import org.ddth.http.impl.content.handler.WebpageContentHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class DinoAge {
	private static final String CONFIG_FILE_PATH = "dinoage.conf";
	
	private Log logger = LogFactory.getLog(DinoAge.class);
	private Session session;
	private DinoAgeSettings settings;
	private Workspace workspace;

	public static final void main(String[] args) {
		DinoAge dinoage = new DinoAge();
		dinoage.run();
 	}

	private void run() {
		settings = new DinoAgeSettings(CONFIG_FILE_PATH);
		try {
			settings.loadConfiguration();
		}
		catch (IOException e) {
		}
		
		if (!chooseWorkspace(null)) {
			System.exit(0);
		}
		
		DinoAgeWindow mainWindow = new DinoAgeWindow(this);
		ChainContentHandler handler = new ChainContentHandler();
		handler.add(new WebpageContentHandler());
		
		ContentHandlerDispatcher dispatcher = new ContentHandlerDispatcher();
		dispatcher.registerHandler(".*", handler);

		session = new ThreadPoolSession(dispatcher);
		mainWindow.open();
	}

	public boolean chooseWorkspace(Shell parent) {
		boolean success = false;
		try {
			String recentWorkspaces = settings.getRecentWorkspaces();
			WorkspaceManager workspaces = new WorkspaceManager();
			workspaces.setRecentWorkspaces(recentWorkspaces);
			DinoAgeChooseWorkspaceDlg dlg = new DinoAgeChooseWorkspaceDlg(parent, workspaces);
			if (dlg.open() == SWT.OK) {
				settings.setRecentWorkspaces(workspaces.getRecentWorkspaces());
				settings.saveConfiguration();
				if (workspace != null) {
					workspace.closeWorkspace();
				}
				workspace = new Workspace(new File(workspaces.getSelection()));
				workspace.loadWorkspace();
				success = true;
			}
		}
		catch (IOException e) {
			logger.error(e);
		}
		return success;
	}

	/**
	 * Start backup the profile from the given state
	 * @param state 
	 */
	public void backup(YBackupState state) {
		session.start();
		
		state.initialize(session);
		
		String profileId = state.getProfileId();
		//session.queueRequest(new Request(YahooBlog.YAHOO_360_BLOG_URL + profileId));
		//session.queueRequest(new Request(YahooBlog.YAHOO_360_GUESTBOOK_URL + profileId));
		
		workspace.saveProfile(state.getProfile());
	}

	public boolean isRunning() {
		return session.isRunning();
	}
	
	public void stop() {
		session.shutdown();
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public Profile getActiveProfile() {
		YBackupState state = null;
		return state != null ? state.getProfile() : null;
	}
	
	/**
	 *  Save all resumable information of the current state
	 */
	public void saveState() {
		YBackupState activeState = null;
		activeState.syncState();
		workspace.saveProfile(activeState.getProfile());
	}
}