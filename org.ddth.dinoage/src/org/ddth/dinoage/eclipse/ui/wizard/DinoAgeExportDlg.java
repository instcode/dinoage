/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 11:48:11 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.eclipse.ui.wizard;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.ddth.blogging.wordpress.WXR;
import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.eclipse.ui.model.ExportModel;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class DinoAgeExportDlg extends WizardDialog {

	/**
	 * Create the dialog
	 * @param parent
	 */
	public DinoAgeExportDlg(Shell parent, ExportModel model) {
		super(parent, createWizard(model));
	}

	private static Wizard createWizard(final ExportModel model) {
		final ExportWizardPage profileWizardPage = new ExportWizardPage(model);
		Wizard wizard = new Wizard() {
			@Override
			public boolean performFinish() {
				profileWizardPage.saveAndUpdate(false);
				performExport(model);
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
			buttonLabel = ResourceManager.getMessage(ResourceManager.KEY_LABEL_EXPORT);
		}
		return super.createButton(parent, id, buttonLabel, defaultButton);
	}
	
	private static void performExport(ExportModel model) {
		OutputStreamWriter outputStreamWriter = null;
		try {
			outputStreamWriter = new OutputStreamWriter(new FileOutputStream(model.getOutputFile()), "utf-8");
			WXR.export(model.getBlog(), outputStreamWriter);
		}
		catch (Exception e) {
		}
		finally {
			if (outputStreamWriter != null) {
				try {
					outputStreamWriter.close();
				}
				catch (IOException e) {
				}
			}
		}
	}
}
