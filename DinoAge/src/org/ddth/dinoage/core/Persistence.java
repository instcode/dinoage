/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 1, 2008 8:23:13 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.http.impl.connection.ThreadPoolConnectionModel;

public class Persistence {
	private static final Log logger = LogFactory.getLog(ThreadPoolConnectionModel.class);
	
	private File[] folders;
	private String[] categories;
	
	public Persistence(File profileFolder, String[] categories) {
		folders = new File[categories.length];
		for (int i = 0; i < categories.length; i++) {
			folders[i] = new File(profileFolder, categories[i]);
			folders[i].mkdirs();
		}
		this.categories = categories;
	}
	
	protected File getFolder(int index) {
		return folders[index];
	}

	private File generateFile(File directory, String name, String extension) {
		File outputFile = null;
		int index = 1;
		outputFile = new File(directory, name + "." + extension);
		while (outputFile.exists()) {
			outputFile = new File(directory, name + "-" + index + "." + extension);
			index++;
		}
		return outputFile;
	}
	
	public void write(InputStream inputStream, int category, String tail) {
		OutputStream outputStream = null;
		try {
			File outputFile = generateFile(folders[category], categories[category] + "-" + tail, "html");
			outputStream = new FileOutputStream(outputFile);
			byte[] buffer = new byte[4096];
			int bytesread = 0;
			do {
				bytesread = inputStream.read(buffer, 0, 4096);
				if (bytesread > 0) {
					outputStream.write(buffer, 0, bytesread);
				}
			}
			while (bytesread > 0);
		}
		catch (IOException e) {
			logger.error("Error", e);
		}
		finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				}
				catch (IOException e) {
					logger.error("Error", e);
				}
			}
		}
	}
	
	public byte[] read(InputStream inputStream) {
		ByteArrayOutputStream savedBytes = new ByteArrayOutputStream(64000);
		try {
			byte[] buffer = new byte[4096];
			int bytesread = 0;
			do {
				bytesread = inputStream.read(buffer, 0, 4096);
				if (bytesread > 0) {
					savedBytes.write(buffer, 0, bytesread);
				}
			} while (bytesread > 0);
		}
		catch (IOException e) {
			logger.error("Error", e);
		}
		return savedBytes.toByteArray();
	}
}
