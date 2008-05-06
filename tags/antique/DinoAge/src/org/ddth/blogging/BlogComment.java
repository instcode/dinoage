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
	private String author;
	private String content;
	private Date date;
	private long postId;

	public BlogComment(String author, String content) {
		setAuthor(author);
		setContent(content);
	}
	
	public BlogComment(String content) {
		this("", content);
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

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	public void setPostId(long postId) {
		this.postId = postId;
	}

	public long getPostId() {
		return postId;
	}
}
