/****************************************************
 * $Project: org.ddth.dinoage
 * $Date:: Jun 30, 2009
 * $Revision:
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.dinoage.eclipse.ui.wizard;

import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.core.Profile;
import org.ddth.dinoage.core.ProfileGrabberSession;
import org.ddth.dinoage.core.Workspace;
import org.ddth.dinoage.eclipse.Activator;
import org.ddth.http.core.ConnectionEvent;
import org.ddth.http.core.ConnectionListener;
import org.ddth.http.core.connection.Request;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author khoanguyen
 *
 */
public class ProfileWizardPage extends WizardPage {

	private Text profileNameText;
	private Text profileURLText;
	private Button fixButton; 
	private Workspace workspace;
	private Profile profile;

	private ModifyListener checkModifyListener;
	private SelectionListener selectFixListener;

	/**
	 * Create the wizard.
	 */
	public ProfileWizardPage(Workspace workspace, Profile profile) {
		super("Profile");
		this.workspace = workspace;
		this.profile = profile;
		setTitle(ResourceManager.getMessage(ResourceManager.KEY_PROFILE_DIALOG_TITLE));
		setDescription("Create & Edit profile");
		createListeners();
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		container.setLayout(layout);

		final Label profileLabel = new Label(container, SWT.NONE);
		profileLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_PROFILE_NAME));
		profileLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));

		profileNameText = new Text(container, SWT.BORDER);
		final GridData gd_profileNameText = new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1);
		gd_profileNameText.heightHint = 15;
		gd_profileNameText.widthHint = 300;
		profileNameText.setLayoutData(gd_profileNameText);
		profileNameText.addModifyListener(checkModifyListener);

		final Label profileURLLabel = new Label(container, SWT.NONE);
		profileURLLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));
		profileURLLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_PROFILE_URL));

		profileURLText = new Text(container, SWT.BORDER);
		final GridData gd_profileURLText = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gd_profileURLText.heightHint = 15;
		profileURLText.setLayoutData(gd_profileURLText);
		profileURLText.addModifyListener(checkModifyListener);

		fixButton = new Button(container, SWT.NONE);
		final GridData gd_fixButton = new GridData(SWT.LEFT, SWT.CENTER, false, true);
		gd_fixButton.widthHint = 60;
		fixButton.setLayoutData(gd_fixButton);
		fixButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_FIX));
		fixButton.addSelectionListener(selectFixListener);
		
		Label label= new Label(container, SWT.LEFT);
		GridData gd= new GridData();
		gd.verticalAlignment = SWT.FILL;
		gd.heightHint= 150;
		label.setLayoutData(gd);
		
		initializeValues();
		setControl(container);
	}

	private boolean validate() {
		String profileName = profileNameText.getText();
		String profileURL = profileURLText.getText();
		
		if (profileNameText.getEditable()) {
			Profile profile2 = workspace.getProfile(profileName);
			if (profile2 != null && profile2.getProfileName().equalsIgnoreCase(profileName)) {
				String message = ResourceManager.getMessage(
						ResourceManager.KEY_MESSAGE_EXISTED_PROFILE, new String[] { profileName }
				);
				setErrorMessage(message);
			}
		}		
		return (profileName.trim().length() > 0 && profileURL.trim().length() > 0);
	}

	protected void createListeners() {
		checkModifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				setPageComplete(validate());
			}
		};
		selectFixListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				final ProfileGrabberSession session = Activator.getDefault().getDinoAge().createProfileGrabberSession();
				session.addConnectionListener(new ConnectionListener() {
					@Override
					public void notifyEvent(ConnectionEvent event) {
						if (event.getEventType() == ConnectionEvent.REQUEST_FINISHED) {
							System.out.println(session.getAuthor());
						}
					}
				});
				session.start();
				session.queue(new Request(profileURLText.getText()));
			}
		};
	}

	private void initializeValues() {
		boolean isEditable = (profile.getProfileName() == null);
		profileNameText.setText((isEditable ? "" : profile.getProfileName()));
		profileURLText.setText((profile.getProfileURL() == null ? "" : profile.getProfileURL()));
		
		profileNameText.setEditable(isEditable);
	}
}
