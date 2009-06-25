/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 11:48:11 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.eclipse.ui.editors;

import java.text.DateFormat;

import org.ddth.blogging.Entry;
import org.ddth.dinoage.eclipse.ui.editors.ProfileLabelProvider.BlogEntryColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class ProfileTableView extends Composite {

	private Table m_table;

	public ProfileTableView(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
	}

	/**
	 * Creates a table item for a file. When a table item is created, it is
	 * initialized to show the name, size, type and modified date for the file.
	 * 
	 * @param file
	 *            the file to provide data for the item
	 * @return the new table item
	 */
	public TableItem createTableItem(Entry entry) {
		if (getShell().isDisposed())
			return null;
		TableItem item = new TableItem(m_table, SWT.NULL);
		item.setData(entry);
		item.setText(BlogEntryColumn.CHECK.index(), "");
		item.setText(BlogEntryColumn.ENTRY.index(), entry.getPost().getTitle());

		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
		String date = dateFormat.format(entry.getPost().getDate());
		item.setText(BlogEntryColumn.DATE.index(), date);

		return item;
	}
}