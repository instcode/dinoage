package org.ddth.dinoage.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class DinoAgeDlg extends Dialog {

	protected Object result;
	protected Shell shell;
	private Label status;

	/**
	 * Create the dialog
	 * 
	 * @param parent
	 * @param style
	 */
	public DinoAgeDlg(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Create the dialog
	 * 
	 * @param parent
	 */
	public DinoAgeDlg(Shell parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Open the dialog
	 * 
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
		shell = new Shell(getParent(), SWT.TITLE | SWT.BORDER | SWT.RESIZE
				| SWT.CLOSE);
		shell.setLayout(new FormLayout());
		shell.setSize(800, 600);
		shell.setText("SWT Dialog");

		status = new Label(shell, SWT.NONE);
		status.setText("Status");

		final ToolBar toolBar = new ToolBar(shell, SWT.NONE);
		final FormData fd_toolBar = new FormData();
		fd_toolBar.right = new FormAttachment(100, -5);
		fd_toolBar.left = new FormAttachment(0, 0);
		toolBar.setLayoutData(fd_toolBar);

		final TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		final FormData fd_tabFolder = new FormData();
		fd_tabFolder.right = new FormAttachment(0, 420);
		fd_tabFolder.top = new FormAttachment(0, 115);
		fd_tabFolder.left = new FormAttachment(0, 40);
		tabFolder.setLayoutData(fd_tabFolder);

		new TabItem(tabFolder, SWT.NONE);
		createLayout();
		//
	}

	private void createLayout() {
		FormLayout layout = new FormLayout();
		shell.setLayout(layout);

		final FormData statusData = new FormData();
		statusData.left = new FormAttachment(0, 5);
		statusData.right = new FormAttachment(100, -5);
		statusData.bottom = new FormAttachment(100, -5);
		status.setLayoutData(statusData);
	}

	public static void main(String[] args) {
		DinoAgeDlg dlg = new DinoAgeDlg(new Shell());
		dlg.open();
	}
}
