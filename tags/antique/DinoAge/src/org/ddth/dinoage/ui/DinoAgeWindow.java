/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 10:10:04 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.ui;

import org.ddth.dinoage.DinoAge;
import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.grabber.yahoo.YBackupState;
import org.ddth.dinoage.model.Profile;
import org.ddth.dinoage.model.Workspace;
import org.ddth.grabber.core.handler.ConnectionListener;
import org.ddth.grabber.core.handler.SessionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DinoAgeWindow implements ConnectionListener, SessionListener {
	
	private Shell shell;
	private Button removeProfileButton;
	private Button editProfileButton;
	private Button switchWorkspaceButton;
	private Combo profilesCombo;
	private Text workspaceText;
	private Button backupButton;
	private Link profileURLText;
	private Link statusLabel;
	
	private SelectionListener backupListener;
	private SelectionListener stopListener;
	private SelectionListener switchWorkspaceListener;
	private SelectionListener editProfileListener;
	private SelectionListener removeProfileListener;
	private SelectionListener launchSelection;
	
	private DinoAge dinoage;
	private boolean shouldClose = false;
	private static final String CREATE_NEW_PROFILE_TEXT = "<create new>";

	public DinoAgeWindow(DinoAge dinoage) {
		this.dinoage = dinoage;
	}
	
	public void open() {
		shell = new Shell(SWT.CENTER | SWT.CLOSE);
		shell.setText(ResourceManager.KEY_PRODUCT_DIALOG_TITLE);
		shell.setLayout(new FillLayout());

		createListeners();
		createContent();
		
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		
		// Center the shell
		UniversalUtil.centerWindow(shell);

		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent event) {
				int answer = SWT.YES;
				if (!shouldClose && dinoage.isRunning()) {
					String sMessage = ResourceManager.getMessage(
							ResourceManager.KEY_CONFIRM_EXIT_WHEN_RUNNING,
							new String[] {dinoage.getActiveProfile().getProfileName()});
					answer = UniversalUtil.showConfirmDlg(shell, shell.getText(), sMessage);
					if (answer == SWT.YES) {
						shouldClose = true;
						setStatusText(ResourceManager.getMessage(
											ResourceManager.KEY_WAIT_FOR_EXITING,
											new String[] {ResourceManager.KEY_RELAX_URL, ResourceManager.KEY_PRODUCT_NAME}));
						dinoage.stop();
						backupButton.setEnabled(false);
						answer = SWT.NO;
					}
				}
				event.doit = (answer == SWT.YES);
			}
		});

		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	private void createListeners() {
		launchSelection = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Program.launch(event.text);
			}
		};
		
		stopListener = new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				if (dinoage.isRunning()) {
					dinoage.stop();
					setStatusText(ResourceManager.getMessage(
							ResourceManager.KEY_WAIT_FOR_STOPPING, new String[] {ResourceManager.KEY_RELAX_URL}));
					backupButton.setEnabled(false);
				}
			}
		};
		
		switchWorkspaceListener = new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				if (dinoage.chooseWorkspace(shell)) {
					initializeValues();
				}
			}
		};
		
		editProfileListener = new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				Workspace workspace = dinoage.getWorkspace();
				DinoAgeProfileDlg dlg = new DinoAgeProfileDlg(shell, workspace);
				
				Profile profile = workspace.getProfile(profilesCombo.getText());
				if (profile != null) {
					dlg.getProfile().populate(profile);
				}
				if (dlg.open() == SWT.OK) {
					String profileName = dlg.getProfile().getProfileName();
					if (profile != null) {
						profile.populate(dlg.getProfile());
					}
					else {
						profilesCombo.add(profileName);						
					}
					profilesCombo.setText(profileName);
					workspace.putProfile(dlg.getProfile());
				}
			}
		};
		
		removeProfileListener = new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				String profileName = profilesCombo.getText();
				if (CREATE_NEW_PROFILE_TEXT.equals(profileName)) {
					return;
				}
				String message = ResourceManager.getMessage(
						ResourceManager.KEY_CONFIRM_REMOVE_WORKSPACE_PROFILE, new Object[] {profileName});
				int answer = UniversalUtil.showConfirmDlg(shell, shell.getText(), message);
				if (answer == SWT.YES) {
					profilesCombo.remove(profileName);
					profilesCombo.setText(CREATE_NEW_PROFILE_TEXT);
					dinoage.getWorkspace().removeProfile(profileName);
				}
			}
		};
		
		backupListener = new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				String profileName = profilesCombo.getText();
				if (CREATE_NEW_PROFILE_TEXT.equals(profileName)) {
					return;
				}
				Profile profile = dinoage.getWorkspace().getProfile(profileName);
				YBackupState state = dinoage.createState(profile.getProfileName());
				
				int answer = SWT.YES;
				if (state != null && !state.isNewlyCreated()) {
					String message = ResourceManager.getMessage(
							ResourceManager.KEY_RESUME_RETRIEVING_CONFIRM,
							new String[] {state.getProfileId(), profile.getProfileName()}
					);
					answer = UniversalUtil.showConfirmDlg(shell, shell.getText(), message);
				}
				if (answer == SWT.NO) {
					state.reset();
				}
				dinoage.backup(state);
				
				// Update backupButton action
				if (dinoage.isRunning()) {
					enableButtons(true);
					backupButton.removeSelectionListener(backupListener);
					backupButton.addSelectionListener(stopListener);
					backupButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_STOP_BACKUP));
				}
			}
		};
	}
	
	private void createContent() {
		final Composite composite = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);

		final Label workspaceLabel = new Label(composite, SWT.NONE);
		final GridData gd_workspaceLabel = new GridData(SWT.LEFT, SWT.CENTER, false, true);
		gd_workspaceLabel.heightHint = 15;
		gd_workspaceLabel.widthHint = 60;
		workspaceLabel.setLayoutData(gd_workspaceLabel);
		workspaceLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_WORKSPACE));

		workspaceText = new Text(composite, SWT.BORDER);
		workspaceText.setEditable(false);
		final GridData gd_workspaceText = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gd_workspaceText.heightHint = 15;
		workspaceText.setLayoutData(gd_workspaceText);

		switchWorkspaceButton = new Button(composite, SWT.NONE);
		final GridData gd_switchWorkspaceButton = new GridData(SWT.FILL, SWT.CENTER, false, true, 2, 1);
		gd_switchWorkspaceButton.heightHint = 23;
		switchWorkspaceButton.setLayoutData(gd_switchWorkspaceButton);
		switchWorkspaceButton.addSelectionListener(switchWorkspaceListener);
		switchWorkspaceButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_SWITCH_WORKSPACE_ELLIPSIS));

		final Label profileLabel = new Label(composite, SWT.NONE);
		final GridData gd_profileLabel = new GridData(SWT.LEFT, SWT.CENTER, false, true);
		gd_profileLabel.heightHint = 15;
		gd_profileLabel.widthHint = 71;
		profileLabel.setLayoutData(gd_profileLabel);
		profileLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_PROFILE_NAME));

		profilesCombo = new Combo(composite, SWT.READ_ONLY);
		final GridData gd_profilesCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_profilesCombo.heightHint = 15;
		profilesCombo.setLayoutData(gd_profilesCombo);

		editProfileButton = new Button(composite, SWT.NONE);
		final GridData gd_editProfileButton = new GridData(60, SWT.DEFAULT);
		editProfileButton.setLayoutData(gd_editProfileButton);
		editProfileButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_EDIT_ELLIPSIS));
		editProfileButton.addSelectionListener(editProfileListener);

		removeProfileButton = new Button(composite, SWT.NONE);
		final GridData gd_removeProfileButton = new GridData(60, SWT.DEFAULT);
		removeProfileButton.setLayoutData(gd_removeProfileButton);
		removeProfileButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_REMOVE));
		removeProfileButton.addSelectionListener(removeProfileListener);
		
		final Label profileURLLabel = new Label(composite, SWT.NONE);
		profileURLLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_Y360_PROFILE));
		GridData gd_profileURLLabel = new GridData(SWT.LEFT, SWT.CENTER, false, true);
		gd_profileURLLabel.heightHint = 15;
		gd_profileURLLabel.widthHint = 85;
		profileURLLabel.setLayoutData(gd_profileURLLabel);

		profileURLText = new Link(composite, SWT.NONE | SWT.NO_FOCUS);
		profileURLText.setText(
				ResourceManager.getMessage(ResourceManager.KEY_MESSAGE_FULL_URL_HREF,
						new String [] {ResourceManager.getMessage(ResourceManager.KEY_RELAX_URL)}));
		profileURLText.addSelectionListener(launchSelection);
		
		GridData gd_profileURLText = new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1);
		gd_profileURLText.heightHint = 15;
		profileURLText.setLayoutData(gd_profileURLText);

		backupButton = new Button(composite, SWT.NONE);
		backupButton.addSelectionListener(backupListener);
		GridData gd_backupButton = new GridData(60, 23);
		backupButton.setLayoutData(gd_backupButton);
		backupButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_BACKUP_BUTTON_TITLE));

		statusLabel = new Link(composite, SWT.NONE | SWT.NO_FOCUS);
		statusLabel.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		statusLabel.addSelectionListener(launchSelection);
		GridData gd_status = new GridData(SWT.FILL, SWT.CENTER, false, true, 4, 1);
		statusLabel.setLayoutData(gd_status);
		
		shell.setTabList(new Control[] {composite});
		composite.setTabList(new Control[] {workspaceText, switchWorkspaceButton, profilesCombo, editProfileButton, removeProfileButton, backupButton, workspaceText});
		
		initializeValues();
	}

	private void initializeValues() {
		Workspace workspace = dinoage.getWorkspace();
		if (workspace == null) {
			return;
		}
		workspaceText.setText(workspace.getWorkspaceLocation());

		Profile[] profiles = workspace.getProfiles();
		
		profilesCombo.removeAll();
		profilesCombo.add(CREATE_NEW_PROFILE_TEXT);
		for (Profile profile : profiles) {
			profilesCombo.add(profile.getProfileName());
		}
		
		String selection = CREATE_NEW_PROFILE_TEXT;
		Profile activeProfile = dinoage.getActiveProfile();
		if (activeProfile != null) {
			selection = activeProfile.getProfileName();
			profileURLText.setText(
					ResourceManager.getMessage(ResourceManager.KEY_MESSAGE_FULL_URL_HREF, new String [] {activeProfile.getProfileURL()}));
		}
		profilesCombo.setText(selection);
	}
	
	private void enableButtons(boolean isEnabled) {
		
	}
	
	public void notifyFinished(final String sURL, boolean isCompletedWithoutError) {
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (!shouldClose) {
					setStatusText(ResourceManager.getMessage(ResourceManager.KEY_MESSAGE_DONE_HREF, new String[] {sURL}));
					dinoage.saveState();
				}
			}
		});
	}

	public void notifyRequesting(final String sURL) {
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (!shouldClose) {
					setStatusText(ResourceManager.getMessage(ResourceManager.KEY_MESSAGE_REQUESTING_HREF, new String[] {sURL}));
				}
			}
		});
	}

	public void sessionStarted() {
	}

	public void sessionStopped() {
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (!shouldClose) {
					backupButton.removeSelectionListener(stopListener);
					backupButton.addSelectionListener(backupListener);
					backupButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_BACKUP_BUTTON_TITLE));
					setStatusText(ResourceManager.getMessage(
							ResourceManager.KEY_MESSAGE_READY_HREF, new String[] {ResourceManager.KEY_RELAX_URL}));
					backupButton.setEnabled(true);
				}
				else {
					shell.close();
					dinoage.getWorkspace().closeWorkspace();
				}
			}
		});
	}
	
	private void setStatusText(String statusText) {
		statusLabel.setText(statusText);
	}
}
