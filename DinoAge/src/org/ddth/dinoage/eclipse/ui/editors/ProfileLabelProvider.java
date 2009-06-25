/****************************************************
 * $Project: DinoAge
 * $Date:: Jun 25, 2009
 * $Revision:
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.dinoage.eclipse.ui.editors;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author khoanguyen
 *
 */
public class ProfileLabelProvider extends LabelProvider implements ITableLabelProvider {

	public enum BlogEntryColumn {
		CHECK("", 5),
		ENTRY("Entry", 70),
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

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == BlogEntryColumn.CHECK.index()) {
			return null;
		}
		else if (columnIndex == BlogEntryColumn.ENTRY.index()) {
			return "Helloooooooooo";
		}
		else if (columnIndex == BlogEntryColumn.DATE.index()) {
			return "KeKeKE";
		}
		return null;
	}

}
