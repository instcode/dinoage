/****************************************************
 * $Project: org.ddth.dinoage
 * $Date:: Jun 30, 2009
 * $Revision:
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.dinoage.eclipse.ui.wizard;

import java.net.URI;

import org.ddth.blogging.api.BlogUtil;
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
		super("ProfileWizardPage");
		this.workspace = workspace;
		this.profile = profile;
		setTitle(ResourceManager.getMessage(ResourceManager.KEY_PROFILE_DIALOG_TITLE));
		setDescription(ResourceManager.getMessage(ResourceManager.KEY_PROFILE_WIZARD_DESCRIPTION));
		createListeners();
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		
		GridLayout layout = new GridLayout(3, false);
		container.setLayout(layout);

		final Label profileURLLabel = new Label(container, SWT.NONE);
		profileURLLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_PROFILE_URL));

		profileURLText = new Text(container, SWT.BORDER);
		profileURLText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		profileURLText.addModifyListener(checkModifyListener);

		fixButton = new Button(container, SWT.NONE);
		final GridData gd_fixButton = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_fixButton.widthHint = 60;
		fixButton.setLayoutData(gd_fixButton);
		fixButton.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_FIX));
		fixButton.addSelectionListener(selectFixListener);

		final Label profileLabel = new Label(container, SWT.NONE);
		profileLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_PROFILE_NAME));
		profileLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		profileNameText = new Text(container, SWT.BORDER);
		final GridData gd_profileNameText = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_profileNameText.widthHint = 300;
		profileNameText.setLayoutData(gd_profileNameText);
		profileNameText.addModifyListener(checkModifyListener);

		saveAndUpdate(true);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
	}

	private boolean validate() {
		String profileName = profileNameText.getText();
		String profileURL = profileURLText.getText();
		setDescription(ResourceManager.getMessage(ResourceManager.KEY_PROFILE_WIZARD_DESCRIPTION));
		if (profileNameText.getEditable()) {
			Profile profile2 = workspace.getProfile(profileName);
			if (profile2 != null && !profile2.equals(profile)) {
				String message = ResourceManager.getMessage(
						ResourceManager.KEY_MESSAGE_EXISTED_PROFILE, new String[] { profileName }
				);
				setErrorMessage(message);
			}
		}
		try {
			URI.create(profileURL);
			return (profileName.trim().length() > 0);
		}
		catch (IllegalArgumentException e) {
			setErrorMessage(ResourceManager.getMessage(ResourceManager.KEY_PROFILE_WIZARD_INVALID_PROFILE_URL));
		}
		return false;
	}

	protected void createListeners() {
		checkModifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				if (event.getSource() == profileURLText) {
					fixButton.setEnabled(true);
				}
				setPageComplete(validate());
			}
		};
		selectFixListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fixButton.setEnabled(false);
				final ProfileGrabberSession session = Activator.getDefault().getDinoAge().createProfileGrabberSession();
				session.addConnectionListener(new ConnectionListener() {
					@Override
					public void notifyEvent(ConnectionEvent event) {
						if (event.getEventType() != ConnectionEvent.REQUEST_FINISHED) {
							return;
						}
						fixButton.getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								String homepage = session.getAuthor().getUrl();
								if (!homepage.isEmpty()) {
									if (profileNameText.getText().isEmpty()) {
										String profileName = BlogUtil.normalize(session.getAuthor().getName());
										profileNameText.setText(profileName.toLowerCase());
									}
									profileURLText.setText(homepage);
								}
								else {
									setErrorMessage(ResourceManager.getMessage(ResourceManager.KEY_PROFILE_WIZARD_INVALID_PROFILE_URL));
								}
							}
						});
					}
				});
				session.start();
				session.queue(new Request(profileURLText.getText()));
			}
		};
	}

	public void saveAndUpdate(boolean isUpdate) {
		if (isUpdate) {
			boolean isEditable = (profile.getProfileName() == null);
			profileNameText.setText((isEditable ? "" : profile.getProfileName()));
			profileURLText.setText((profile.getProfileURL() == null ? "" : profile.getProfileURL()));
			profileNameText.setEditable(isEditable);
		}
		else {
			profile.setProfileName(profileNameText.getText());
			profile.setProfileURL(profileURLText.getText());
		}
	}
}
