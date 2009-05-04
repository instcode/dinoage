/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 2:14:06 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging;

import java.util.Date;

public class BlogComment {
	private Author author;
	private String content;
	private Date date;
	private String postId;

	public BlogComment(Author author, String content) {
		setAuthor(author);
		setContent(content);
	}
	
	public BlogComment(String content) {
		this(null, content);
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Author getAuthor() {
		return author;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getPostId() {
		return postId;
	}
	
	@Override
	public String toString() {
		return author + " " + content;
	}
}
