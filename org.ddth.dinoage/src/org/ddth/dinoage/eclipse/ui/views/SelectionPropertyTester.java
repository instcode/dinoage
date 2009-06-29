/****************************************************
 * $Project: DinoAge
 * $Date:: Jun 22, 2009
 * $Revision:
 * $Author:: khoanguyen
 * $Comment:
 **************************************************/
package org.ddth.dinoage.eclipse.ui.views;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * A property tester for testing selection on any view which supports
 * {@link ISelectionProvider}. Can test whether or not a given node (by class
 * name) is selected in a given view (by view id).<br>
 * <br>
 * First, define this tester in propertyTesters extension point:
 * <pre>
 * &lt;extension point="org.eclipse.core.expressions.propertyTesters"&gt;
 *     &lt;propertyTester
 *         class="org.ddth.dinoage.eclipse.ui.views.SelectionPropertyTester"
 *         id="org.ddth.dinoage.viewer.selectionPropertyTester"
 *         namespace="org.ddth.dinoage.viewer" properties="selection"
 *         type="org.eclipse.ui.IWorkbenchWindow"&gt;
 *     &lt;/propertyTester&gt;
 * &lt;/extension&gt;</pre>
 * Use it in your property testing:
 * <pre>
 * &lt;enabledWhen&gt;
 *     &lt;with variable="activeWorkbenchWindow"&gt;
 *         &lt;test
 *             args="org.ddth.dinoage.ui.views.profile, org.ddth.dinoage.eclipse.ui.providers.TreeParent"
 *             property="org.ddth.dinoage.viewer.selection"&gt;
 *         &lt;/test&gt;
 *     &lt;/with&gt;
 * &lt;/enabledWhen&gt;</pre>
 * 
 */
public class SelectionPropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		IWorkbenchWindow workbenchWindow = (IWorkbenchWindow) receiver;
		IViewPart view = workbenchWindow.getActivePage().findView((String)args[0]);
		if (view != null) {
			IStructuredSelection selection = (IStructuredSelection) view.getSite().getSelectionProvider().getSelection();
			if (selection.size() == 1 && selection.getFirstElement().getClass().getCanonicalName().equals(args[1])) {
				return true;
			}
		}
		return false;
	}
}
