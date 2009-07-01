/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 11:48:11 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.eclipse.ui.wizard;

import java.io.File;

import org.ddth.blogging.Author;
import org.ddth.blogging.Blog;
import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.eclipse.ui.model.ExportModel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExportWizardPage extends WizardPage {
	private Text blogURL;
	private Text blogTitle;
	private Text blogDescription;
	private Text username;
	private Text homepage;
	private Text avatar;
	private Text outputFile;

	private ExportModel model;
	/**
	 * Create the wizard.
	 */
	public ExportWizardPage(ExportModel model) {
		super("ExportWizardPage");
		setTitle(ResourceManager.getMessage(ResourceManager.KEY_LABEL_WORDPRESS));
		setDescription(ResourceManager.getMessage(ResourceManager.KEY_LABEL_EXPORT_DESCRIPTION));
		this.model = model;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));

		setControl(container);
		{
			Label lblBlog = new Label(container, SWT.NONE);
			lblBlog.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
			lblBlog.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_BLOG_INFORMATION));
		}
		{
			Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		}
		{
			Label lblUrl = new Label(container, SWT.NONE);
			lblUrl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblUrl.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_URL));
		}
		{
			blogURL = new Text(container, SWT.BORDER);
			blogURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		}
		{
			Label lblTitle = new Label(container, SWT.NONE);
			lblTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblTitle.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_TITLE));
		}
		{
			blogTitle = new Text(container, SWT.BORDER);
			blogTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		}
		{
			Label lblDescription = new Label(container, SWT.NONE);
			lblDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblDescription.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_DESCRIPTION));
		}
		{
			blogDescription = new Text(container, SWT.BORDER);
			blogDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		}
		{
			Label lblAuthor = new Label(container, SWT.NONE);
			lblAuthor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
			lblAuthor.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_AUTHOR_DESCRIPTION));
		}
		{
			Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		}
		{
			Label lblHomepage = new Label(container, SWT.NONE);
			lblHomepage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblHomepage.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_USERNAME));
		}
		{
			username = new Text(container, SWT.BORDER);
			username.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		}
		{
			Label lblHomepage = new Label(container, SWT.NONE);
			lblHomepage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblHomepage.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_HOMEPAGE));
		}
		{
			homepage = new Text(container, SWT.BORDER);
			homepage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		}
		{
			Label lblAvatar = new Label(container, SWT.NONE);
			lblAvatar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblAvatar.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_AVATAR));
		}
		{
			avatar = new Text(container, SWT.BORDER);
			avatar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		}
		{
			Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		}
		{
			Label lblOutput = new Label(container, SWT.NONE);
			lblOutput.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblOutput.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_FILE_OUTPUT));
		}
		{
			outputFile = new Text(container, SWT.BORDER);
			outputFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		}
		{
			Button btnBrowse = new Button(container, SWT.NONE);
			btnBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			btnBrowse.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_BROWSE_ELLIPSIS));
			btnBrowse.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FileDialog dialog = new FileDialog(container.getShell(), SWT.SAVE);
					dialog.setFileName(outputFile.getText());
					String filePath = dialog.open();
					if (filePath != null) {
						outputFile.setText(filePath);
					}
				}
			});
		}
		
		saveAndUpdate(true);
	}
	
	public void saveAndUpdate(boolean isUpdate) {
		if (isUpdate) {
			Blog blog = model.getBlog();
			blogURL.setText(blog.getUrl());
			blogTitle.setText(blog.getTitle());
			blogDescription.setText(blog.getDescription());
			Author author = blog.getAuthor();
			username.setText(author.getName());
			homepage.setText(author.getUrl());
			avatar.setText(author.getAvatar());
			outputFile.setText(model.getOutputFile().getAbsolutePath());
		}
		else {
			Blog blog = model.getBlog();
			blog.setUrl(blogURL.getText());
			blog.setTitle(blogTitle.getText());
			blog.setDescription(blogDescription.getText());
			Author author = blog.getAuthor();
			author.setName(username.getText());
			author.setUrl(homepage.getText());
			author.setAvatar(avatar.getText());
			model.setOutputFile(new File(outputFile.getText()));
		}
	}
}
