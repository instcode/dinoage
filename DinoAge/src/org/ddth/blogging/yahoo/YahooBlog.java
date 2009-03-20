/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 3:35:11 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.yahoo;

import org.ddth.blogging.BasicBlog;
import org.ddth.blogging.BlogComment;
import org.ddth.blogging.BlogEntry;

public class YahooBlog extends BasicBlog {

	public static final String YAHOO_360_PROFILE_URL = "http://360.yahoo.com/profile-";
	public static final String YAHOO_360_GUESTBOOK_URL = "http://360.yahoo.com/guestbook-";
	public static final String YAHOO_360_BLOG_URL = "http://blog.360.yahoo.com/blog-";

	public boolean createEntry(BlogEntry entry) {
		return true;
	}

	public boolean createComment(BlogComment comment) {
		return false;
	}
}
