/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 1, 2008 8:23:13 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.yahoo.grabber;

import java.io.File;
import java.util.List;

import org.ddth.blogging.Blog;
import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;
import org.ddth.blogging.yahoo.YahooBlog;
import org.ddth.blogging.yahoo.YahooBlogEntry;
import org.ddth.dinoage.core.DataLoadMonitor;
import org.ddth.dinoage.core.Persistence;
import org.ddth.dinoage.data.DataManager;

public class YahooPersistence extends Persistence {
	public static final String SUFFIX_LIST = "list";
	public static final String[] CATEGORIES = {"entry", "guestbook", "database"};
	public static final int BLOG_ENTRY = 0;
	public static final int BLOG_GUESTBOOK = 1;
	public static final int BLOG_DATABASE = 2;
	
	private DataManager manager;
	private DataManager manager2;
	
	public YahooPersistence(File profileFile, DataLoadMonitor monitor) {
		super(profileFile.getParentFile(), CATEGORIES);
		manager = new DataManager(getFolder(BLOG_DATABASE).getAbsolutePath());
		manager2 = new DataManager(new YRawDataProvider(profileFile.getParentFile(), monitor));
	}
	
	public Blog load(String profileId) {
		Blog blog = manager2.getBlog(profileId);
		List<Entry> entries = manager2.getEntries(profileId);
		blog.setEntries(entries);
		return blog;
	}
	
	public void save(YBlogGuestbookContent guestbookContent) {
		write(guestbookContent.getContent().getContent(), BLOG_ENTRY, CATEGORIES[BLOG_GUESTBOOK]);
	}
	
	public boolean save(YBlogContent blogContent) {
		write(blogContent.getContent().getContent(), BLOG_ENTRY, SUFFIX_LIST);
		YahooBlog blog = blogContent.getBlog();
		if (blog != null) {
			manager.createAuthor(blog.getAuthor());
			manager.createBlog(blog);
		}
		return true;
	}

	public boolean save(YBlogEntryContent blogEntry) {
		YahooBlogEntry entry = blogEntry.getEntry();
		write(blogEntry.getContent().getContent(), BLOG_ENTRY,
				String.valueOf(entry.getPost().getPostId()));
		manager.createEntry("blog.getBlogId()", entry);
		for (Comment comment : entry.getComments()) {
			manager.createComment(entry.getEntryId(), comment);
		}
		return true;
	}
}
