package org.ddth.dinoage.eclipse.ui.editors;

import java.util.Date;

import org.ddth.blogging.Author;
import org.ddth.blogging.Blog;
import org.ddth.blogging.BlogPost;
import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class ProfileContentProvider implements ILazyContentProvider {
	private TableViewer viewer;
	private Blog blog = new Blog();
	
	public ProfileContentProvider() {
		
		blog.setTitle("instcode's blog");
		blog.setUrl("http://instcode.wordpress.com");
		blog.setDescription("Welcome to my blog :D");
		Author author = new Author("instcode", "instcode", "http://instcode.wordpress.com",
				"http://en.gravatar.com/userimage/2251513/e5a20d2422b54b45befee4a92ccd4ae5.jpg");
		blog.addAuthor(author);
		
		BlogPost blogPost = new BlogPost();
		blogPost.setAuthor(author);
		blogPost.setTitle("Title goes here");
		blogPost.setContent("Content goes here");
		blogPost.setTags("computer, misc");
		blogPost.setPostId(12);
		blogPost.setDate(new Date());
		
		Comment blogComment = new Comment(new Author("test", "Test", "http://yahoo.com", "http://avatar.com/avatar.png"), "Comment ne", new Date(3434343L));
		
		Entry blogEntry = new Entry(blogPost);
		blogEntry.addComment(blogComment);
		
		for (int i = 0; i < 100; i++) {
			blog.addEntry(blogEntry);
		}
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
