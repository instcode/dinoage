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

	public boolean createEntry(BlogEntry entry) {
		return true;
	}

	public boolean createComment(BlogComment comment) {
		return false;
	}
}
