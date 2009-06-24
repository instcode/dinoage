package org.ddth.dinoage.eclipse.ui.editors;

import java.util.Date;
import java.util.List;

import org.ddth.blogging.Author;
import org.ddth.blogging.Blog;
import org.ddth.blogging.BlogPost;
import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;

public class ProfileContentProvider {

	Blog blog = new Blog();
	
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
		
		blog.addEntry(blogEntry);
	}
	
	public List<Entry> getEntries() {
		return blog.getEntries();
	}

}
