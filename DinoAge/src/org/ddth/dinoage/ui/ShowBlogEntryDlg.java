package org.ddth.dinoage.ui;

import org.ddth.dinoage.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class ShowBlogEntryDlg extends Dialog {

	protected Object result;
	protected Shell shell;
	private ToolBar toolBar;
	private BlogEntryView blogEntryView;

	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public ShowBlogEntryDlg(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Create the dialog
	 * @param parent
	 */
	public ShowBlogEntryDlg(Shell parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Open the dialog
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	/**
	 * Create contents of the dialog
	 */
	protected void createContents() {
		shell = new Shell(getParent(), SWT.TITLE | SWT.BORDER | SWT.RESIZE | SWT.CLOSE);
		shell.setLayout(new FormLayout());
		shell.setSize(701, 602);
		shell.setText("SWT Dialog");
		
		blogEntryView = new BlogEntryView(shell, SWT.NONE);
		toolBar = new ToolBar(shell, SWT.FLAT);
		
		ToolItem searchItem = new ToolItem(toolBar, SWT.PUSH);
		searchItem.setImage(ResourceManager.EXPORT_ICON);
		searchItem.setToolTipText(ResourceManager.getMessage("Search"));
		searchItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				MessageBox dialog = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
				dialog.setText(shell.getText());
				dialog.setMessage("Hellooooooooooo");
				dialog.open();
			}
		});
		
		Point point = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		toolBar.setSize(point.x, point.y);
		
		createLayout();
		//
	}
	
	private void createLayout() {
		FormLayout layout = new FormLayout();
		shell.setLayout(layout);

		FormData toolData = new FormData();
		toolData.top = new FormAttachment(0, 5);
		toolData.left = new FormAttachment(0, 5);
		toolData.right = new FormAttachment(100, -5);
		int offset = toolBar.getSize().y + 5;
		toolData.bottom = new FormAttachment(0, offset);
		toolBar.setLayoutData(toolData);

		final FormData formData = new FormData();
		formData.top = new FormAttachment(toolBar, 5);
		formData.left = new FormAttachment(0, 5);
		formData.bottom = new FormAttachment(100, -5);
		formData.right = new FormAttachment(100, -5);
		blogEntryView.setLayoutData(formData);
	}
}
