/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 2:14:06 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging;

import java.util.Date;

public class BlogPost {
	private String title;
	private String content;
	private String tags;
	private Date date;
	private String postId;

	public BlogPost(String title, String content, String tags) {
		setTitle(title);
		setContent(content);
		setTags(tags);
	}
	
	public BlogPost(String title, String content) {
		this(title, content, "");
	}
	
	public BlogPost() {
		this("", "");
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getPostId() {
		return postId;
	}
}
