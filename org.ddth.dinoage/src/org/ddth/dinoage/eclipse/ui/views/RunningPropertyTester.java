/****************************************************
 * $Project: DinoAge
 * $Date:: Jun 22, 2009
 * $Revision:
 * $Author:: khoanguyen
 * $Comment:
 **************************************************/
package org.ddth.dinoage.eclipse.ui.views;

import org.ddth.dinoage.DinoAge;
import org.ddth.dinoage.core.BrowsingSession;
import org.ddth.dinoage.eclipse.Activator;
import org.ddth.dinoage.eclipse.ui.model.ProfileNode;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;

public class RunningPropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		IWorkbenchWindow workbenchWindow = (IWorkbenchWindow) receiver;
		IViewPart view = workbenchWindow.getActivePage().findView((String)args[0]);
		if (view != null) {
			IStructuredSelection selection = (IStructuredSelection) view.getSite().getSelectionProvider().getSelection();
			Object firstElement = selection.getFirstElement();
			if (selection.size() == 1 && firstElement.getClass().getCanonicalName().equals(args[1])) {
				ProfileNode profileNode = (ProfileNode) firstElement;
				DinoAge dinoAge = Activator.getDefault().getDinoAge();
				BrowsingSession session = dinoAge.getSession(profileNode.getData());
				return session.isRunning();
			}
		}
		return true;
	}
}
