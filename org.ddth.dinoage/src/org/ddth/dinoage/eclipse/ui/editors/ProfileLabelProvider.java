/****************************************************
 * $Project: DinoAge
 * $Date:: Jun 25, 2009
 * $Revision:
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.dinoage.eclipse.ui.editors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.ddth.blogging.Entry;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Point;

/**
 * @author khoanguyen
 *
 */
public class ProfileLabelProvider extends CellLabelProvider {

	private static final DateFormat ENTRY_DATE_FORMAT = new SimpleDateFormat("dd-MMM-y");

	public enum BlogEntryColumn {
		CHECK("", 5),
		ENTRY("Entry", 60),
		COMMENT("Comment", 10),
		DATE("Date", 25);

		private String name;
		private int weight;

		private BlogEntryColumn(String name, int weight) {
			this.name = name;
			this.weight = weight;
		}
		
		public int index() {
			return ordinal();
		}
		
		public int weight() {
			return weight;
		}

		public String toString() {
			return name;
		}
	}

	public String getToolTipText(Object element) {
		return "<font face=\"verdana\" size=\"2\">" + ((Entry)element).getPost().getContent() + "</font>";
	}

	public Point getToolTipShift(Object object) {
		return new Point(0, 0);
	}

	public int getToolTipDisplayDelayTime(Object object) {
		return 0;
	}

	public int getToolTipTimeDisplayed(Object object) {
		return 60 * 60 * 1000; // 1 hour =))
	}

	public void update(ViewerCell cell) {
		Entry entry = (Entry) cell.getElement();
		int columnIndex = cell.getColumnIndex();
		if (columnIndex == BlogEntryColumn.CHECK.index()) {
			return;
		}
		else if (columnIndex == BlogEntryColumn.ENTRY.index()) {
			cell.setText(entry.getPost().getTitle());
		}
		else if (columnIndex == BlogEntryColumn.COMMENT.index()) {
			cell.setText(String.valueOf(entry.getComments().size()));
		}
		else if (columnIndex == BlogEntryColumn.DATE.index()) {
			cell.setText(ENTRY_DATE_FORMAT.format(entry.getPost().getDate()));
		}
	}
}
