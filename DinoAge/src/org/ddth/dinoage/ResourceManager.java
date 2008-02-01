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
	public static final String RESUME_FILE_NAME = ".resume";
	
	public static final String KEY_ENCODING = "UTF-8";
	public static final String KEY_GUESTBOOK_URL = "http://360.yahoo.com/guestbook-";
	public static final String KEY_PROFILE_URL = "http://360.yahoo.com/profile-";
	public static final String KEY_BLOG_URL = "http://blog.360.yahoo.com/blog-";
	public static final String KEY_BLOG_LIST_PARAMETER_VALUE = "&list=1";
	public static final String KEY_BLOG_ENTRY_REGEXP = KEY_BLOG_URL + ".*p=[\\d+].*";
	public static final String KEY_RELAX_URL = "http://360.yahoo.com/profile-n75YJ78_fL5JcEVFlIE1";
	
	public static final String KEY_RESUME_RETRIEVING_CONFIRM =
		"A resumable backup for \"{0}\" is found in [{1}] folder.\n" +
		"\n" +
		"Would you like to resume it? (Choose *No* to start from beginning)";
	public static final String KEY_CONFLICT_RESUMABLE_FILE =
		"A resumable backup for \"{0}\" is found in [{1}] folder.\n" +
		"However, its content seems to be a backup of another profile \"{2}\".\n" +
		"\n" +
		"{3} now starts a backup for \"{0}\" from beginning.\n" +
		"Remember to choose another profile name plz!";
	public static final String KEY_CONFIRM_EXIT_WHEN_RUNNING = "The current backup for {0} is running.\nDo you want to exit now?";
	public static final String KEY_WAIT_FOR_EXITING = "<a href=\"{0}\">Plz wait!... {1} is now exiting...</a>";
	public static final String KEY_WAIT_FOR_STOPPING = "<a href=\"{0}\">Waiting for the current request to finish...</a>";
	public static final String KEY_MESSAGE_READY_HREF = "<a href=\"{0}\">I''m ready!... Hehehe...</a>";
	public static final String KEY_MESSAGE_DONE_HREF = "<a href=\"{0}\">{0}... Done!</a>";
	public static final String KEY_MESSAGE_REQUESTING_HREF = "<a href=\"{0}\">{0}... Requesting...</a>";
	public static final String KEY_LABEL_Y360_PROFILE = "Y360 Profile";
	public static final String KEY_LABEL_BACKUP_BUTTON_TITLE = "Backup...";
	public static final String KEY_LABEL_STOP_BACKUP = "Stop";
	public static final String KEY_LABEL_PROFILE_NAME = "Profile Name";
	public static final String KEY_LABEL_START = "Start";
	public static final String KEY_LABEL_GUESTBOOK = "Guestbook";
	public static final String KEY_LABEL_BLOG_ENTRY = "Blog Entry";
	public static final String KEY_BACKUP_SETTINGS_DIALOG_TITLE = "Backup Settings";
	public static final String KEY_READ_WARNING_CONFIRM_MESSAGE = "I warned you already, ok!!??";
	public static final String KEY_DUPLICATE_PROFILE_NAME_DETECTED_MESSAGE =
		"The profile you entered is already existed in {0} home folder. If you\n" +
		"continue, plz makesure you have a backup of that profile folder already,\n" +
		"otherwise, all existing information will be overwritten and not restorable!\n" +
		"\n" +
		"The [{1}] profile folder: [{2}]\n" +
		"\n" +
		"Do you want to continue? :-??";
	public static final String KEY_PLZ_CHOOSE_BACKUP_OPTION_MESSAGE = "Please choose at least one backup option!!";
	public static final String KEY_BACKUP_SETTING_INFORMATION_MESSAGE =
		"The profile name is used to locate your backup-content directory.\n" +
		"Plz select which options you want to backup, then click \"Start\".";
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
	public static final String KEY_LABEL_WORKSPACE = "Workspace:";
	public static final String KEY_LABEL_OK_BUTTON = "OK";
	public static final String KEY_LABEL_BROWSE_BUTTON = "Browse...";
	public static final String KEY_DIRECTORY_DIALOG_MESSAGE = "Choose a workspace folder to use for this session.";
	public static final String KEY_DIRECTORY_DIALOG_TITLE = "Select Workspace Folder";
	public static final String KEY_LABEL_CANCEL_BUTTON = "Cancel";
	
	public static ResourceBundle resources;

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
