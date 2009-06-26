/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 29, 2008 9:32:44 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Workspace {
	private static final Log logger = LogFactory.getLog(Workspace.class);
	
	private static final String LOCK_FILE = ".lock";
	private static final String PROFILE_FILE_NAME = ".profile";
		
	private Map<String, Profile> map = new HashMap<String, Profile>();
	private File workspaceFolder;
	private FileLock lock;
	private ProfileLoader profileLoader;
	private List<WorkspaceChangeListener> listeners = new ArrayList<WorkspaceChangeListener>();

	public static final int WORKSPACE_IS_AVAILABLE = 0;
	public static final int WORKSPACE_IS_INVALID = 1;
	public static final int WORKSPACE_IS_BEING_USED = 2;
	
	public Workspace(File workspaceFolder, ProfileLoader loader) {
		this.workspaceFolder = workspaceFolder;
		this.profileLoader = loader;
	}

	/**
	 * Check if the given workspace by its path is available for using
	 *  
	 * @param workspacePath
	 * @return
	 */
	public static int checkWorkspace(String workspacePath) {
		File workspaceFolder = new File(workspacePath);
		if (!workspaceFolder.exists() || !workspaceFolder.isDirectory()) {
			return WORKSPACE_IS_INVALID;
		}
		File lockFile = new File(workspaceFolder, LOCK_FILE);
		if (!lockFile.exists() || lockFile.delete()) {
			return WORKSPACE_IS_AVAILABLE;
		}
		return WORKSPACE_IS_BEING_USED;
	}
	
	public void closeWorkspace() {
		releaseExclusiveAccess(workspaceFolder);
	}
	
	private void releaseExclusiveAccess(File workspaceFolder) {
		try {
			lock.release();
			lock.channel().close();
			new File(workspaceFolder, LOCK_FILE).delete();
		}
		catch (IOException e) {
			logger.error(e);
		}
	}
	
	private FileLock aqquireExclusiveAccess(File workspaceFolder) {
		FileLock lock = null;
		try {
			File lockFile = new File(workspaceFolder, LOCK_FILE);
			lockFile.deleteOnExit();
			RandomAccessFile workspaceFile = new RandomAccessFile(lockFile, "rw");
			FileChannel channel = workspaceFile.getChannel();
			lock = channel.tryLock();
		}
		catch (IOException e) {
			logger.error(e);
		}
		return lock;
	}
	
	public String getWorkspaceLocation() {
		return workspaceFolder.getAbsolutePath();
	}

	public void loadWorkspace() throws IOException {
		lock = aqquireExclusiveAccess(workspaceFolder);
		if (lock == null) {
			throw new IOException("Workspace is in used.");
		}
		File[] children = workspaceFolder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		
		for (File directory : children) {
			try {
				File profileFile = new File(directory, Workspace.PROFILE_FILE_NAME);
				Profile profile = profileLoader.loadProfile(profileFile);
				// Override the name in profile
				profile.setProfileName(directory.getName());
				map.put(profile.getProfileName().toLowerCase(), profile);
			}
			catch (Exception e) {
				logger.debug("'" + directory.getName() + "' directory doesn't appear as a valid profile storage");
			}
		}
		fireWorkspaceReloaded();
	}
	
	public boolean saveProfile(Profile profile) {
		boolean success = false;
		OutputStream outputStream = null;
		try {
			File profileFolder = getProfileFolder(profile);
			profileFolder.mkdirs();
			File resumeFile = new File(profileFolder, Workspace.PROFILE_FILE_NAME);
			boolean exists = resumeFile.exists();
			outputStream = profile.store(resumeFile);
			map.put(profile.getProfileName().toLowerCase(), profile);
			if (!exists) {
				fireProfileAdded(profile);
			}
			success = true;
		}
		catch (IOException e) {
			logger.error("Can not save profile '" + profile.getProfileName() + "'", e);
		}
		finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			}
			catch (IOException e) {
				logger.error(e);
			}
		}
		return success;
	}
	
	public File getProfileFolder(Profile profile) {
		return new File(workspaceFolder, profile.getProfileName());
	}

	public Profile removeProfile(Profile profile) {
		File profileFile = new File(new File(workspaceFolder, profile.getProfileName()), Workspace.PROFILE_FILE_NAME);
		profileFile.delete();
		Profile success = map.remove(profile.getProfileName().toLowerCase());
		if (success != null) {
			fireProfileRemoved(profile);
		}
		return success;
	}
	
	public Profile getProfile(String profileName) {
		return map.get(profileName.toLowerCase());
	}

	public Collection<Profile> getProfiles() {
		return map.values();
	}

	public ProfileLoader getProfileLoader() {
		return profileLoader;
	}

	public Profile createEmptyProfile() {
		return profileLoader.createProfile();
	}
	
	public void addWorkspaceChangeListener(WorkspaceChangeListener listener) {
		listeners.add(listener);
	}
	
	public boolean removeWorkspaceChangeListener(WorkspaceChangeListener listener) {
		return listeners.remove(listener);
	}
	
	private void fireWorkspaceChanged(WorkspaceChangeEvent event) {
		for (WorkspaceChangeListener listener : listeners) {
			listener.workspaceChanged(event);
		}
	}
	
	private void fireProfileAdded(Profile profile) {
		WorkspaceChangeEvent event = new WorkspaceChangeEvent(this, profile, WorkspaceChangeEvent.PROFILE_ADDED_CHANGE);
		fireWorkspaceChanged(event);
	}
	
	private void fireProfileRemoved(Profile profile) {
		WorkspaceChangeEvent event = new WorkspaceChangeEvent(this, profile, WorkspaceChangeEvent.PROFILE_REMOVED_CHANGE);
		fireWorkspaceChanged(event);
	}
	
	private void fireWorkspaceReloaded() {
		WorkspaceChangeEvent event = new WorkspaceChangeEvent(this, null, WorkspaceChangeEvent.WORKSPACE_RELOADED_CHANGE);
		fireWorkspaceChanged(event);
	}
}
