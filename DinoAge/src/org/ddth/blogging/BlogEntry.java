/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 2:14:06 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging;

import java.util.ArrayList;
import java.util.List;

public class BlogEntry {
	private BlogPost post;
	private List<BlogComment> comments = new ArrayList<BlogComment>();
	
	public BlogEntry(BlogPost post) {
		this.post = post;
	}

	public BlogPost getPost() {
		return post;
	}

	public List<BlogComment> getComments() {
		return comments;
	}
	
	public void addComment(BlogComment comment) {
		comments.add(comment);
	}
	
	public void removeComment(BlogComment comment) {
		comments.remove(comment);
	}
}
