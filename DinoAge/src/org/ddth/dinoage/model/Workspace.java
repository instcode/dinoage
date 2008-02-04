/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 29, 2008 9:32:44 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.model;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.dinoage.ResourceManager;

public class Workspace {
	private static final String LOCK_FILE = ".lock";
	
	private Log logger = LogFactory.getLog(Workspace.class);
	private Map<String, Profile> map = new HashMap<String, Profile>();
	private File workspaceFolder;
	private FileLock lock;
	
	public Workspace(File workspaceFolder) {
		this.workspaceFolder = workspaceFolder;
	}
	
	public void loadWorkspace() throws IOException {
		lock = aqquireExclusiveAccess();
		if (lock == null) {
			throw new IOException("Workspace is in used.");
		}
		File[] children = workspaceFolder.listFiles();
		for (File child : children) {
			if (!child.isDirectory()) {
				continue;
			}
			Profile profile = new Profile();
			try {
				File resumeFile = new File(child, ResourceManager.RESUME_FILE_NAME);
				profile.load(resumeFile);
				map.put(profile.getProfileName(), profile);
			}
			catch (IOException e) {
				logger.debug(child.getName() + " appears not a valid profile storage", e);
			}
		}
	}
	
	public void saveProfile(Profile profile) {
		OutputStream outputStream = null;
		try {
			File resumeFile = new File(
					new File(workspaceFolder, profile.getProfileName()),
					ResourceManager.RESUME_FILE_NAME);
			profile.store(resumeFile);
		}
		catch (IOException e) {
			logger.debug("Can not save profile '" + profile.getProfileName() + "'", e);
		}
		finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			}
			catch (IOException e) {
				logger.debug(e);
			}
		}
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
			logger.debug(e);
		}
	}
	
	private FileLock aqquireExclusiveAccess() {
		FileLock lock = null;
		try {
			File lockFile = new File(workspaceFolder, LOCK_FILE);
			lockFile.deleteOnExit();
			RandomAccessFile workspaceFile = new RandomAccessFile(lockFile, "rw");
			FileChannel channel = workspaceFile.getChannel();
			lock = channel.tryLock();
		}
		catch (IOException e) {
			logger.debug(e);
		}
		return lock;
	}
	
	public String getWorkspaceLocation() {
		return workspaceFolder.getAbsolutePath();
	}

	public void putProfile(Profile profile) {
		map.put(profile.getProfileName(), profile);
	}

	public Profile removeProfile(String profileName) {
		return map.remove(profileName);
	}
	
	public Profile getProfile(String profileName) {
		return map.get(profileName);
	}

	public Profile[] getProfiles() {
		return map.values().toArray(new Profile[map.size()]);
	}

	public static void main(String[] args) throws Exception {
		final Workspace workspace = new Workspace(new File("."));
		workspace.loadWorkspace();
		new Thread(new Runnable() {

			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				workspace.releaseExclusiveAccess();
				
			}
			
		}).start();
		Thread.sleep(10000);
	}
}
