/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 3:35:49 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging;

import java.util.HashMap;
import java.util.Map;

import org.ddth.blogging.google.Blogger;
import org.ddth.blogging.wordpress.WordpressBlog;

public class BlogProvider {
	public static final String BLOG_TYPE_BLOGGER = "blogger";
	public static final String BLOG_TYPE_WORDPRESS = "wordpress";
	
	private static BlogProvider instance;
	private Map<String, Class<?>> blogs = new HashMap<String, Class<?>>();

	public static BlogProvider getInstance() {
		if (instance == null) {
			instance = new BlogProvider();
			instance.registerBlog(BLOG_TYPE_WORDPRESS, WordpressBlog.class);
			instance.registerBlog(BLOG_TYPE_BLOGGER, Blogger.class);
		}
		return instance;
	}
	
	public void registerBlog(String blogType, Class<?> clazz) {
		blogs.put(blogType, clazz);
	}
	
	public Blog createBlog(String blogType) {
		try {
			Class<?> clazz = blogs.get(blogType);
			if (clazz != null) {
				return (Blog) clazz.newInstance();
			}
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
