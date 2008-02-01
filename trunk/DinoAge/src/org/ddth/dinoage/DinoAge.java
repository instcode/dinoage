/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.CookieStore;
import org.ddth.dinoage.grabber.yahoo.handler.EntryListNavigationHandler;
import org.ddth.dinoage.grabber.yahoo.handler.GuestbookNavigationHandler;
import org.ddth.dinoage.model.Profile;
import org.ddth.dinoage.model.WorkspaceManager;
import org.ddth.dinoage.model.Persistence;
import org.ddth.dinoage.model.Workspace;
import org.ddth.dinoage.ui.DinoAgeChooseWorkspaceDlg;
import org.ddth.dinoage.ui.DinoAgeWindow;
import org.ddth.dinoage.ui.YLoginDlg;
import org.ddth.grabber.core.connection.Session;
import org.ddth.grabber.impl.connection.SingleConnectionModel;

public class DinoAge {
	private static final String CONFIG_FILE_PATH = "dinoage.conf";
	
	private Log logger = LogFactory.getLog(DinoAge.class);
	private Session session;
	private Profile currentState;
	private Workspace workspace;

	public static final void main(String[] args) throws IOException {
		DinoAge dinoage = new DinoAge();
		dinoage.openWorkspace();
		
		YLoginDlg yLoginDlg = new YLoginDlg();
		yLoginDlg.open();

		dinoage.initialize(yLoginDlg.getCookieStore());

		DinoAgeWindow mainWindow = new DinoAgeWindow(dinoage);
		dinoage.getSession().getConnectionModel().registerConnectionListener(mainWindow);
		dinoage.getSession().registerSessionListener(mainWindow);
		mainWindow.open();
 	}

	public void openWorkspace() {
		try {
			WorkspaceManager workspaces = new WorkspaceManager(CONFIG_FILE_PATH);
			workspaces.loadConfiguration();
			DinoAgeChooseWorkspaceDlg dlg = new DinoAgeChooseWorkspaceDlg(workspaces);
			dlg.open();
			workspaces.saveConfiguration();
			workspace = new Workspace(new File(workspaces.getSelection()));
		}
		catch (IOException e) {
			logger.error(e);
		}
	}
	
	/**
	 * Stop current job and store resumable information..
	 */
	public void stop() {
		session.pause();
		try {
			workspace.saveWorkspace();
			saveProfile();
		}
		catch (IOException e) {
			logger.debug(e);
		}
	}

	public void initialize(CookieStore cookieStore) throws IOException {
		workspace.loadWorkspace();
		session = new Session(ResourceManager.getMessage(ResourceManager.KEY_ENCODING), cookieStore);
		session.setConnectionModel(new SingleConnectionModel(session.getHttpClient()));
	}
	
	public void saveProfile() throws IOException {
		OutputStream outputStream = null;
		try {
			File resumeFile = new File(
					new File(workspace.getFolder(), currentState.getProfile()),
					ResourceManager.RESUME_FILE_NAME);
			outputStream = new FileOutputStream(resumeFile);
			currentState.store(outputStream);
		}
		finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	public Profile loadProfile(String profilePath) {
		Profile state = new Profile();
		try {
			File profileFolder = new File(workspace.getFolder(), profilePath);
			state.load(profileFolder, session);
		}
		catch (IOException e) {
			state = null;
			logger.debug(e);
		}
		return state;
	}

	/**
	 * Start backup the profile at the given state
	 */
	public void backup() {
		if (currentState == null) {
			throw new IllegalStateException("Cannot resume to a NULL state.");
		}
		String profileId = currentState.getProfileId();
	
		if (profileId != null) {
			workspace.addProfile(profileId, currentState.getProfile());
			try {
				workspace.saveWorkspace();
			}
			catch (IOException e) {
				logger.debug(e);
			}
		}
		session.resume(currentState);
		
		if (currentState.isNewlyCreated()) {
			File profileFolder = new File(workspace.getFolder(), currentState.getProfile());
			Persistence persistence = new Persistence(profileFolder, new int[] {0, 0});
			currentState.initialize(persistence);
			if (currentState.isBackupEntry()) {
				String blogURL = ResourceManager.KEY_BLOG_URL + profileId +
					ResourceManager.KEY_BLOG_LIST_PARAMETER_VALUE;
				session.queueRequest(blogURL, new EntryListNavigationHandler(persistence, session));
			}
			if (currentState.isBackupGuestbook()) {
				String guestbookURL = ResourceManager.KEY_GUESTBOOK_URL + profileId;
				session.queueRequest(guestbookURL, new GuestbookNavigationHandler(persistence, session));
			}
		}
		session.start();
	}

	public Profile getState(final String profileId) {
		String profilePath = workspace.getProfilePath(profileId);
		if (profilePath == null || profilePath.length() == 0) {
			return null;
		}
		return loadProfile(profilePath);
	}

	public void setWorkingState(Profile state) {
		currentState = state;
	}

	public Profile getWorkingState() {
		return currentState;
	}

	public boolean isRunning() {
		return session.isRunning();
	}
	
	public Session getSession() {
		return session;
	}

	public void savePoint() {
		try {
			saveProfile();
		}
		catch (IOException e) {
			logger.error(e);
		}
		int outgoingCount = getWorkingState().getOutgoingMap().size();
		if (outgoingCount == 0) {
			stop();
		}
	}
}