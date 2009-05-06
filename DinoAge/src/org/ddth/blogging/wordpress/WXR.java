/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 3:35:11 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.wordpress;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.ddth.blogging.Author;
import org.ddth.blogging.BlogEntry;
import org.ddth.blogging.BlogPost;

/**
 * @author khoa.nguyen
 *
 */
public class WXR {

	private Writer writer;
	
	public WXR() {
		this(new PrintWriter(new OutputStreamWriter(System.out)));
	}
	
	public WXR(Writer writer) {
		this.writer = writer;
	}
	
	public void start() throws Exception {
		VelocityEngine ve = new VelocityEngine();
		ve.init();
		InputStreamReader reader = new InputStreamReader(WXR.class.getResourceAsStream("wordpress.xml"));
		VelocityContext context = new VelocityContext();
		
		Map<String, Object> blog = new HashMap<String, Object>();
		blog.put("title", "instcode's blog");
		blog.put("url", "http://instcode.wordpress.com");
		blog.put("description", "Welcome to my blog :D");
		blog.put("author", new Author("instcode", "http://instcode.wordpress.com",
				"http://en.gravatar.com/userimage/2251513/e5a20d2422b54b45befee4a92ccd4ae5.jpg"));
		context.put("blog", blog);
		context.put("tags", new String[] {"computer", "programming", "thought"});
		context.put("entries", new BlogEntry[] {
				new BlogEntry(new BlogPost("Blog ne", "Noi dung ne", "computer")),
				new BlogEntry(new BlogPost("Blog khac ne", "Noi dung khac ne", "thought")) });
		ve.evaluate(context, writer, "wordpress.xml", reader);
		writer.flush();
	}
	
	public static void main(String[] args) throws Exception {
		WXR generator = new WXR();
		generator.start();
	}

}
