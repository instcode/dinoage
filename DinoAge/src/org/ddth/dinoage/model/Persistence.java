/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 1, 2008 8:23:13 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Persistence {

	public static final int GUESTBOOK = 0;
	public static final int BLOG_ENTRY = 1;
	
	private static final String[] CATEGORIES = {"guestbook", "entry"};
	private File[] folders = new File[2];
	
	public Persistence(File profileFolder) {
		for (int i = 0; i < CATEGORIES.length; i++) {
			folders[i] = new File(profileFolder, CATEGORIES[i]);
			folders[i].mkdirs();
		}
	}

	public void write(InputStream inputStream, int category, String tail) {
		OutputStream outputStream = null;
		try {
			File outputFile = new File(folders[category], CATEGORIES[category] + "-" + tail + ".html");
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
			e.printStackTrace();
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
}
