/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 2:14:06 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.api;

public abstract class BasicBlogAPI implements BlogAPI {

	private String author;
	private String password;
	private String blogURL;

	public String getAuthor() {
		return author;
	}

	public String getPassword() {
		return password;
	}

	public String getBlogURL() {
		return blogURL;
	}

	public void setup(String blogURL, String author, String password) {
		this.blogURL = blogURL;
		this.author = author;
		this.password = password;
	}
}
