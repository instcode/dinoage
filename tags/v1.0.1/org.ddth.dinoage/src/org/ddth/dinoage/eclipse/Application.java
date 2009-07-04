/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 29, 2008 9:32:44 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.eclipse;

import org.ddth.dinoage.DinoAge;
import org.ddth.dinoage.DinoAgeSettings;
import org.ddth.dinoage.core.WorkspaceManager;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = loadWorkspace();
			if (returnCode == PlatformUI.RETURN_OK) {
				returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			}
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
		
	}

	private int loadWorkspace() throws BackingStoreException {
		int returnCode = PlatformUI.RETURN_OK;
		Preferences prefs = new ConfigurationScope().getNode(Activator.PLUGIN_ID);
		boolean showChooserDialog = prefs.getBoolean(DinoAgeSettings.PROPERTY_WORKSPACE_SHOW_CHOOSER_DIALOG, true);
		String recentWorkspaces = prefs.get(DinoAgeSettings.PROPERTY_WORKSPACE_RECENTS, "");
		
		DinoAge dinoage = Activator.getDefault().getDinoAge();
		WorkspaceManager workspaces = new WorkspaceManager(recentWorkspaces);
		// Try to pick up a workspace in the recent workspace list.
		// If the workspace cannot be loaded, the workspace chooser
		// dialog will be showed...
		if (showChooserDialog || !dinoage.selectWorkspace(workspaces)) {
			if (dinoage.chooseWorkspace(null, workspaces)) {
				recentWorkspaces = workspaces.getSelection();
				// Save workspace...
				prefs.put(DinoAgeSettings.PROPERTY_WORKSPACE_RECENTS, recentWorkspaces);
				prefs.sync();
			}
			else {
				returnCode = PlatformUI.RETURN_UNSTARTABLE;
			}
		}
		return returnCode;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return;
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
