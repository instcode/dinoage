/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 10:10:04 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.ui;

import java.io.File;
import java.util.Collection;

import org.ddth.dinoage.DinoAge;
import org.ddth.dinoage.DinoAgeSettings;
import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.grabber.BrowsingSession;
import org.ddth.dinoage.model.Profile;
import org.ddth.dinoage.model.Workspace;
import org.ddth.dinoage.model.WorkspaceManager;
import org.ddth.http.core.ConnectionEvent;
import org.ddth.http.core.ConnectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class DinoAgeWindow implements ConnectionListener {
	
	private Shell shell;
	private Button removeProfileButton;
	private Button editProfileButton;
	private Button switchWorkspaceButton;
	private Button showBackup;
	private Button backupButton;
	private Combo profilesCombo;
	private Link profileURLText;
	private Link statusLabel;
	private Text workspaceText;
	private ToolTip tooltip;
	private MenuItem showWorkspaceChooserDialogItem;
	private MenuItem showMainWindowItem;
	
	private SelectionListener showWindowListener;
	private SelectionListener backupListener;
	private SelectionListener stopListener;
	private SelectionListener showBackupListener;
	private SelectionListener switchWorkspaceListener;
	private SelectionListener editProfileListener;
	private SelectionListener removeProfileListener;
	private SelectionListener launchSelection;
	
	private DinoAge dinoage;
	private ModifyListener profileModifyListener;
	private SelectionListener exitListener;
	private static final String CREATE_NEW_PROFILE_TEXT = "<create new>";

	public DinoAgeWindow(DinoAge dinoage) {
		this.dinoage = dinoage;
	}
	
	public void open() {
		shell = new Shell(SWT.CENTER | SWT.CLOSE | SWT.MIN);
		shell.setText(ResourceManager.KEY_PRODUCT_DIALOG_TITLE);
		shell.setLayout(new FillLayout());

		createListeners();
		createContent();
		if (!createTray()) {
			shell.addShellListener(new ShellAdapter() {
				@Override
				public void shellClosed(ShellEvent e) {
					exitListener.widgetSelected(null);
				}
			});
		}
		
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		// Center the shell
		UniversalUtil.centerWindow(shell);

		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	private void createListeners() {
		showWindowListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.setMinimized(false);
				shell.setVisible(true);
			}
		};
		
		exitListener = new SelectionAdapter() {			
			public void widgetSelected(final SelectionEvent e) {
				if (dinoage.isRunning()) {
					Profile profile = dinoage.getWorkspace().getProfile(profilesCombo.getText());
					String message = ResourceManager.getMessage(ResourceManager.KEY_MESSAGE_EXIT_WHEN_RUNNING,
							new String[] { profile.getProfileName() });
					UniversalUtil.showMessageBox(shell, shell.getText(), message);
				}
				else {
					// Close workspace before exiting
					dinoage.getWorkspace().closeWorkspace();
					shell.close();
				}
			}
		};
		
		launchSelection = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Program.launch(event.text);
			}
		};
		
		stopListener = new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				if (dinoage.isRunning()) {
					setStatusText(ResourceManager.getMessage(ResourceManager.KEY_WAIT_FOR_STOPPING));
					dinoage.stop();
					shell.getDisplay().asyncExec(new Runnable() {
						public void run() {
							backupButton.removeSelectionListener(stopListener);
							backupButton.addSelectionListener(backupListener);
							backupButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_BACKUP_BUTTON_TITLE));
							enableButtons(true);
							setStatusText(ResourceManager.getMessage(
									ResourceManager.KEY_MESSAGE_READY_HREF, new String[] {ResourceManager.KEY_RELAX_URL}));
						}
					});
				}
			}
		};
		
		switchWorkspaceListener = new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				WorkspaceManager workspaces = new WorkspaceManager(DinoAgeSettings.getInstance().getRecentWorkspaces());
				if (dinoage.chooseWorkspace(workspaces)) {
					initializeValues();
				}
			}
		};
		
		editProfileListener = new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				Workspace workspace = dinoage.getWorkspace();
				Profile profile = workspace.getProfile(profilesCombo.getText());
				
				DinoAgeProfileDlg dlg = new DinoAgeProfileDlg(shell, workspace);
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
					workspace.saveProfile(dlg.getProfile());
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
		
		profileModifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				String profileName = profilesCombo.getText();
				Profile profile = dinoage.getWorkspace().getProfile(profileName);
				String url = "";
				String folder = "";
				if (profile != null) {
					url = profile.getProfileURL();
					folder = "file:///" + dinoage.getWorkspace().getWorkspaceLocation() + "/" + profileName;
					editProfileButton.setText(ResourceManager.getMessage(
							ResourceManager.KEY_LABEL_EDIT_ELLIPSIS));
				}
				else {
					editProfileButton.setText(ResourceManager.getMessage(
							ResourceManager.KEY_LABEL_NEW_ELLIPSIS));
				}
				profileURLText.setText(
						ResourceManager.getMessage(ResourceManager.KEY_MESSAGE_FULL_HREF, new String [] {folder, url}));
			}
		};
		
		backupListener = new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				String profileName = profilesCombo.getText();
				Workspace workspace = dinoage.getWorkspace();
				Profile profile = workspace.getProfile(profileName);
				BrowsingSession session = dinoage.createSession(profile);
				if (session == null) {
					return;
				}
				int answer = SWT.YES;
				if (!session.isRestorable()) {
					File profileFolder = workspace.getProfileFolder(profile);
					String message = ResourceManager.getMessage(
							ResourceManager.KEY_RESUME_RETRIEVING_CONFIRM,
							new String[] {profile.getProfileName(), profileFolder.getAbsolutePath()}
					);
					answer = UniversalUtil.showConfirmDlg(shell, shell.getText(), message);
				}
				if (answer == SWT.YES) {
					session.restore();
				}
				else if (answer == SWT.NO) {
					session.start();
				}
				
				// Update backupButton action
				if (dinoage.isRunning()) {
					enableButtons(false);
					backupButton.removeSelectionListener(backupListener);
					backupButton.addSelectionListener(stopListener);
					backupButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_STOP_BACKUP));
				}
			}
		};
		
		showBackupListener = new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				String profileName = profilesCombo.getText();
				Profile profile = dinoage.getWorkspace().getProfile(profileName);
				if (profile == null) {
					return;
				}
				ShowBlogEntryDlg dlg = new ShowBlogEntryDlg(shell);
				dlg.open();
			}
		};
	}

	private boolean createTray() {
		Display display = shell.getDisplay();
		Tray tray = display.getSystemTray();
		if (tray == null) {
			return false;
		}
		
		tooltip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
		tooltip.setText(ResourceManager.getMessage(ResourceManager.KEY_PRODUCT_NAME));
		tooltip.setMessage(ResourceManager.getMessage(ResourceManager.KEY_MINIMIZE_TO_TRAY));
	
		Image image = new Image(display, DinoAgeWindow.class.getResourceAsStream("/images/export.gif"));
		
		final TrayItem item = new TrayItem(tray, SWT.NONE);
		item.setImage(image);
		item.setToolTip(tooltip);
		
		shell.addShellListener(new ShellAdapter() {
			/**
			 * Go to tray when minimized/close active window.
			 */
			private void gotoTray() {
				shell.setVisible(false);
				tooltip.setVisible(true);
			}
			
			@Override
			public void shellIconified(ShellEvent even) {
				gotoTray();
			}
			
			@Override
			public void shellClosed(ShellEvent event) {
				// If the window is showing, we only put it to tray
				if (shell.isVisible()) {
					event.doit = false;
					gotoTray();
				}
				else {
					// 'Cause the program is gonna close soon, clean up
					// everything.
					item.setVisible(false);
				}
			}
		});
		
		final Menu menu = new Menu(shell, SWT.POP_UP);
		showMainWindowItem = new MenuItem(menu, SWT.PUSH);
		showMainWindowItem.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_SHOW_WINDOW));
		showMainWindowItem.setEnabled(true);
		showMainWindowItem.addSelectionListener(showWindowListener);
		
		showWorkspaceChooserDialogItem = new MenuItem(menu, SWT.PUSH);		
		showWorkspaceChooserDialogItem.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_SWITCH_WORKSPACE_ELLIPSIS));
		showWorkspaceChooserDialogItem.setEnabled(true);
		showWorkspaceChooserDialogItem.addSelectionListener(switchWorkspaceListener);
		
		MenuItem exitItem = new MenuItem(menu, SWT.PUSH);		
		exitItem.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_EXIT));
		exitItem.addSelectionListener(exitListener);
			
		// Add menu detection listener to tray icon.
		item.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
				menu.setVisible(true);
			}
		});
		item.addSelectionListener(showWindowListener);
		return true;
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
		profilesCombo.addModifyListener(profileModifyListener);
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
		profileURLText.addSelectionListener(launchSelection);
		GridData gd_profileURLText = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gd_profileURLText.widthHint = 400;
		gd_profileURLText.heightHint = 15;
		profileURLText.setLayoutData(gd_profileURLText);

		backupButton = new Button(composite, SWT.NONE);
		backupButton.addSelectionListener(backupListener);
		GridData gd_backupButton = new GridData(60, 23);
		backupButton.setLayoutData(gd_backupButton);
		backupButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_BACKUP_BUTTON_TITLE));

		showBackup = new Button(composite, SWT.NONE);
		showBackup.setLayoutData(new GridData(60, SWT.DEFAULT));
		showBackup.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_SHOW_BACKUP_ELLIPSIS));
		showBackup.addSelectionListener(showBackupListener);

		statusLabel = new Link(composite, SWT.NONE | SWT.NO_FOCUS);
		statusLabel.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		statusLabel.addSelectionListener(launchSelection);
		GridData gd_status = new GridData(SWT.FILL, SWT.CENTER, false, true, 4, 1);
		statusLabel.setLayoutData(gd_status);
		
		shell.setTabList(new Control[] {composite});
		composite.setTabList(new Control[] {workspaceText, switchWorkspaceButton, profilesCombo, editProfileButton, removeProfileButton, backupButton, showBackup, workspaceText});
		
		initializeValues();
	}

	private void initializeValues() {
		Workspace workspace = dinoage.getWorkspace();
		if (workspace == null) {
			return;
		}
		String selection = CREATE_NEW_PROFILE_TEXT;
		
		workspaceText.setText(workspace.getWorkspaceLocation());
		profilesCombo.removeAll();
		profilesCombo.add(selection);
		
		Collection<Profile> profiles = workspace.getProfiles();
		for (Profile profile : profiles) {
			profilesCombo.add(profile.getProfileName());
		}
		profilesCombo.setText(selection);
		setStatusText(ResourceManager.getMessage(
				ResourceManager.KEY_MESSAGE_READY_HREF, new String[] {ResourceManager.KEY_RELAX_URL}));
	}
	
	private void enableButtons(boolean isEnabled) {
		switchWorkspaceButton.setEnabled(isEnabled);
		profilesCombo.setEnabled(isEnabled);
		editProfileButton.setEnabled(isEnabled);
		removeProfileButton.setEnabled(isEnabled);
	}

	public void notifyFinished(ConnectionEvent event) {
		final String sURL = event.getRequest().getURL();
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				setStatusText(ResourceManager.getMessage(
						ResourceManager.KEY_MESSAGE_DONE_HREF, new String[] {sURL, sURL}));
				
			}
		});
	}

	public void notifyRequesting(ConnectionEvent event) {
		final String sURL = event.getRequest().getURL();
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				setStatusText(ResourceManager.getMessage(ResourceManager.KEY_MESSAGE_REQUESTING_HREF, new String[] {sURL, sURL}));
			}
		});
	}

	public void notifyResponding(ConnectionEvent event) {
	}
	
	private void setStatusText(String statusText) {
		statusLabel.setText(statusText);
	}
}
