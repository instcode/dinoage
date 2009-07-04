package org.ddth.dinoage.eclipse.ui.perferences;

import org.ddth.dinoage.eclipse.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferenceConnectionPage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public PreferenceConnectionPage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(null);
	}

	public void createFieldEditors() {
		addField(new DirectoryFieldEditor("xxx", "&Directory preference:", getFieldEditorParent()));
		addField(new BooleanFieldEditor("yyy", "&An example of a boolean preference", getFieldEditorParent()));
		addField(new RadioGroupFieldEditor("zzz",
				"An example of a multiple-choice preference", 1,
				new String[][] { { "&Choice 1", "choice1" },
						{ "C&hoice 2", "choice2" } }, getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}