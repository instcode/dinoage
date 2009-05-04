package org.ddth.dinoage.ui;

import java.text.DateFormat;
import java.util.Date;

import org.ddth.blogging.BlogPost;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class BlogEntryView extends Composite {
	public enum BlogEntryData {
		CHECK("", 30),
		ENTRY("Entry", 400),
		DATE("Date", 120),
		PROFILE("Profile", 80);

		private String name;
		private int width;

		private BlogEntryData(String name, int width) {
			this.name = name;
			this.width = width;
		}
		
		public int index() {
			return ordinal();
		}

		public String toString() {
			return name;
		}
	}

	private Table m_table;

	public BlogEntryView(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
		createTable();
		createTableMenu();
	}

	/**
	 * Creates the table. A table is used to show individual files and folders
	 * within a directory. The table is created with columns for the file and
	 * folder names, size, type and modified date. The table is initially sorted
	 * in ascending order by name.
	 */
	private final void createTable() {
		m_table = new Table(this, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION
				| SWT.CHECK);
		m_table.setHeaderVisible(true);

		/* Create the columns */
		for (BlogEntryData data : BlogEntryData.values()) {
			final TableColumn column = new TableColumn(m_table, SWT.CENTER);
			column.setText(data.name);
			column.setWidth(data.width);
			column.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					tableColumnSelected(column);
				}
			});
		}

		/*
		 * Add a selection listener to show the statistics associated with the
		 * selected items on the status line. When the user reselects an item,
		 * queue an in-line edit session to allow the name of the folder or
		 * directory to be changed.
		 */
		m_table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

			}

			public void widgetDefaultSelected(SelectionEvent event) {
				TableItem[] items = m_table.getSelection();
				TableItem item = items.length == 1 ? items[0] : null;
				if (item != null) {
					System.out.println("Selected...");
				}
			}
		});
		m_table.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent event) {
				// Empty
			}

			public void focusLost(FocusEvent event) {

			}
		});
		m_table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.character == SWT.DEL) {
					// Delete
				}
			}
		});

		for (int i = 0; i < 5; i++) {
			BlogPost entry = new BlogPost("Blog " + i, "<<Empty>>...");
			createTableItem(entry);
		}
	}

	/**
	 * Creates a table item for a file. When a table item is created, it is
	 * initialized to show the name, size, type and modified date for the file.
	 * 
	 * @param file
	 *            the file to provide data for the item
	 * @return the new table item
	 */
	public TableItem createTableItem(BlogPost entry) {
		if (getShell().isDisposed())
			return null;
		TableItem item = new TableItem(m_table, SWT.NULL);
		item.setData(entry);
		item.setText(BlogEntryData.CHECK.index(), "");
		item.setText(BlogEntryData.ENTRY.index(), entry.getTitle());
		item.setText(BlogEntryData.PROFILE.index(), "instcode");

		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
		String date = dateFormat.format(new Date());
		item.setText(BlogEntryData.DATE.index(), date);

		return item;
	}

	/**
	 * Creates and assigns the popup menu for the table.
	 */
	private void createTableMenu() {
		Menu menu = new Menu(getShell(), SWT.POP_UP);
		MenuItem item1 = new MenuItem(menu, SWT.NONE);
		item1.setText("Item1");
		MenuItem item2 = new MenuItem(menu, SWT.NONE);
		item2.setText("Item2");
		m_table.setMenu(menu);
	}

	/**
	 * Performs the table column selected action. When a table column is
	 * selected, the table is sorted based on the column. If the same column is
	 * reselected, the sorting order is inverted.
	 * 
	 * @param column
	 *            the table column that was selected
	 */
	private void tableColumnSelected(TableColumn column) {
		// TODO - use a sort icon to show the sorting order
	}

	public Item[] getSelection() {
		return m_table.getSelection();
	}

	public void selectAll() {
		m_table.selectAll();
	}
}