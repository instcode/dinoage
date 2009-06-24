/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 11:48:11 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.eclipse.ui.widget;

import java.io.File;

import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.eclipse.ui.UniversalUtil;
import org.ddth.dinoage.model.Workspace;
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

public class ChooseWorkspaceDlg {
	private WorkspaceManager workspaces;
	private Combo workspacesCombo;
	private Shell parent;
	private Shell shell;
	private Button okButton;
	private Label errorLabel;
	private int answer;
	
	public ChooseWorkspaceDlg(Shell parent, WorkspaceManager workspaces) {
		this.parent = parent;
		this.workspaces = workspaces;
	}
	
	public int open() {
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
		shell.setText(ResourceManager.getMessage(ResourceManager.KEY_CHOOSE_WORKSPACE_DIALOG_TITLE));
		shell.setLayout(new FillLayout());
		
		createContent();
		
		shell.setSize(500, 130);

		// Center the shell
		UniversalUtil.centerWindow(shell);

		answer = SWT.CANCEL;
		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return answer;
	}

	private void createContent() {
		final Composite composite = new Composite(shell, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		composite.setLayout(gridLayout);

		final Label messageLabel = new Label(composite, SWT.NONE);
		final GridData gd_messageLabel = new GridData(SWT.LEFT, SWT.CENTER, false, true, 4, 1);
		messageLabel.setLayoutData(gd_messageLabel);
		messageLabel.setText(ResourceManager.getMessage(
				ResourceManager.KEY_LABEL_CHOOSE_WORKSPACE_MESSAGE,
				new String[] {
					ResourceManager.KEY_PRODUCT_NAME,
					ResourceManager.getMessage(ResourceManager.KEY_DIRECTORY_DIALOG_MESSAGE)
				}));

		final Label horizontalLine = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_horizontalLine = new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1);
		horizontalLine.setLayoutData(gd_horizontalLine);

		final Label workspaceLabel = new Label(composite, SWT.NONE);
		workspaceLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_WORKSPACE));

		workspacesCombo = new Combo(composite, SWT.NONE);
		final GridData gd_workspacesCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
		workspacesCombo.setLayoutData(gd_workspacesCombo);
		workspacesCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				int check = Workspace.checkWorkspace(workspacesCombo.getText());
				okButton.setEnabled(check == Workspace.WORKSPACE_IS_AVAILABLE);
				String message = "";
				switch (check) {
				case Workspace.WORKSPACE_IS_INVALID:
					message = ResourceManager.getMessage(
							ResourceManager.KEY_MESSAGE_WORKSPACE_MUST_BE_EXISTED);
					break;
				case Workspace.WORKSPACE_IS_BEING_USED:
					message = ResourceManager.getMessage(
							ResourceManager.KEY_MESSAGE_WORKSPACE_IS_BEING_USED);
					break;
				}
				errorLabel.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
				errorLabel.setText(message);
			}
		});

		final Button browseButton = new Button(composite, SWT.NONE);
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				dialog.setFilterPath(System.getProperty("user.dir"));
				dialog.setText(ResourceManager.getMessage(ResourceManager.KEY_DIRECTORY_DIALOG_TITLE));
				dialog.setMessage(ResourceManager.getMessage(ResourceManager.KEY_DIRECTORY_DIALOG_MESSAGE));
				String dir = dialog.open();
				if (dir != null) {
					workspacesCombo.setText(dir);
				}
			}
		});
		final GridData gd_browseButton = new GridData();
		browseButton.setLayoutData(gd_browseButton);
		browseButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_BROWSE_ELLIPSIS));
		
		Button removeWorkspaceButton = new Button(composite, SWT.NONE);
		final GridData gd_removeProfileButton = new GridData(60, SWT.DEFAULT);
		removeWorkspaceButton.setLayoutData(gd_removeProfileButton);
		removeWorkspaceButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_REMOVE));
		removeWorkspaceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				String workspaceName = workspacesCombo.getText();
				if (workspaceName.length() > 0) {
					String message = ResourceManager.getMessage(
							ResourceManager.KEY_CONFIRM_REMOVE_WORKSPACE, new Object[] {workspaceName});
					int answer = UniversalUtil.showConfirmDlg(shell, shell.getText(), message);
					if (answer == SWT.YES) {
						workspacesCombo.remove(workspaceName);
						workspacesCombo.setText("");
					}
				}
			}
		});

		errorLabel = new Label(composite, SWT.NONE);
		final GridData gd_errorText = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_errorText.widthHint = 355;
		errorLabel.setLayoutData(gd_errorText);
		
		okButton = new Button(composite, SWT.PUSH);
		final GridData gd_okButton = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gd_okButton.widthHint = 60;
		okButton.setLayoutData(gd_okButton);
		okButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_OK));
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				answer = SWT.OK;
				workspaces.setWorkspace(workspacesCombo.getText());
				workspaces.setWorkspaces(workspacesCombo.getItems());
				shell.close();
			}
		});
		
		final Button cancelButton = new Button(composite, SWT.NONE);
		final GridData gd_cancelButton = new GridData(SWT.FILL, SWT.CENTER, false, false);
		cancelButton.setLayoutData(gd_cancelButton);
		cancelButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_CANCEL));
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				answer = SWT.CANCEL;
				shell.close();
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
		if (workingSpace == null) {
			workingSpace = new File("").getAbsolutePath();
		}
		workspacesCombo.setText(workingSpace);
	}
}
