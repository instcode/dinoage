/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 15, 2008 10:07:43 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class UniversalUtil {

	public static void showMessageBox(Shell shell, String title, String message) {
		MessageBox dlg = new MessageBox(shell, SWT.OK);
		dlg.setText(title);
		dlg.setMessage(message);
		dlg.open();
	}
	
	public static int showConfirmDlg(Shell shell, String title, String message) {
		MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		dlg.setText(title);
		dlg.setMessage(message);
		return dlg.open();
	}
	
	public static void centerWindow(Shell shell) {
		Rectangle displayBounds = shell.getDisplay().getBounds();
		int nLeft = (displayBounds.width - shell.getSize().x) / 2;
		int nTop = (displayBounds.height - shell.getSize().y) / 2;
		shell.setBounds(nLeft, nTop, shell.getSize().x, shell.getSize().y);
	}
}
