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

public class Persistence {
	private static final Log logger = LogFactory.getLog(Persistence.class);
	
	public void write(InputStream inputStream, File outputFile) {
		outputFile.getParentFile().mkdirs();
		OutputStream outputStream = null;
		try {
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
			}
			while (bytesread > 0);
		}
		catch (IOException e) {
			logger.error("Error", e);
		}
		return savedBytes.toByteArray();
	}
}
