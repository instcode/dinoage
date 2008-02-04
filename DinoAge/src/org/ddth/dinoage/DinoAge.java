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
import org.apache.http.client.CookieStore;
import org.ddth.dinoage.grabber.yahoo.YBackupState;
import org.ddth.dinoage.grabber.yahoo.YahooRequestFactory;
import org.ddth.dinoage.model.Profile;
import org.ddth.dinoage.model.Workspace;
import org.ddth.dinoage.model.WorkspaceManager;
import org.ddth.dinoage.ui.DinoAgeChooseWorkspaceDlg;
import org.ddth.dinoage.ui.DinoAgeWindow;
import org.ddth.dinoage.ui.YLoginDlg;
import org.ddth.grabber.core.connection.Session;
import org.ddth.grabber.impl.connection.SingleConnectionModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class DinoAge {
	private static final String CONFIG_FILE_PATH = "dinoage.conf";
	
	private Log logger = LogFactory.getLog(DinoAge.class);
	private Session<YBackupState> session;
	private Workspace workspace;

	public static final void main(String[] args) throws IOException {
		DinoAge dinoage = new DinoAge();
		if (!dinoage.chooseWorkspace(null)) {
			System.exit(0);
		}
		
		YLoginDlg yLoginDlg = new YLoginDlg();
		//yLoginDlg.open();

		dinoage.initialize(yLoginDlg.getCookieStore());

		DinoAgeWindow mainWindow = new DinoAgeWindow(dinoage);
		dinoage.session.getConnectionModel().registerConnectionListener(mainWindow);
		dinoage.session.registerSessionListener(mainWindow);
		mainWindow.open();
 	}

	public boolean chooseWorkspace(Shell parent) {
		boolean success = false;
		try {
			WorkspaceManager workspaces = new WorkspaceManager(CONFIG_FILE_PATH);
			workspaces.loadConfiguration();
			DinoAgeChooseWorkspaceDlg dlg = new DinoAgeChooseWorkspaceDlg(parent, workspaces);
			if (dlg.open() == SWT.OK) {
				if (workspace != null) {
					workspace.closeWorkspace();
				}
				workspace = new Workspace(new File(workspaces.getSelection()));
				workspace.loadWorkspace();
				workspaces.saveConfiguration();
				success = true;
			}
		}
		catch (IOException e) {
			logger.error(e);
		}
		return success;
	}

	public void initialize(CookieStore cookieStore) throws IOException {
		session = new Session<YBackupState>(
				ResourceManager.getMessage(ResourceManager.KEY_ENCODING), cookieStore,
				new YahooRequestFactory());
		session.setConnectionModel(new SingleConnectionModel(session.getHttpClient()));
	}

	/**
	 * Start backup the profile from the given state
	 * @param state 
	 */
	public void backup(YBackupState state) {
		session.setState(state);
		state.initialize(session);
		
		String profileId = state.getProfileId();
		session.queueRequest(ResourceManager.KEY_BLOG_URL + profileId + ResourceManager.KEY_BLOG_LIST_PARAMETER_VALUE);
		session.queueRequest(ResourceManager.KEY_GUESTBOOK_URL + profileId);
		
		workspace.putProfile(state.getProfile());
		
		// Start crawling..
		session.start();
	}

	public boolean isRunning() {
		return session.isRunning();
	}
	
	/**
	 * Inform the current request to stop
	 */
	public void stop() {
		session.pause();
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public Profile getActiveProfile() {
		YBackupState state = session.getState();
		return state != null ? state.getProfile() : null;
	}
	
	public YBackupState createState(String profileName) {
		Profile profile = workspace.getProfile(profileName);
		return (profile != null) ? new YBackupState(profile) : null;
	}

	/**
	 *  Save all resumable information of the current state
	 */
	public void saveState() {
		YBackupState activeState = session.getState();
		activeState.syncState();
		workspace.saveProfile(activeState.getProfile());
	}
}