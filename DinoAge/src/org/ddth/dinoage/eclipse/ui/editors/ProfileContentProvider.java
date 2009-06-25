package org.ddth.dinoage.eclipse.ui.editors;

import org.ddth.blogging.Blog;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class ProfileContentProvider implements ILazyContentProvider {
	private TableViewer viewer;
	private Blog blog = Blog.createBlog();
	
	public ProfileContentProvider() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		this.viewer = (TableViewer) viewer;
		this.viewer.setItemCount(blog.getEntries().size());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILazyContentProvider#updateElement(int)
	 */
	public void updateElement(int index) {
		viewer.replace(blog.getEntries().get(index), index);
	}
}
