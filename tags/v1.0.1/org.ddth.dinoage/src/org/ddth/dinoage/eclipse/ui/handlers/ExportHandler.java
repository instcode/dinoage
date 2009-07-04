package org.ddth.dinoage.eclipse.ui.handlers;

import java.io.File;

import org.ddth.blogging.Author;
import org.ddth.blogging.Blog;
import org.ddth.blogging.Entry;
import org.ddth.dinoage.eclipse.ui.editors.ProfileEditor;
import org.ddth.dinoage.eclipse.ui.model.ExportModel;
import org.ddth.dinoage.eclipse.ui.wizard.DinoAgeExportDlg;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExportHandler extends AbstractHandler {
	private ExportModel model;
	
	public ExportHandler() {
		Blog blog = new Blog();
		blog.setTitle("instcode's blog");
		blog.setUrl("http://instcode.wordpress.com");
		blog.setDescription("Welcome to my blog :D");
		Author author = new Author("instcode", "instcode", "http://instcode.wordpress.com",
				"http://en.gravatar.com/userimage/2251513/e5a20d2422b54b45befee4a92ccd4ae5.jpg");
		blog.addAuthor(author);
		model = new ExportModel(blog, new File("wp.xml"));
	}
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		ProfileEditor editor = (ProfileEditor)window.getActivePage().getActiveEditor();
		
		Blog blog = model.getBlog();
		blog.removeAllEntries();
		Object[] elements = editor.getViewer().getCheckedElements();
		for (Object element : elements) {
			blog.addEntry((Entry) element);
		}
		DinoAgeExportDlg dlg = new DinoAgeExportDlg(window.getShell(), model);
		dlg.open();
		return null;
	}
}
