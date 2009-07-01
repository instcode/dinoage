package org.ddth.dinoage.eclipse.ui.handlers;

import org.ddth.dinoage.DinoAge;
import org.ddth.dinoage.DinoAgeSettings;
import org.ddth.dinoage.core.WorkspaceManager;
import org.ddth.dinoage.eclipse.Activator;
import org.ddth.dinoage.eclipse.ui.views.WorkspaceView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class SwitchWorkspaceHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		DinoAge dinoage = Activator.getDefault().getDinoAge();
		Preferences prefs = new ConfigurationScope().getNode(Activator.PLUGIN_ID);
		String recentWorkspaces = prefs.get(DinoAgeSettings.PROPERTY_WORKSPACE_RECENTS, "");
		WorkspaceManager workspaces = new WorkspaceManager(recentWorkspaces);
		if (dinoage.chooseWorkspace(window.getShell(), workspaces)) {
			((WorkspaceView)window.getActivePage().findView(WorkspaceView.ID)).setInput(dinoage.getWorkspace());
			// The current workspace has been changed...
			prefs.put(DinoAgeSettings.PROPERTY_WORKSPACE_RECENTS, workspaces.getRecentWorkspaces());
			try {
				prefs.flush();
			}
			catch (BackingStoreException e) {
			}
		}
		return null;
	}
}
