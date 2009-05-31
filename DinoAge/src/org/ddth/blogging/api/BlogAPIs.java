/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 3:35:49 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.api;

import java.util.HashMap;
import java.util.Map;

import org.ddth.blogging.google.BloggerAPI;
import org.ddth.blogging.wordpress.WordpressBlogAPI;

public class BlogAPIs {
	public static final String BLOG_TYPE_BLOGGER = "blogger";
	public static final String BLOG_TYPE_WORDPRESS = "wordpress";
	public static final String BLOG_TYPE_OPERA = "opera";
	public static final String BLOG_TYPE_FACEBOOK = "facebook";
	
	private static BlogAPIs instance;
	private Map<String, Class<?>> blogs = new HashMap<String, Class<?>>();

	public static BlogAPIs getInstance() {
		if (instance == null) {
			instance = new BlogAPIs();
			instance.registerBlogAPI(BLOG_TYPE_WORDPRESS, WordpressBlogAPI.class);
			instance.registerBlogAPI(BLOG_TYPE_BLOGGER, BloggerAPI.class);
		}
		return instance;
	}
	
	public void registerBlogAPI(String blogType, Class<?> clazz) {
		blogs.put(blogType, clazz);
	}
	
	public BlogAPI createBlogAPI(String blogType) {
		try {
			Class<?> clazz = blogs.get(blogType);
			if (clazz != null) {
				return (BlogAPI) clazz.newInstance();
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
