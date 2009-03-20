/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 30, 2008 12:34:20 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class WorkspaceManager {
	
	private static final String WORKSPACE_SEPARATOR = "\n";
	
	private String[] workspaces;
	private String selection;

	public void setRecentWorkspaces(String recentWorkspaces) {
		StringTokenizer tokenizer = new StringTokenizer(recentWorkspaces, WORKSPACE_SEPARATOR);
		List<String> workspaces = new ArrayList<String>();
		while (tokenizer.hasMoreElements()) {
			String directory = tokenizer.nextToken();
			File workspaceFolder = new File(directory);
			workspaces.add(workspaceFolder.getAbsolutePath());
		}
		selection = workspaces.size() > 0 ? workspaces.get(0) : null;
		setWorkspaces(workspaces.toArray(new String[workspaces.size()]));
	}

	public String getRecentWorkspaces() {
		StringBuilder recentWorkspaces = new StringBuilder();
		if (selection != null) {
			recentWorkspaces.append(selection + WORKSPACE_SEPARATOR);
		}
		String[] workspaces = getWorkspaces();
		for (String workspace : workspaces) {
			if (!workspace.equals(selection)) {
				recentWorkspaces.append(workspace + WORKSPACE_SEPARATOR);
			}
		}
		return recentWorkspaces.toString();
	}

	public String getSelection() {
		return selection;
	}

	public String[] getWorkspaces() {
		return workspaces;
	}

	public void setWorkspaces(String[] workspaces) {
		this.workspaces = workspaces;
	}
	
	public void setWorkspace(String workspacePath) {
		selection = workspacePath;
	}
}
