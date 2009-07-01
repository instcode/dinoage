/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 1, 2008 8:23:13 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.yahoo.grabber;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.ddth.blogging.Blog;
import org.ddth.blogging.Entry;
import org.ddth.blogging.yahoo.YahooBlogEntry;
import org.ddth.dinoage.core.DataLoadMonitor;
import org.ddth.dinoage.core.Persistence;
import org.ddth.dinoage.data.DataManager;

public class YahooPersistence extends Persistence {
	public static final String SUFFIX_LIST = "list";
	public static final String[] CATEGORIES = {"entry"};
	public static final String DATABASE = "database";
	public static final int BLOG_ENTRY = 0;
	
	private DataManager manager;
	
	public YahooPersistence(File profileFolder, DataLoadMonitor monitor) {
		super(profileFolder, CATEGORIES);
		manager = new DataManager(new YRawDataProvider(profileFolder, monitor));
	}
	
	public Blog load(String blogId) {
		Blog blog = manager.getBlog(blogId);
		if (blog != null) {
			List<Entry> entries = manager.getEntries(blogId);
			blog.setEntries(entries);
		}
		return blog;
	}

	public boolean save(YBlogContent blogContent) {
		write(blogContent.getContent().getContent(), BLOG_ENTRY, SUFFIX_LIST);
		return true;
	}

	public boolean save(YBlogEntryContent blogEntry) {
		YahooBlogEntry entry = blogEntry.getEntry();
		write(blogEntry.getContent().getContent(), BLOG_ENTRY,
				String.valueOf(entry.getPost().getPostId()));
		return true;
	}

	public void save(String imageName, InputStream inputStream) {
		write(inputStream, new File(getFolder(BLOG_ENTRY), imageName));
	}

	public File getResource(Map<String, String> parameters) {
		String postId = parameters.get("p");
		String blogId = parameters.get("id");
		String low = parameters.get("l");
		String max = parameters.get("mx");
		String fragment = parameters.get("fragment");

		File resource = null;
		if (blogId != null && postId != null) {
			resource = getFile(BLOG_ENTRY, "hires_" + postId + ".jpg");
		}
		else if (fragment != null && fragment.startsWith("lores_")) {
			resource = getFile(BLOG_ENTRY, fragment);
		}
		else {
			int index = 0;
			String entryId = postId;
			// Multi-pages detection
			if (low != null) {
				if (postId == null) {
					// 10 guestbook comments per page
					index = (Integer.parseInt(max) - Integer.parseInt(low) + 1) / 10;
					entryId = "0";
				}
				else {
					// 50 entry comments per page
					index = Integer.parseInt(low) / 50;
				}
			}
			
			String tail = (index == 0) ? "" : "-" + String.valueOf(index);
			String filename = CATEGORIES[BLOG_ENTRY] + "-" + entryId + tail + ".html";
			resource = getFile(BLOG_ENTRY, filename);
		}

		return resource.exists() ? resource : null;
	}

	public void clean(String beginningURL) {
		if (beginningURL.lastIndexOf('?') == -1) {
			if (beginningURL.indexOf("/guestbook-") > 0) {
				getFile(BLOG_ENTRY, "entry-0.html").delete();
			}
			else if (beginningURL.indexOf("/blog-") > 0) {
				getFile(BLOG_ENTRY, "entry-list.html").delete();
			}
		}
	}
}
