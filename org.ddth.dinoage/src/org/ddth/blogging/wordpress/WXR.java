/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 3:35:11 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.wordpress;

import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.ddth.blogging.Author;
import org.ddth.blogging.Blog;
import org.ddth.blogging.BlogPost;
import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;
import org.ddth.blogging.api.BlogUtil;
import org.ddth.dinoage.data.DataManager;

/**
 * @author khoa.nguyen
 *
 */
public class WXR {
	
	public static void export(Blog blog) throws Exception {
		export(blog, new PrintWriter(new OutputStreamWriter(System.out)));
	}
	
	public static void export(Blog blog, Writer writer) throws Exception {
		VelocityEngine ve = new VelocityEngine();
		ve.init();
		InputStreamReader reader = new InputStreamReader(WXR.class.getResourceAsStream("wordpress.xml"));
		VelocityContext context = new VelocityContext();
		
		BlogUtil.rebuild(blog);
		context.put("util", new BlogUtil());
		context.put("blog", blog);
		ve.evaluate(context, writer, "wordpress.xml", reader);
		writer.flush();
	}

	public static void main(String[] args) throws Exception {
		export(Blog.createBlog());
		//export(Blog.createBlog(), new OutputStreamWriter(new FileOutputStream("wp.xml"), "utf-8"));
		//database();
	}

	static void database() {
		File file = new File("D:\\Projects\\DinoAge\\workspaces\\w1\\watersnake");
		File databaseFolder = new File(file, "database");

		Blog blog = new Blog();
		blog.setBlogId("yqp68HY_fL5vjbhcS9.elKU-");
		blog.setTitle("instcode's blog");
		blog.setUrl("http://instcode.wordpress.com");
		blog.setDescription("Welcome to my blog :D");
		Author author = new Author("instcode", "instcode", "http://instcode.wordpress.com",
				"http://en.gravatar.com/userimage/2251513/e5a20d2422b54b45befee4a92ccd4ae5.jpg");
		
		if (databaseFolder.exists()) {
			DataManager manager = new DataManager(databaseFolder);
			List<Entry> entries = manager.getEntries(blog.getBlogId());
			for (Entry entry : entries) {
				System.out.println(entry.getPost().getContent());
				List<Comment> comments = manager.getComments(entry.getEntryId());
				System.out.println("Comment:");
				for (Comment comment : comments) {
					System.out.println(comment);
				}
			}
			return;
		}
		else {
			DataManager manager = new DataManager(databaseFolder);
			manager.createAuthor(author);
			
			blog.addAuthor(author);
			manager.createBlog(blog);
			
			author = new Author("hello", "Test", "http://yahoo.com", "http://avatar.com/avatar.png");
			manager.createAuthor(author);
			
			BlogPost blogPost = new BlogPost();
			blogPost.setAuthor(author);
			blogPost.setTitle("Đây là tiêu đề");
			blogPost.setContent("Đừng xa em đêm nay khi má em đã ngủ say");
			blogPost.setTags("computer, misc, hello, tagne");
			blogPost.setPostId(12);
			blogPost.setDate(new Date());
			Entry entry = new Entry(blogPost);
			
			manager.createEntry(blog.getBlogId(), entry);
			
			entry.addComment(new Comment(author, "Comment ne 1", new Date(3434343L)));
			entry.addComment(new Comment(author, "Comment ne 2", new Date(3434343L)));
			entry.addComment(new Comment(author, "Comment ne 3", new Date(3434343L)));
			
			for (Comment comment : entry.getComments()) {
				manager.createComment(entry.getEntryId(), comment);
			}
		}
	}
}
