/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 2:14:06 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Entry {
	private long entryId;
	private BlogPost post;
	private List<Comment> comments = new CopyOnWriteArrayList<Comment>();
	private String url;
	
	public Entry(BlogPost post) {
		this.post = post;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public BlogPost getPost() {
		return post;
	}

	public long getEntryId() {
		return entryId;
	}
	
	public void setEntryId(long entryId) {
		this.entryId = entryId;
	}
	
	public List<Comment> getComments() {
		return comments;
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
	}
	
	public void removeComment(Comment comment) {
		comments.remove(comment);
	}
}
