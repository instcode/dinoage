/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceManager {
	public static final String KEY_LABEL_BROWSE_ELLIPSIS = "KEY_LABEL_BROWSE_ELLIPSIS";
	public static final String KEY_LABEL_EDIT_ELLIPSIS = "KEY_LABEL_EDIT_ELLIPSIS";
	public static final String KEY_LOGIN_FAILED_MESSAGE = "KEY_LOGIN_FAILED_MESSAGE";
	public static final String KEY_CONFIRM_REMOVE_WORKSPACE_PROFILE = "KEY_CONFIRM_REMOVE_WORKSPACE_PROFILE";
	public static final String KEY_LABEL_CANCEL = "KEY_LABEL_CANCEL";
	public static final String KEY_LOGIN_DIALOG_TITLE = "KEY_LOGIN_DIALOG_TITLE";
	public static final String KEY_LABEL_PROFILE_NAME = "KEY_LABEL_PROFILE_NAME";
	public static final String KEY_MESSAGE_REQUESTING_HREF = "KEY_MESSAGE_REQUESTING_HREF";
	public static final String KEY_DIRECTORY_DIALOG_MESSAGE = "KEY_DIRECTORY_DIALOG_MESSAGE";
	public static final String KEY_LABEL_BLOG_ENTRY = "KEY_LABEL_BLOG_ENTRY";
	public static final String KEY_MESSAGE_FULL_HREF = "KEY_MESSAGE_FULL_HREF";
	public static final String KEY_ENCODING = "KEY_ENCODING";
	public static final String KEY_LABEL_SAVE = "KEY_LABEL_SAVE";
	public static final String KEY_LABEL_PROFILE_URL = "KEY_LABEL_PROFILE_URL";
	public static final String KEY_WORKSPACE_RESUME_FILE_HEADER = "KEY_WORKSPACE_RESUME_FILE_HEADER";
	public static final String KEY_MESSAGE_DONE_HREF = "KEY_MESSAGE_DONE_HREF";
	public static final String KEY_PRODUCT_VERSION = "KEY_PRODUCT_VERSION";
	public static final String KEY_CONFIRM_REMOVE_WORKSPACE = "KEY_CONFIRM_REMOVE_WORKSPACE";
	public static final String KEY_LABEL_WORKSPACE = "KEY_LABEL_WORKSPACE";
	public static final String KEY_LABEL_SHOW_WINDOW = "KEY_LABEL_SHOW_WINDOW";
	public static final String KEY_SYSTEM_CONFIG_FILE_HEADER = "KEY_SYSTEM_CONFIG_FILE_HEADER";
	public static final String KEY_LABEL_SWITCH_WORKSPACE_ELLIPSIS = "KEY_LABEL_SWITCH_WORKSPACE_ELLIPSIS";
	public static final String KEY_DUPLICATE_PROFILE_NAME_DETECTED_MESSAGE = "KEY_DUPLICATE_PROFILE_NAME_DETECTED_MESSAGE";
	public static final String KEY_LABEL_OK = "KEY_LABEL_OK";
	public static final String KEY_LABEL_STOP_BACKUP = "KEY_LABEL_STOP_BACKUP";
	public static final String KEY_LABEL_GUESTBOOK = "KEY_LABEL_GUESTBOOK";
	public static final String KEY_LABEL_REMOVE = "KEY_LABEL_REMOVE";
	public static final String KEY_LABEL_SHOW_BACKUP_ELLIPSIS = "KEY_LABEL_SHOW_BACKUP_ELLIPSIS";
	public static final String KEY_MESSAGE_WORKSPACE_IS_BEING_USED = "KEY_MESSAGE_WORKSPACE_IS_BEING_USED";
	public static final String KEY_MESSAGE_EXIT_WHEN_RUNNING = "KEY_MESSAGE_EXIT_WHEN_RUNNING";
	public static final String KEY_WAIT_FOR_STOPPING = "KEY_WAIT_FOR_STOPPING";
	public static final String KEY_DIRECTORY_DIALOG_TITLE = "KEY_DIRECTORY_DIALOG_TITLE";
	public static final String KEY_LABEL_BACKUP_BUTTON_TITLE = "KEY_LABEL_BACKUP_BUTTON_TITLE";
	public static final String KEY_RESUME_RETRIEVING_CONFIRM = "KEY_RESUME_RETRIEVING_CONFIRM";
	public static final String KEY_MESSAGE_READY_HREF = "KEY_MESSAGE_READY_HREF";
	public static final String KEY_MINIMIZE_TO_TRAY = "KEY_MINIMIZE_TO_TRAY";
	public static final String KEY_PRODUCT_AUTHOR = "KEY_PRODUCT_AUTHOR";
	public static final String KEY_MESSAGE_WORKSPACE_MUST_BE_EXISTED = "KEY_MESSAGE_WORKSPACE_MUST_BE_EXISTED";
	public static final String KEY_READ_WARNING_CONFIRM_MESSAGE = "KEY_READ_WARNING_CONFIRM_MESSAGE";
	public static final String KEY_PROFILE_RESUME_FILE_HEADER = "KEY_PROFILE_RESUME_FILE_HEADER";
	public static final String KEY_PRODUCT_NAME = "KEY_PRODUCT_NAME";
	public static final String KEY_RELAX_URL = "KEY_RELAX_URL";
	public static final String KEY_BACKUP_SETTING_INFORMATION_MESSAGE = "KEY_BACKUP_SETTING_INFORMATION_MESSAGE";
	public static final String KEY_LABEL_CHOOSE_WORKSPACE_MESSAGE = "KEY_LABEL_CHOOSE_WORKSPACE_MESSAGE";
	public static final String KEY_PROFILE_DIALOG_TITLE = "KEY_PROFILE_DIALOG_TITLE";
	public static final String KEY_LABEL_EXIT = "KEY_LABEL_EXIT";
	public static final String KEY_CHOOSE_WORKSPACE_DIALOG_TITLE = "KEY_CHOOSE_WORKSPACE_DIALOG_TITLE";
	public static final String KEY_LABEL_NEW_ELLIPSIS = "KEY_LABEL_NEW_ELLIPSIS";
	public static final String KEY_MESSAGE_EXISTED_PROFILE = "KEY_MESSAGE_EXISTED_PROFILE";
	public static final String KEY_CONFIRM_STOP_ACTIVE_PROFILE = "KEY_CONFIRM_STOP_ACTIVE_PROFILE";
	public static final String KEY_MESSAGE_SELECT_ALL = "KEY_MESSAGE_SELECT_ALL";
	public static final String KEY_MESSAGE_SELECT_NONE = "KEY_MESSAGE_SELECT_NONE";
	public static final String KEY_MESSAGE_INVERT_SELECTION = "KEY_MESSAGE_INVERT_SELECTION";
	
	private static ResourceBundle resources;

	static {
		try {
			ResourceManager.resources = ResourceBundle.getBundle("dinoage_en");
//			Properties prop = new Properties();
//			try {
//				prop.load(new FileInputStream("D:\\Projects\\DinoAge\\res\\dinoage_en.properties"));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			Enumeration<?> enumeration = prop.keys();
//			while (enumeration.hasMoreElements()) {
//				String nextElement = (String) enumeration.nextElement();
//				//System.out.println("public static final String " + nextElement + " = \"" + nextElement.toLowerCase().replace("_", ".") + "\";");
//				//System.out.println("public static final String " + nextElement + " = \"" + nextElement + "\";");
//				System.out.println(nextElement + " ==> " + prop.getProperty(nextElement));
//			}
		}
		catch (MissingResourceException e) {
			// Empty
		}
	}

	/**
	 * Loads a string from a resource file using a key. If the key does not
	 * exist, it is used as the result.
	 * 
	 * @param key
	 *            the name of the string resource
	 * @return the string resource
	 */
	public static String getMessage(String key) {
		if (resources == null)
			return key;
		try {
			return resources.getString(key);
		}
		catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Loads a string from a resource file using a key and formats it using
	 * MessageFormat. If the key does not exist, it is used as the argument to
	 * be format().
	 * 
	 * @param key
	 *            the name of the string resource
	 * @param args
	 *            the array of strings to substitute
	 * @return the string resource
	 */
	public static String getMessage(String key, Object[] args) {
		return MessageFormat.format(getMessage(key), args);
	}
}
