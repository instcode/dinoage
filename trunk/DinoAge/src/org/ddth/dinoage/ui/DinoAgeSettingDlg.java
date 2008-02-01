/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 11:48:11 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.ui;

import java.io.File;

import org.ddth.dinoage.DinoAge;
import org.ddth.dinoage.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DinoAgeSettingDlg extends Dialog {

	private Button backupGuestbookButton;
	private Button backupEntryButton;
	private Label profileLabel;
	private Shell shell;
	private Button startButton;
	private Text profileText;
	private DinoAge dinoage;

	/**
	 * Create the dialog
	 * @param parent
	 */
	public DinoAgeSettingDlg(Shell parent, DinoAge dinoage) {
		super(parent, SWT.NONE);
		this.dinoage = dinoage;
	}

	/**
	 * Open the dialog
	 * @return the result
	 */
	public boolean open() {
		createContents();
		shell.setText(ResourceManager.getMessage(ResourceManager.KEY_BACKUP_SETTINGS_DIALOG_TITLE));
		shell.pack();
		
		// Center the shell
		UniversalUtil.centerWindow(shell);
		
		boolean isRunning = false;
		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return isRunning;
	}

	/**
	 * Create contents of the dialog
	 */
	protected void createContents() {
		shell = new Shell(getParent().getDisplay(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
		shell.setLayout(new FillLayout());
		Composite composite = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		label.setText(ResourceManager.getMessage(ResourceManager.KEY_BACKUP_SETTING_INFORMATION_MESSAGE));
		
		profileLabel = new Label(composite, SWT.NONE);
		profileLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_PROFILE_NAME));
		GridData gd_profileLabel = new GridData(SWT.LEFT, SWT.CENTER, false, true);
		gd_profileLabel.widthHint = 62;
		profileLabel.setLayoutData(gd_profileLabel);

		profileText = new Text(composite, SWT.BORDER);
		profileText.setText(ResourceManager.KEY_PRODUCT_AUTHOR);
		final GridData gd_profileText = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gd_profileText.widthHint = 84;
		gd_profileText.heightHint = 15;
		profileText.setLayoutData(gd_profileText);

		backupEntryButton = new Button(composite, SWT.CHECK);
		final GridData gd_entryButton = new GridData(SWT.FILL, SWT.CENTER, false, false);
		backupEntryButton.setLayoutData(gd_entryButton);
		backupEntryButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_BLOG_ENTRY));
		new Label(composite, SWT.NONE);

		backupGuestbookButton = new Button(composite, SWT.CHECK);
		final GridData gd_guestbookButton = new GridData(SWT.FILL, SWT.CENTER, false, false);
		backupGuestbookButton.setLayoutData(gd_guestbookButton);
		backupGuestbookButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_GUESTBOOK));

		startButton = new Button(composite, SWT.NONE);
		startButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				if (!backupEntryButton.getSelection() && !backupGuestbookButton.getSelection()) {
					UniversalUtil.showMessageBox(shell, shell.getText(),
							ResourceManager.getMessage(ResourceManager.KEY_PLZ_CHOOSE_BACKUP_OPTION_MESSAGE));
					return;
				}
				dinoage.getWorkingState().setBackupEntry(backupEntryButton.getSelection());
				dinoage.getWorkingState().setBackupGuestbook(backupGuestbookButton.getSelection());
				dinoage.getWorkingState().setProfile(profileText.getText());
				int answer = SWT.YES;
				File profileFolder = new File(profileText.getText());
				if (!profileFolder.mkdirs()) {
					answer = UniversalUtil.showConfirmDlg(shell, shell.getText(),
							ResourceManager.getMessage(ResourceManager.KEY_DUPLICATE_PROFILE_NAME_DETECTED_MESSAGE,
									new String[] { ResourceManager.KEY_PRODUCT_NAME, profileText.getText(), profileFolder.getAbsolutePath() }));
					if (answer == SWT.YES) {
						answer = UniversalUtil.showConfirmDlg(shell, shell.getText(),
							ResourceManager.getMessage(ResourceManager.KEY_READ_WARNING_CONFIRM_MESSAGE));
					}
				}
				if (answer == SWT.YES) {
					dinoage.backup();
					shell.close();
				}
			}
		});
		final GridData gd_startButton = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gd_startButton.widthHint = 57;
		startButton.setLayoutData(gd_startButton);
		startButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_START));
	}

}
