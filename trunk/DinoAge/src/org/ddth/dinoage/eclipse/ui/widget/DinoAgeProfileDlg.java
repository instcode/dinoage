/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 11:48:11 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.eclipse.ui.widget;

import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.core.Profile;
import org.ddth.dinoage.core.Workspace;
import org.ddth.dinoage.eclipse.ui.UniversalUtil;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DinoAgeProfileDlg extends Dialog {

	private Shell shell;
	private Text profileURLText;
	private Text profileText;
	private Button okButton; 
	private Workspace workspace;
	private Profile profile;
	
	private int answer;
	
	private SelectionListener saveListener;
	private ModifyListener checkModifyListener;
	private ControlDecoration profileURLDecoration;
	private ControlDecoration profileTextDecoration;

	/**
	 * Create the dialog
	 * @param parent
	 */
	public DinoAgeProfileDlg(Shell parent, Workspace workspace) {
		super(parent, SWT.CENTER);
		this.workspace = workspace;
		this.profile = workspace.createEmptyProfile();
	}

	/**
	 * Open the dialog
	 * @return the result
	 */
	public int open() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
		shell.setText(ResourceManager.getMessage(ResourceManager.KEY_PROFILE_DIALOG_TITLE));
		shell.setLayout(new FillLayout());
		
		createListeners();
		createContents();

		shell.pack();
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

	public Profile getProfile() {
		return profile;
	}

	private boolean checkInputs() {
		String profileName = profileText.getText();
		String profileURL = profileURLText.getText();
		return (profileName.trim().length() > 0 && profileURL.trim().length() > 0);
	}
	
	/**
	 * Create contents of the dialog
	 */
	protected void createListeners() {
		checkModifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				okButton.setEnabled(checkInputs());
			}
		};
		
		saveListener = new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				String profileName = profileText.getText();
				if (profileText.getEditable()) {
					if (workspace.getProfile(profileName) != null) {
						String message = ResourceManager.getMessage(
								ResourceManager.KEY_MESSAGE_EXISTED_PROFILE,
									new String[] { profileName }
						);
						UniversalUtil.showMessageBox(shell, shell.getText(), message);
						return;
					}
				}		
				profile.setProfileName(profileName);
				profile.setProfileURL(profileURLText.getText());
				if (workspace.saveProfile(profile)) {
					answer = SWT.OK;
					shell.close();
				}
			}
		};
	}
	
	private ControlDecoration createControlDecoration(Control control, String hoverText) {
		ControlDecoration controlDecoration = new ControlDecoration(control,
				SWT.LEFT | SWT.TOP);
		controlDecoration.setDescriptionText(hoverText);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION);
		controlDecoration.setImage(fieldDecoration.getImage());
		return controlDecoration;
	}
	
	/**
	 * Create contents of the dialog
	 */
	protected void createContents() {
		Composite composite = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final Label profileURLLabel = new Label(composite, SWT.NONE);
		final GridData gd_profileURLLabel = new GridData(SWT.LEFT, SWT.CENTER, false, true);
		profileURLLabel.setLayoutData(gd_profileURLLabel);
		profileURLLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_PROFILE_URL));

		profileURLText = new Text(composite, SWT.BORDER);
		final GridData gd_profileURLText = new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1);
		gd_profileURLText.heightHint = 15;
		gd_profileURLText.widthHint = 300;
		profileURLText.setLayoutData(gd_profileURLText);
		profileURLText.addModifyListener(checkModifyListener);
		profileURLDecoration = createControlDecoration(profileURLText, "Please enter profile URL");

		final Label profileLabel = new Label(composite, SWT.NONE);
		profileLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_PROFILE_NAME));
		GridData gd_profileLabel = new GridData(SWT.FILL, SWT.CENTER, false, true);
		profileLabel.setLayoutData(gd_profileLabel);

		profileText = new Text(composite, SWT.BORDER);
		final GridData gd_profileText = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gd_profileText.heightHint = 15;
		profileText.setLayoutData(gd_profileText);
		profileText.addModifyListener(checkModifyListener);
		profileTextDecoration = createControlDecoration(profileText, "Please enter profile name");

		okButton = new Button(composite, SWT.NONE);
		final GridData gd_okButton = new GridData(SWT.LEFT, SWT.CENTER, false, true);
		gd_okButton.widthHint = 60;
		okButton.setLayoutData(gd_okButton);
		okButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_SAVE));
		okButton.addSelectionListener(saveListener);
		
		profileURLDecoration.show();
		profileTextDecoration.show();
		
		initializeValues();
	}
	
	private void initializeValues() {
		boolean isEditable = (profile.getProfileName() == null);
		profileText.setText((isEditable ? "" : profile.getProfileName()));
		profileURLText.setText((profile.getProfileURL() == null ? "" : profile.getProfileURL()));
		profileText.setEditable(isEditable);
	}
}
