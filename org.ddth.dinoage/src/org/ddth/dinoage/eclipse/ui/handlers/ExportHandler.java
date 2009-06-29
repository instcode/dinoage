package org.ddth.dinoage.eclipse.ui.handlers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.ddth.blogging.Author;
import org.ddth.blogging.Blog;
import org.ddth.blogging.Entry;
import org.ddth.blogging.wordpress.WXR;
import org.ddth.dinoage.eclipse.ui.editors.ProfileEditor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExportHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		ProfileEditor editor = (ProfileEditor)window.getActivePage().getActiveEditor();
		FileDialog dialog = new FileDialog(window.getShell(), SWT.SAVE);
		dialog.setFileName("wp.xml");
		String fileSelection = dialog.open();
		if (fileSelection != null) {
			Blog blog = new Blog();
			blog.setTitle("instcode's blog");
			blog.setUrl("http://instcode.wordpress.com");
			blog.setDescription("Welcome to my blog :D");
			Author author = new Author("instcode", "instcode", "http://instcode.wordpress.com",
					"http://en.gravatar.com/userimage/2251513/e5a20d2422b54b45befee4a92ccd4ae5.jpg");
			blog.addAuthor(author);
			Object[] elements = editor.getViewer().getCheckedElements();
			for (Object element : elements) {
				blog.addEntry((Entry) element);
			}
			OutputStreamWriter outputStreamWriter = null;
			try {
				outputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileSelection), "utf-8");
				WXR.export(blog, outputStreamWriter);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (outputStreamWriter != null) {
					try {
						outputStreamWriter.close();
					}
					catch (IOException e) {
					}
				}
			}
		}
		return null;
	}
}
