package org.ddth.dinoage.eclipse.ui.perferences;

import org.ddth.dinoage.DinoAgeSettings;
import org.ddth.dinoage.eclipse.Activator;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class PreferenceDataPage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public PreferenceDataPage() {
		super(GRID);
		IPreferenceStore preferences = new ScopedPreferenceStore(new ConfigurationScope(), Activator.PLUGIN_ID);
	    setPreferenceStore(preferences);
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new ComboFieldEditor(DinoAgeSettings.PROPERTY_DB_TYPE, "Database Type:",
			new String[][] {{"Derby", "derby"}, {"MySQL", "mysql"}, {"HSQLDB", "hsqldb"}}, getFieldEditorParent()));
		addField(new StringFieldEditor(DinoAgeSettings.PROPERTY_DB_CONNECTION_URL, "Connection URL:", getFieldEditorParent()));
		addField(new StringFieldEditor(DinoAgeSettings.PROPERTY_DB_JDBC_DRIVER, "JDBC Driver:", getFieldEditorParent()));
		addField(new StringFieldEditor(DinoAgeSettings.PROPERTY_DB_USERNAME, "Username:", getFieldEditorParent()));
		StringFieldEditor editor = new StringFieldEditor(DinoAgeSettings.PROPERTY_DB_PASSWORD, "Password:", getFieldEditorParent());
		editor.getTextControl(getFieldEditorParent()).setEchoChar('*');
		addField(editor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}