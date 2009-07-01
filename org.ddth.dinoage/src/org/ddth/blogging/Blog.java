/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 2:14:06 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Blog {

	private String blogId;
	private List<Author> authors = new ArrayList<Author>();
	private String title;
	private String url;
	private String description;
	private Map<Long, Entry> entries = new ConcurrentHashMap<Long, Entry>();

	public String getBlogId() {
		return blogId;
	}
	
	public void setBlogId(String blogId) {
		this.blogId = blogId;
	}
	
	public List<Author> getAuthors() {
		return authors;
	}
	
	public Author getAuthor() {
		return authors.size() > 0 ? authors.get(0) : null;
	}

	public void addAuthor(Author author) {
		authors.add(author);
	}
	
	public boolean removeAuthor(Author author) {
		return authors.remove(author);
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<Entry> getEntries() {
		return entries.values();
	}

	public void setEntries(List<Entry> entries) {
		for (Entry entry : entries) {
			addEntry(entry);
		}
	}
	
	public boolean addEntry(Entry entry) {
		Long entryId = new Long(entry.getEntryId());
		Entry existence = entries.get(entryId);
		if (existence != null) {
			existence.getComments().addAll(entry.getComments());
		}
		else {
			entries.put(entryId, entry);
		}
		return (existence == null);
	}

	public void removeAllEntries() {
		entries.clear();
	}
	
	public static final Blog createBlog() {
		Blog blog = new Blog();
		
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
		return blog;
	}
}
