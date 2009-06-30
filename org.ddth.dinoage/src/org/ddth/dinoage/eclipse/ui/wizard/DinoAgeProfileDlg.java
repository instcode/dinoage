/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 11:48:11 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.eclipse.ui.wizard;

import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.core.Profile;
import org.ddth.dinoage.core.Workspace;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class DinoAgeProfileDlg extends WizardDialog {

	/**
	 * Create the dialog
	 * @param parent
	 */
	public DinoAgeProfileDlg(Shell parent, final Workspace workspace, final Profile profile) {
		super(parent, createWizard(workspace, profile));
	}

	private static Wizard createWizard(final Workspace workspace, final Profile profile) {
		ProfileWizardPage profileWizardPage = new ProfileWizardPage(workspace, profile);
		Wizard wizard = new Wizard() {
			@Override
			public boolean performFinish() {
				if (workspace.saveProfile(profile)) {
					return true;
				}
				return true;
			}
		};
		wizard.addPage(profileWizardPage);
		return wizard;
	}
	
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		String buttonLabel = label;
		// Change FINISH button label to our own name
		if (id == IDialogConstants.FINISH_ID) {
			buttonLabel = ResourceManager.getMessage(ResourceManager.KEY_LABEL_SAVE);
		}
		return super.createButton(parent, id, buttonLabel, defaultButton);
	}
}
