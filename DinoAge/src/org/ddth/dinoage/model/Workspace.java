/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 29, 2008 9:32:44 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.dinoage.ResourceManager;

public class Workspace implements Runnable {
	private Log logger = LogFactory.getLog(Workspace.class);
	private Properties map;
	private File workspaceFolder;
	
	public Workspace(File workspaceFolder) {
		this.workspaceFolder = workspaceFolder;
		Thread monitor = new Thread(this);
		monitor.setDaemon(true);
	}
	
	public void loadWorkspace() throws IOException {
		map = new Properties();
		File resumeFile = new File(workspaceFolder, ResourceManager.RESUME_FILE_NAME);
		if (resumeFile.canRead()) {
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(resumeFile);
				map.load(inputStream);
			}
			finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		}
		else {
			resumeFile.createNewFile();
		}
	}

	public void saveWorkspace() throws IOException {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(new File(ResourceManager.RESUME_FILE_NAME));
			map.store(outputStream, ResourceManager.getMessage(ResourceManager.KEY_WORKSPACE_RESUME_FILE_HEADER));
		}
		finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	public void closeWorkspace() {
	}
	
	public void lock() {
		try {
			RandomAccessFile workspaceFile = new RandomAccessFile(new File(workspaceFolder, ".lock"), "rw");
			FileChannel channel = workspaceFile.getChannel();
			// Use the file channel to create a lock on the file.
			// This method blocks until it can retrieve the lock.
			FileLock lock = channel.lock();

			// Try acquiring the lock without blocking. This method returns
			// null or throws an exception if the file is already locked.
			try {
				lock = channel.tryLock();
			}
			catch (OverlappingFileLockException e) {
				// File is already locked in this thread or virtual machine
			}

			// Release the lock
			lock.release();

			// Close the file
			channel.close();
		}
		catch (IOException e) {
			logger.debug(e);
		}
	}
	
	public File getFolder() {
		return workspaceFolder;
	}

	public void addProfile(String profileId, String profilePath) {
		map.put(profileId, profilePath);
	}

	public String getProfilePath(String profileId) {
		return map.getProperty(profileId);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
