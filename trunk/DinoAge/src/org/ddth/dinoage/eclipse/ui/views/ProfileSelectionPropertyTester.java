package org.ddth.dinoage.eclipse.ui.views;

import org.ddth.dinoage.eclipse.ui.providers.TreeParent;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ProfileSelectionPropertyTester extends PropertyTester {

	public ProfileSelectionPropertyTester() {
		// TODO Auto-generated constructor stub
	}

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			ProfileListView view = (ProfileListView) workbenchWindow.getActivePage().findView(ProfileListView.ID);
			if (view != null) {
				IStructuredSelection selection = (IStructuredSelection)view.getSite().getSelectionProvider().getSelection();
				if (selection.size() == 1 && selection.getFirstElement() instanceof TreeParent) {
					return true;
				}
			}
		}
		return false;
	}
}
