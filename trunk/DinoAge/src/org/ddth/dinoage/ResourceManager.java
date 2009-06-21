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
	public static final String KEY_PRODUCT_NAME = "DinoAge";
	public static final String KEY_PRODUCT_VERSION = "1.0.0";
	public static final String KEY_PRODUCT_AUTHOR = "instcode";
	public static final String KEY_PRODUCT_DIALOG_TITLE = KEY_PRODUCT_NAME + " " + KEY_PRODUCT_VERSION;
	public static final String KEY_ENCODING = "UTF-8";
	public static final String KEY_RELAX_URL = "http://360.yahoo.com/profile-n75YJ78_fL5JcEVFlIE1";
	
	public static final String KEY_MINIMIZE_TO_TRAY =
		"DinoAge will continue running in the system tray.";
	public static final String KEY_RESUME_RETRIEVING_CONFIRM =
		"A resumable backup for ''{0}'' is found in ''{1}'' folder.\n" +
		"\n" +
		"Would you like to resume it? (Choose *No* to start from beginning)";
	public static final String KEY_MESSAGE_EXISTED_PROFILE =
		"The profile ''{0}'' you entered can not be saved!\n" +
		"It has been existed in the current workspace.\n" +
		"You can only modify it by selecting from the drop down list.";
	public static final String KEY_MESSAGE_EXIT_WHEN_RUNNING =
		"Plz stop the current backup for {0} first.";
	public static final String KEY_MESSAGE_WORKSPACE_MUST_BE_EXISTED =
		"Workspace folder must be existed.";
	public static final String KEY_MESSAGE_WORKSPACE_IS_BEING_USED =
		"Workspace is being used.";
	
	public static final String KEY_MESSAGE_FULL_HREF = "<a href=\"{0}\">{1}</a>";
	public static final String KEY_MESSAGE_READY_HREF = "Ready!";
	public static final String KEY_MESSAGE_REQUESTING_HREF = "<a href=\"{0}\">{1}... Requesting...</a>";
	public static final String KEY_MESSAGE_DONE_HREF = "<a href=\"{0}\">{1}... Done!</a>";
	public static final String KEY_LABEL_PROFILE_URL = "Profile URL";
	public static final String KEY_WAIT_FOR_STOPPING = "Waiting for the current request to finish...";
	public static final String KEY_LABEL_GUESTBOOK = "Backup all Yahoo 360 guestbooks";
	public static final String KEY_LABEL_BLOG_ENTRY = "Backup all Yahoo 360 entries";
	public static final String KEY_PROFILE_DIALOG_TITLE = "Profile";
	public static final String KEY_READ_WARNING_CONFIRM_MESSAGE = "I warned you already, ok!!??";
	public static final String KEY_DUPLICATE_PROFILE_NAME_DETECTED_MESSAGE =
		"The profile you entered is already existed in {0} workspace. If you\n" +
		"continue, plz makesure you have a backup of that profile folder already,\n" +
		"otherwise, all existing information will be overwritten and not restorable!\n" +
		"\n" +
		"The ''{1}'' profile folder: ''{2}''\n" +
		"\n" +
		"Do you want to continue? :-??";
	public static final String KEY_BACKUP_SETTING_INFORMATION_MESSAGE =
		"Enter, paste or drag && drop the profile URL from any source then select\n" +
		"which options you want the {0} to backup.\n" +
		"\n" +
		"The profile name is used to locate the backup content and must be unique\n" +
		"in the same workspace.\n" +
		"\n" +
		"Save your settings by using the ''Save'' button";
	
	public static final String KEY_SYSTEM_CONFIG_FILE_HEADER =
		"#########################################\n" +
		"# This file stores the system configuration.\n" +
		"# Do not manually modify or rename\n" +
		"##########################################";
	public static final String KEY_WORKSPACE_RESUME_FILE_HEADER =
		"#########################################\n" +
		"# This file stores profiles' resume information.\n" +
		"# Do not modify manually, rename or delete\n" +
		"##########################################";
	public static final String KEY_PROFILE_RESUME_FILE_HEADER =
		"###################### WARNING ##########################\n" +
		"# This file contains resumable information for *{0}*\n" +
		"# Profile URL: {1}\n" +
		"#\n" +
		"# Do not edit, rename or delete manually, otherwise\n" +
		"# you will lose all resume information.\n" +
		"#################### END WARNING ########################\n";
	public static final String KEY_LOGIN_DIALOG_TITLE = "Login Yahoo";
	public static final String KEY_LOGIN_FAILED_MESSAGE =
		"Hmm...\n" +
		"You have entered an invalid YID or password or simply ignored them!\n" +
		"Don''t worry, {0} still continue working with anonymous account ;)).\n" +
		"However, some private blog contents may not be retrieved... :D\n" +
		"Anyway, it's your problem, not my program''s, ok??! ;))\n" +
		"So, continue or restart the application, that is a BIG question!\n\n" +
		"Hahaha...\n";

	public static final String KEY_CHOOSE_WORKSPACE_DIALOG_TITLE = "Workspace Chooser";
	public static final String KEY_LABEL_CHOOSE_WORKSPACE_MESSAGE =
		"{0} stores your working profiles in a folder called a workspace.\n{1}";
	public static final String KEY_LABEL_SAVE = "Save";
	public static final String KEY_LABEL_WORKSPACE = "Workspace";
	public static final String KEY_LABEL_OK = "OK";
	public static final String KEY_LABEL_CANCEL = "Cancel";
	public static final String KEY_LABEL_BROWSE_ELLIPSIS = "Browse...";
	public static final String KEY_DIRECTORY_DIALOG_MESSAGE = "Choose a workspace folder to use for this session.";
	public static final String KEY_DIRECTORY_DIALOG_TITLE = "Select Workspace Folder";
	public static final String KEY_CONFIRM_REMOVE_WORKSPACE =
		"You're gonna remove the ''{0}'' location in workspace list.\n" +
		"Don''t worry, this makes no change to your existing data.\n" +
		"\n" +
		"Do you want to continue?";
	public static final String KEY_CONFIRM_REMOVE_WORKSPACE_PROFILE =
		"You're gonna remove the ''{0}'' profile in profile list.\n" +
		"This deletes *only* resume information (but cannot be undone).\n" +
		"Your data will be kept untouched.\n" +
		"\n" +
		"Do you want to continue?";
	public static final String KEY_LABEL_SWITCH_WORKSPACE_ELLIPSIS = "Switch Workspace...";
	public static final String KEY_LABEL_EDIT_ELLIPSIS = "Edit...";
	public static final String KEY_LABEL_NEW_ELLIPSIS = "New...";
	public static final String KEY_LABEL_REMOVE = "Remove";
	public static final String KEY_LABEL_BACKUP_BUTTON_TITLE = "Backup";
	public static final String KEY_LABEL_SHOW_BACKUP_ELLIPSIS = "Show...";
	public static final String KEY_LABEL_STOP_BACKUP = "Stop";
	public static final String KEY_LABEL_PROFILE_NAME = "Profile Name";
	public static final String KEY_LABEL_SHOW_WINDOW = "Show";
	public static final String KEY_LABEL_EXIT = "Exit";
	
	private static ResourceBundle resources;

	static {
		try {
			ResourceManager.resources = ResourceBundle.getBundle("dinoage_en");
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
