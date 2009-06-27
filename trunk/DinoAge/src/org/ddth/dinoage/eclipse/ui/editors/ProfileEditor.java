package org.ddth.dinoage.eclipse.ui.editors;

import org.ddth.blogging.Entry;
import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.core.Profile;
import org.ddth.dinoage.eclipse.ui.editors.ProfileLabelProvider.BlogEntryColumn;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class ProfileEditor extends EditorPart {
	public static final String ID = "org.ddth.ui.editors.blogview";
	private CheckboxTableViewer viewer;

	public ProfileEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public CheckboxTableViewer getViewer() {
		return viewer;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		Composite banner = createBanner(composite);
		banner.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL,
				GridData.VERTICAL_ALIGN_BEGINNING, true, false));
		
		viewer = createTableViewer(composite);
		viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		viewer.setInput(getEditorInput());
		ToolTipSupport support = new ToolTipSupport(viewer, ToolTip.NO_RECREATE | SWT.SHADOW_ETCHED_OUT, false);
		support.setHideOnMouseDown(false);
	}

	private final Composite createBanner(Composite composite) {
		Profile profile = (Profile) getEditorInput().getAdapter(Profile.class);
		
		Composite banner = new Composite(composite, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.numColumns = 2;
		banner.setLayout(layout);

		Font boldFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);

		Label profileLabel = new Label(banner, SWT.WRAP);
		profileLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_PROFILE_NAME));
		profileLabel.setFont(boldFont);
		Label profileText = new Label(banner, SWT.WRAP);
		profileText.setText(profile.getProfileName());

		Label profileUrlLabel = new Label(banner, SWT.WRAP);
		profileUrlLabel.setText(ResourceManager.getMessage(ResourceManager.KEY_LABEL_PROFILE_URL));
		profileUrlLabel.setFont(boldFont);

		Link profileUrlLink = new Link(banner, SWT.NONE);
		profileUrlLink.setText(ResourceManager.getMessage(ResourceManager.KEY_MESSAGE_FULL_HREF,
			new Object[] { profile.getProfileURL(), profile.getProfileURL() })
		);
		profileUrlLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(e.text);
			}
		});
		return banner;
	}
	
	private final CheckboxTableViewer createTableViewer(Composite parent) {
		CheckboxTableViewer viewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.MULTI);
		viewer.getTable().setHeaderVisible(true);
		
		TableLayout layout = new TableLayout();
		for (BlogEntryColumn data : BlogEntryColumn.values()) {
			final TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);
			column.setText(data.toString());
			layout.addColumnData(new ColumnWeightData(data.weight(), true));
		}
		viewer.getTable().setLayout(layout);
		
		viewer.setContentProvider(new ProfileContentProvider());
		viewer.setLabelProvider(new ProfileLabelProvider());
		viewer.setInput(new Object());
		createMenu(viewer.getTable());
		return viewer;
	}

	/**
	 * Creates and assigns the popup menu for the component.
	 */
	private void createMenu(Composite composite) {
		Menu menu = new Menu(composite.getShell(), SWT.POP_UP);
		
		MenuItem checkAllItem = new MenuItem(menu, SWT.NONE);
		checkAllItem.setText("Select All");
		checkAllItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/*
				 * Right now, we can't use viewer.setAllChecked(true)
				 * because getTable().getItems() won't work with lazy
				 * load table view. Waiting for this bug fixed.
				 */
				Table table = viewer.getTable();
				int itemCount = table.getItemCount();
				for(int i = 0; i < itemCount; i++){
	                TableItem item = table.getItem(i);
	                // Invoke #getChecked() to fix a silly bug by
	                // using lazy load table view
	                item.getChecked();
	                item.setChecked(true);
	            }
			}
		});
		
		MenuItem checkNoneItem = new MenuItem(menu, SWT.NONE);
		checkNoneItem.setText("Select None");
		checkNoneItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(false);
			}
		});
		
		MenuItem invertSelectionItem = new MenuItem(menu, SWT.NONE);
		invertSelectionItem.setText("Invert Selection");
		invertSelectionItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Table table = viewer.getTable();
				int itemCount = table.getItemCount();
				for(int i = 0; i < itemCount; i++){
	                TableItem item = table.getItem(i);
	                item.setChecked(!item.getChecked());
	            }
			}
		});
		composite.setMenu(menu);
	}
	
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private static class ToolTipSupport extends ColumnViewerToolTipSupport {

		protected ToolTipSupport(ColumnViewer viewer, int style,
				boolean manualActivation) {
			super(viewer, style, manualActivation);
		}

		protected Composite createViewerToolTipContentArea(Event event,
				ViewerCell cell, Composite parent) {
			Composite composite = new Composite(parent, SWT.BORDER | SWT.RESIZE);
			GridLayout layout = new GridLayout(1, false);
			layout.horizontalSpacing = 5;
			layout.marginWidth = 5;
			layout.marginHeight = 5;
			layout.verticalSpacing = 5;
			composite.setLayout(layout);
			
			Label entryTitle = new Label(composite, SWT.NONE);
			entryTitle.setText(((Entry)cell.getElement()).getPost().getTitle());
			
			Link entryLink = new Link(composite, SWT.NONE);
			String entryURL = ((Entry)cell.getElement()).getUrl();
			
			entryLink.setText(ResourceManager.getMessage(ResourceManager.KEY_MESSAGE_FULL_HREF,
				new Object[] { entryURL, entryURL})
			);
			entryLink.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					ToolTipSupport.this.hide();
					Program.launch(e.text);
				}
			});
			Browser browser = new Browser(composite, SWT.BORDER);
			browser.setText(getText(event));
			browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			composite.setSize(400, 200);
			return composite;
		}
	}
}
