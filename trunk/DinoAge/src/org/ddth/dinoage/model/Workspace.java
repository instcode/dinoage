/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 29, 2008 9:32:44 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.model;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Workspace {
	private static final String LOCK_FILE = ".lock";
	private static final String PROFILE_FILE_NAME = ".profile";
		
	private static final Log logger = LogFactory.getLog(Workspace.class);
	private Map<String, Profile> map = new HashMap<String, Profile>();
	private File workspaceFolder;
	private FileLock lock;

	public Workspace(File workspaceFolder) {
		this.workspaceFolder = workspaceFolder;
	}
	
	/**
	 * Check if the given workspace by its path is available for using
	 *  
	 * @param workspacePath
	 * @return
	 */
	public static boolean checkWorkspace(String workspacePath) {
		File workspaceFolder = new File(workspacePath);
		if (!workspaceFolder.exists() || !workspaceFolder.isDirectory()) {
			return false;
		}
		File lockFile = new File(workspaceFolder, LOCK_FILE);
		if (!lockFile.exists() || lockFile.delete()) {
			return true;
		}
		return false;
	}
	
	public void loadWorkspace() throws IOException {
		lock = aqquireExclusiveAccess(workspaceFolder);
		if (lock == null) {
			throw new IOException("Workspace is in used.");
		}
		File[] children = workspaceFolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		
		for (File directory : children) {
			try {
				File profileFile = new File(directory, Workspace.PROFILE_FILE_NAME);
				Profile profile = new Profile();
				profile.load(profileFile);
				// Override the name in profile
				profile.setProfileName(directory.getName());
				map.put(profile.getProfileName().toLowerCase(), profile);
			}
			catch (Exception e) {
				logger.debug("'" + directory.getName() + "' directory doesn't appear as a valid profile storage", e);
			}
		}
	}
	
	public boolean saveProfile(Profile profile) {
		boolean success = false;
		OutputStream outputStream = null;
		try {
			File profileFolder = getProfileFolder(profile);
			profileFolder.mkdirs();
			File resumeFile = new File(profileFolder, Workspace.PROFILE_FILE_NAME);
			profile.store(resumeFile);
			map.put(profile.getProfileName().toLowerCase(), profile);
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

	public void closeWorkspace() {
		releaseExclusiveAccess();
	}
	
	private void releaseExclusiveAccess() {
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
	
	public File getProfileFolder(Profile profile) {
		return new File(workspaceFolder, profile.getProfileName());
	}

	public Profile removeProfile(String profileName) {
		File profileFile = new File(new File(workspaceFolder, profileName), Workspace.PROFILE_FILE_NAME);
		profileFile.delete();
		return map.remove(profileName.toLowerCase());
	}
	
	public Profile getProfile(String profileName) {
		return map.get(profileName.toLowerCase());
	}

	public Profile[] getProfiles() {
		return map.values().toArray(new Profile[map.size()]);
	}
}
