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

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * In the past, Firefox stored all cookies in plain text file format
 * *cookies.txt*. Since Firefox 3.x, persistent cookies have been moved to
 * *cookies.sqlite* while session cookies were moved to *sessionstore.js*. This
 * makes it even harder to implement a cookies reader. However, because
 * persistent cookies are not important and can be retrieved easily from server
 * at any time, we only need to focus on reading session cookies. Luckily, in
 * sessionstore.js, session cookies are stored in json format and we can read
 * them easily with any json library. And, this {@link CookiesReader} is really
 * that simple Mozilla Firefox cookies (session) store reader.<br>
 * 
 * @author khoa.nguyen
 * 
 */
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
			profilesFolder = new File("~/Library/Application Support/Firefox/Profiles");
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
	 * Firefox 3.5 stores cookies in 2 types of window json objects:
	 * <ol>
	 * <li>windows: Refers to active windows.</li>
	 * <li>_closedWindows: Refers to closed windows.</li>
	 * </ol>
	 * Cookies should be extracted from these objects.
	 * <br>
	 * @return A {@link CookieStore} object which contains all cookies from your
	 *         current browser.
	 */
	public CookieStore readBrowserCookies() {
		cookieStore = new BasicCookieStore();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(getSessionStore()));
			String json = reader.readLine();
			JSONObject jsonObject = new JSONArray(json).getJSONObject(0);
			addCookies(jsonObject, "windows");
			addCookies(jsonObject, "_closedWindows");
		}
		catch (Exception e) {
			// Something's wrong with the reading, but it's okay :-)
		}
		finally {
			try {
				if (reader != null) {
					reader.close();
				}
			}
			catch (IOException e) {
			}
		}
		return cookieStore;
	}

	private void addCookies(JSONObject jsonObject, String window)
			throws JSONException {
		JSONArray windows = jsonObject.getJSONArray(window);
		if (windows == null) {
			return;
		}
		JSONArray cookies = windows.getJSONObject(0).getJSONArray("cookies");
		for (int i = 0; i < cookies.length(); i++) {
			JSONObject session = cookies.getJSONObject(i);
			String host = session.getString("host");
			String name = session.getString("name");
			String value = session.getString("value");
			String path = session.getString("path");
			addCookie(host, name, value, path);
		}
	}

	private void addCookie(String host, String name, String value, String path) {
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setDomain(host);
		cookie.setPath(path);
		cookieStore.addCookie(cookie);
	}
}
