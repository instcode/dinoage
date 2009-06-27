package org.ddth.dinoage.eclipse.ui.views;

import org.ddth.dinoage.eclipse.Activator;
import org.ddth.http.core.SessionChangeEvent;
import org.ddth.http.core.SessionChangeListener;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IEvaluationService;

public class WorkspaceView extends ViewPart implements SessionChangeListener {
	public static final String ID = "org.ddth.dinoage.ui.views.workspace";
	private TreeViewer viewer;

	/**
	 * The constructor.
	 */
	public WorkspaceView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ProjectExplorerContentProvider());
		viewer.setLabelProvider(new ProjectExplorerLabelProvider());
		viewer.setSorter(new ViewerSorter());
		viewer.setInput(Activator.getDefault().getDinoAge().getWorkspace());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IEvaluationService service = (IEvaluationService) getSite()
						.getService(IEvaluationService.class);
				service.requestEvaluation("org.ddth.dinoage.viewer.selection");
				service.requestEvaluation("org.ddth.dinoage.viewer.running");
			}
		});
		getSite().setSelectionProvider(viewer);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "org.ddth.dinoage.viewer");
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	public void setInput(Object input) {
		viewer.setInput(input);
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				WorkspaceView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillContextMenu(IMenuManager manager) {
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				try {
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					handlerService.executeCommand("org.ddth.dinoage.command.profile.open", null);
				}
				catch (Exception e) {
				}
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void sessionChanged(SessionChangeEvent event) {
		// This method might be invoked in a worker thread, to eliminate
		// illegal thread access, the invocation of evaluation service 
		// should asynchronously run within UI thread. 
		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IEvaluationService service = (IEvaluationService) getSite().getService(
						IEvaluationService.class);
				service.requestEvaluation("org.ddth.dinoage.viewer.running");
			}
		});
	}
}