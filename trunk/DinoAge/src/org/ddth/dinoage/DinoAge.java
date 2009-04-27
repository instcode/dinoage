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
import org.ddth.dinoage.grabber.yahoo.YBrowsingSession;
import org.ddth.dinoage.grabber.yahoo.handler.YBlogEntryContentHandler;
import org.ddth.dinoage.grabber.yahoo.handler.YEntryListContentHandler;
import org.ddth.dinoage.model.Profile;
import org.ddth.dinoage.model.Workspace;
import org.ddth.dinoage.model.WorkspaceManager;
import org.ddth.dinoage.ui.DinoAgeWindow;
import org.ddth.dinoage.ui.widget.ChooseWorkspaceDlg;
import org.ddth.http.core.Session;
import org.ddth.http.core.content.handler.ChainContentHandler;
import org.ddth.http.core.content.handler.ContentHandlerDispatcher;
import org.ddth.http.impl.content.handler.WebpageContentHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class DinoAge {
	private static final String CONFIG_FILE_PATH = "dinoage.conf";
	
	private Log logger = LogFactory.getLog(DinoAge.class);
	private List<Session> sessions = new CopyOnWriteArrayList<Session>();
	private DinoAgeSettings settings;
	private Workspace workspace;
	private DinoAgeWindow mainWindow;

	private ContentHandlerDispatcher dispatcher;

	public static final void main(String[] args) {
		ResourceManager.createResources();
		DinoAge dinoage = new DinoAge();
		dinoage.run();
		ResourceManager.disposeResources();
 	}

	private void run() {
		settings = new DinoAgeSettings(CONFIG_FILE_PATH);
		try {
			settings.loadConfiguration();
		}
		catch (IOException e) {
		}
		
		if (!chooseWorkspace()) {
			System.exit(0);
		}
		
		dispatcher = createDispatcher();
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
	}
	
	public ContentHandlerDispatcher getDispatcher() {
		return dispatcher;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public DinoAgeWindow getMainWindow() {
		return mainWindow;
	}
	
	private ContentHandlerDispatcher createDispatcher() {
		WebpageContentHandler webHandler = new WebpageContentHandler();
		
		ChainContentHandler frontHandler = new ChainContentHandler();
		frontHandler.add(webHandler);
		frontHandler.add(new YEntryListContentHandler());
		
		ChainContentHandler entryHandler = new ChainContentHandler();
		entryHandler.add(webHandler);
		entryHandler.add(new YBlogEntryContentHandler());
		
		ContentHandlerDispatcher dispatcher = new ContentHandlerDispatcher();
		dispatcher.registerHandler("http://.*/blog-.*\\?.*p=(\\d+).*", entryHandler);
		dispatcher.registerHandler("http://.*/blog-[^?]*", frontHandler);
		return dispatcher;
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
			String recentWorkspaces = settings.getRecentWorkspaces();
			WorkspaceManager workspaces = new WorkspaceManager();
			workspaces.setRecentWorkspaces(recentWorkspaces);
			ChooseWorkspaceDlg dlg = new ChooseWorkspaceDlg(parent, workspaces);
			if (dlg.open() == SWT.OK) {
				settings.setRecentWorkspaces(workspaces.getRecentWorkspaces());
				settings.saveConfiguration();
				if (workspace != null) {
					workspace.closeWorkspace();
				}
				workspace = new Workspace(new File(workspaces.getSelection()), new DinoAgeProfileLoader());
				workspace.loadWorkspace();
				success = true;
			}
		}
		catch (IOException e) {
			logger.error(e);
		}
		return success;
	}

	public YBrowsingSession createSession(String profileName) {
		Profile profile = getWorkspace().getProfile(profileName);
		//FIXME It's better to lookup an existing session other than create/add a new one
		YBrowsingSession session = (profile == null) ? null : new YBrowsingSession(
				profile, getWorkspace(), getDispatcher());
		session.registerConnectionListener(getMainWindow());
		sessions.add(session);
		return session;
	}
}