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
import org.ddth.dinoage.model.Profile;
import org.ddth.grabber.core.handler.ConnectionListener;
import org.ddth.grabber.core.handler.SessionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DinoAgeWindow implements ConnectionListener, SessionListener {

	private Button backupButton;
	private Text profileURLText;
	private Shell shell;
	private DinoAge dinoage;
	private SelectionAdapter backupListener;
	private SelectionAdapter stopListener;
	private Link statusLabel;
	private boolean shouldClose = false;

	public DinoAgeWindow(DinoAge dinoage) {
		this.dinoage = dinoage;
	}
	
	public void open() {
		
		createListeners();
		createContent();
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		
		// Center the shell
		UniversalUtil.centerWindow(shell);

		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent event) {
				int answer = SWT.YES;
				if (!shouldClose && dinoage.getSession().isRunning()) {
					String sMessage = ResourceManager.getMessage(
							ResourceManager.KEY_CONFIRM_EXIT_WHEN_RUNNING,
							new String[] {dinoage.getWorkingState().getProfile()});
					answer = UniversalUtil.showConfirmDlg(shell, shell.getText(), sMessage);
					if (answer == SWT.YES) {
						shouldClose = true;
						statusLabel.setText(
								ResourceManager.getMessage(
										ResourceManager.KEY_WAIT_FOR_EXITING, new String[] {ResourceManager.KEY_RELAX_URL, ResourceManager.KEY_PRODUCT_NAME}));
						dinoage.stop();
						backupButton.setEnabled(false);
						answer = SWT.NO;
					}
				}
				event.doit = (answer == SWT.YES);
			}
		});
		
		shell.setText(ResourceManager.KEY_PRODUCT_DIALOG_TITLE);
		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	private void createListeners() {
		stopListener = new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				if (dinoage.isRunning()) {
					dinoage.stop();
					statusLabel.setText(ResourceManager.getMessage(
							ResourceManager.KEY_WAIT_FOR_STOPPING, new String[] {ResourceManager.KEY_RELAX_URL}));
					backupButton.setEnabled(false);
				}
			}
		};
		
		backupListener = new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				String profileURL = profileURLText.getText();
				String profileId = Profile.getProfileId(profileURL);
				Profile state = dinoage.getState(profileId);
				if (state != null) {
					int answer;
					if (!profileURL.equals(state.getProfileURL())) {
						String message = ResourceManager.getMessage(
								ResourceManager.KEY_CONFLICT_RESUMABLE_FILE,
								new String[] {profileId, state.getProfile(), state.getProfileId(), ResourceManager.KEY_PRODUCT_NAME}
						);
						
						UniversalUtil.showMessageBox(shell, shell.getText(), message);
						answer = SWT.NO;
					}
					else {
						String message = ResourceManager.getMessage(
								ResourceManager.KEY_RESUME_RETRIEVING_CONFIRM,
								new String[] {profileId, state.getProfile()}
						);
						answer = UniversalUtil.showConfirmDlg(shell, shell.getText(), message);
					}
					if (answer == SWT.YES) {
						dinoage.setWorkingState(state);
						dinoage.backup();
					}
					else {
						state = null;
					}
				}
				
				// Create a new state if it's not available
				if (state == null) {
					state = new Profile();
					state.setProfileURL(profileURL);
					dinoage.setWorkingState(state);
					// Show capture dialog
					DinoAgeSettingDlg dlg = new DinoAgeSettingDlg(shell, dinoage);
					dlg.open();
				}
				// Update backupButton action
				if (dinoage.isRunning()) {
					backupButton.removeSelectionListener(backupListener);
					backupButton.addSelectionListener(stopListener);
					backupButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_STOP_BACKUP));
				}
			}
		};
	}
	
	private void createContent() {
		shell = new Shell(Display.getDefault(), SWT.CENTER | SWT.CLOSE);
		shell.setLayout(new FillLayout());
		
		Composite composite = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);
		
		Label profileURLLabel = new Label(composite, SWT.NONE);
		profileURLLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_Y360_PROFILE));
		GridData gd_profileURLLabel = new GridData(SWT.CENTER, SWT.CENTER, false, true);
		gd_profileURLLabel.widthHint = 60;
		profileURLLabel.setLayoutData(gd_profileURLLabel);

		profileURLText = new Text(composite, SWT.BORDER);
		profileURLText.setText(ResourceManager.getMessage(ResourceManager.KEY_RELAX_URL));
		GridData gd_profileURLText = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gd_profileURLText.heightHint = 15;
		profileURLText.setLayoutData(gd_profileURLText);

		backupButton = new Button(composite, SWT.NONE);
		backupButton.addSelectionListener(backupListener);
		GridData gd_backupButton = new GridData(60, SWT.DEFAULT);
		backupButton.setLayoutData(gd_backupButton);
		backupButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_BACKUP_BUTTON_TITLE));

		statusLabel = new Link(composite, SWT.NONE | SWT.NO_FOCUS);
		statusLabel.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		statusLabel.setText(
				ResourceManager.getMessage(ResourceManager.KEY_MESSAGE_READY_HREF, new String[] {ResourceManager.KEY_RELAX_URL}));
		statusLabel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Program.launch(event.text);
			}
		});
		GridData gd_status = new GridData(SWT.LEFT, SWT.CENTER, false, true, 3, 1);
		gd_status.widthHint = 550;
		statusLabel.setLayoutData(gd_status);
		
		composite.setTabList(new Control[] {profileURLLabel, profileURLText, backupButton});
	}

	public void notifyFinished(final String sURL, boolean isCompletedWithoutError) {
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (!shouldClose) {
					statusLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_MESSAGE_DONE_HREF, new String[] {sURL}));
					dinoage.savePoint();
				}
			}
		});
	}

	public void notifyRequesting(final String sURL) {
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (!shouldClose) {
					statusLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_MESSAGE_REQUESTING_HREF, new String[] {sURL}));
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
					statusLabel.setText(ResourceManager.getMessage(
							ResourceManager.KEY_MESSAGE_READY_HREF, new String[] {ResourceManager.KEY_RELAX_URL}));
					backupButton.setEnabled(true);
				}
				else {
					shell.close();
				}
			}
		});
	}
}
