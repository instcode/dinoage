/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 11:48:11 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.ui;

import java.io.File;

import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.model.WorkspaceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class DinoAgeChooseWorkspaceDlg {
	private WorkspaceManager workspaces;
	private Combo workspacesCombo;
	private Shell shell;
	private Button okButton;

	public DinoAgeChooseWorkspaceDlg(WorkspaceManager workspaces) {
		this.workspaces = workspaces;
	}

	protected String getWorkspaceLocation() {
		return workspacesCombo.getText();
	}

	public void open() {
		shell = new Shell();
		shell.setLayout(new FillLayout());
		createContent();

		shell.setText(ResourceManager.getMessage(ResourceManager.KEY_CHOOSE_WORKSPACE_DIALOG_TITLE));
		shell.pack();
		shell.setSize(440, 132);

		// Center the shell
		UniversalUtil.centerWindow(shell);

		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void createContent() {
		final Composite composite = new Composite(shell, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		composite.setLayout(gridLayout);

		final Label messageLabel = new Label(composite, SWT.NONE);
		final GridData gd_messageLabel = new GridData(SWT.LEFT, SWT.CENTER, false, true, 3, 1);
		gd_messageLabel.heightHint = 33;
		gd_messageLabel.widthHint = 423;
		messageLabel.setLayoutData(gd_messageLabel);
		messageLabel.setText(ResourceManager.getMessage(
				ResourceManager.KEY_LABEL_CHOOSE_WORKSPACE_MESSAGE,
				new String[] {
						ResourceManager.KEY_PRODUCT_NAME,
						ResourceManager.getMessage(ResourceManager.KEY_DIRECTORY_DIALOG_MESSAGE)
				}));

		final Label horizontalLine = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_horizontalLine = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd_horizontalLine.widthHint = 424;
		horizontalLine.setLayoutData(gd_horizontalLine);

		final Label workspaceLabel = new Label(composite, SWT.NONE);
		workspaceLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_WORKSPACE));

		workspacesCombo = new Combo(composite, SWT.NONE);
		final GridData gd_workspacesCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_workspacesCombo.widthHint = 303;
		workspacesCombo.setLayoutData(gd_workspacesCombo);
		workspacesCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				boolean isEnabled = (workspaces.setWorkspace(getWorkspaceLocation()) != null);
				okButton.setEnabled(isEnabled);
			}
		});

		final Button browseButton = new Button(composite, SWT.NONE);
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				dialog.setText(ResourceManager.getMessage(ResourceManager.KEY_DIRECTORY_DIALOG_TITLE));
				dialog.setMessage(ResourceManager.getMessage(ResourceManager.KEY_DIRECTORY_DIALOG_MESSAGE));
				dialog.setFilterPath(new File(".").getAbsolutePath());
				String dir = dialog.open();
				if (dir != null) {
					workspacesCombo.setText(dir);
				}
			}
		});
		final GridData gd_browseButton = new GridData(60, SWT.DEFAULT);
		browseButton.setLayoutData(gd_browseButton);
		browseButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_BROWSE_BUTTON));
		new Label(composite, SWT.NONE);

		okButton = new Button(composite, SWT.PUSH);
		final GridData gd_okButton = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gd_okButton.widthHint = 59;
		okButton.setLayoutData(gd_okButton);
		okButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_OK_BUTTON));
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		final Button cancelButton = new Button(composite, SWT.NONE);
		final GridData gd_cancelButton = new GridData(60, SWT.DEFAULT);
		cancelButton.setLayoutData(gd_cancelButton);
		cancelButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_CANCEL_BUTTON));
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				System.exit(0);
			}
		});
		
		shell.setDefaultButton(okButton);

		// Set initial values for all components
		initializeValues();
	}

	private void initializeValues() {
		String[] recentWorkspaces = workspaces.getWorkspaces();
		for (int i = 0; i < recentWorkspaces.length; ++i) {
			if (recentWorkspaces[i] != null) {
				workspacesCombo.add(recentWorkspaces[i]);
			}
		}
		String workingSpace = workspaces.getSelection();
		if (workingSpace != null) {
			workspacesCombo.setText(workingSpace);
		}
	}
}
