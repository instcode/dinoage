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
	private YBackupState state;

	public static final void main(String[] args) throws IOException {
		DinoAge dinoage = new DinoAge();
		if (!dinoage.chooseWorkspace(null)) {
			System.exit(0);
		}
		
		YLoginDlg yLoginDlg = new YLoginDlg();
		//yLoginDlg.open();

		dinoage.initialize(yLoginDlg.getCookieStore());

		DinoAgeWindow mainWindow = new DinoAgeWindow(dinoage);
		dinoage.getSession().getConnectionModel().registerConnectionListener(mainWindow);
		dinoage.getSession().registerSessionListener(mainWindow);
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

	public Workspace getWorkspace() {
		return workspace;
	}
	
	/**
	 * Stop current job and store resumable information..
	 */
	public void stop() {
		session.pause();
	}

	public void initialize(CookieStore cookieStore) throws IOException {
		session = new Session<YBackupState>(ResourceManager.getMessage(ResourceManager.KEY_ENCODING), cookieStore, null);
		session.setConnectionModel(new SingleConnectionModel(session.getHttpClient()));
	}

	/**
	 * Start backup the profile at the given state
	 * @param state 
	 */
	public void backup(YBackupState state) {
		Profile profile = state.getProfile();
		workspace.setActiveProfile(profile);
		
		String profileId = state.getProfileId();
		session.setState(state);

		if (profile.isBackupEntry()) {
			String blogURL = ResourceManager.KEY_BLOG_URL + profileId + ResourceManager.KEY_BLOG_LIST_PARAMETER_VALUE;
			session.queueRequest(blogURL);
		}
		if (profile.isBackupGuestbook()) {
			String guestbookURL = ResourceManager.KEY_GUESTBOOK_URL + profileId;
			session.queueRequest(guestbookURL);
		}
		
		session.start();
		this.state = state;
	}

	public boolean isRunning() {
		return session.isRunning();
	}
	
	public Session<YBackupState> getSession() {
		return session;
	}

	public YBackupState getActiveState() {
		return state;
	}
	
	public YBackupState getState(String profileName) {
		Profile profile = workspace.getProfile(profileName);
		if (profile == null) {
			return null;
		}

		YBackupState state = new YBackupState(profile);
		state.initialize(session);
		return state;
	}
}