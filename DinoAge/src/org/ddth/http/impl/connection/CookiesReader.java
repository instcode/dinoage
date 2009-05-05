/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.impl.connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CookiesReader {
	private CookieStore cookieStore;

	/**
	 * Pick <em>sessionstore.js</em> where Mozilla Firefox stores all session cookies. If
	 * you have more than one <a
	 * href="http://support.mozilla.com/en-US/kb/Profiles">profiles</a> in
	 * default profiles location, this method will try to get the
	 * <em>sessionstore.js</em> in the default profile (xxxxxxxx.default).
	 * 
	 * @return The <em>sessionstore.js</em> file in a Mozilla Firefox profile folder.
	 */
	private File getSessionStore() {
		String os = System.getProperty("os.name").toLowerCase();
		File profilesFolder = new File("");
		if (os.indexOf("win") >= 0) {
			profilesFolder = new File(System.getenv("APPDATA") + "\\Mozilla\\Firefox\\Profiles");
		} else if (os.indexOf("mac") >= 0) {
			profilesFolder = new File("~/Library/Application Support/Firefox/Profiles/");
		} else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
			profilesFolder = new File("~/.mozilla/firefox");
		}

		File sessionFile = null;
		String[] children = profilesFolder.list();

		// The algorithm simply searches through all sub folders in default
		// profiles location and check to see if each of them contains a
		// "sessionstore.js" file. If that file exists, the folder appears
		// to be a valid Firefox profile and it will be picked out.
		for (String child : children) {
			File profileFolder = new File(profilesFolder, child);
			File file = new File(profileFolder, "sessionstore.js");
			if (file.exists()) {
				if (sessionFile == null) {
					sessionFile = file;
				}
				if (child.endsWith("default")) {
					sessionFile = file;
					break;
				}
			}
		}
		return sessionFile;
	}

	/**
	 * Read all the cookies which are stored in the session store and store in
	 * an object that the HttpClient can understand.<br>
	 * <br>
	 * 
	 * @return A {@link CookieStore} object which contains all cookies from your
	 *         current browser.
	 */
	public CookieStore readBrowserCookies() {
		cookieStore = new BasicCookieStore();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(getSessionStore()));
			String s = reader.readLine();
			JSONArray a = new JSONArray(s);
			JSONObject o = a.getJSONObject(0);
			JSONArray windows = o.getJSONArray("windows");
			JSONArray cookies = windows.getJSONObject(0).getJSONArray("cookies");
			for (int i = 0; i < cookies.length(); i++) {
				JSONObject session = cookies.getJSONObject(i);
				String host = session.getString("host");
				String name = session.getString("name");
				String value = session.getString("value");
				String path = session.getString("path");
				addCookie(host, name, value, path);
			}
		} catch (IOException e) {
		} catch (JSONException e) {
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
		return cookieStore;
	}

	private void addCookie(String host, String name, String value, String path) {
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setDomain(host);
		cookie.setPath(path);
		cookie.setExpiryDate(new Date(System.currentTimeMillis() + 31536000000L));
		cookieStore.addCookie(cookie);
	}
}
