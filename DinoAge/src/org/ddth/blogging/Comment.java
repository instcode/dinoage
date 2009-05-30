/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 2:14:06 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging;

public class Comment extends Post {
	private long commentId;
	
	public Comment(Author author, String content) {
		setAuthor(author);
		setContent(content);
	}

	public long getCommentId() {
		return commentId;
	}
	
	public void setCommentId(long commentId) {
		this.commentId = commentId;
	}
	
	@Override
	public String toString() {
		return getAuthor() + " " + getContent();
	}
}
