package org.ddth.dinoage.eclipse;

import org.ddth.dinoage.DinoAgeSettings;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.service.prefs.Preferences;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		Preferences prefs = new ConfigurationScope().getNode(Activator.PLUGIN_ID);
		
		prefs.put(DinoAgeSettings.PROPERTY_DB_CONNECTION_URL, "jdbc:derby:database;create=true;");
		prefs.put(DinoAgeSettings.PROPERTY_DB_TYPE, "derby");
		prefs.put(DinoAgeSettings.PROPERTY_DB_JDBC_DRIVER, "org.apache.derby.jdbc.EmbeddedDriver");
		prefs.put(DinoAgeSettings.PROPERTY_DB_USERNAME, "");
		prefs.put(DinoAgeSettings.PROPERTY_DB_PASSWORD, "");
		
		prefs.putBoolean(DinoAgeSettings.PROPERTY_WORKSPACE_SHOW_CHOOSER_DIALOG, true);
	}
}
