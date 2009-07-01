/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 2:14:06 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.api;

import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;

public interface BlogAPI {

	/**
	 * @return
	 */
	public String getAuthor();
	
	/**
	 * @return
	 */
	public String getPassword();
	
	/**
	 * @return
	 */
	public String getBlogURL();
	
	/**
	 * Setup blog account :D
	 * 
	 * @param blogURL
	 * @param author
	 * @param password
	 */
	public void setup(String blogURL, String author, String password);
	
	/**
	 * Create blog entry :D
	 * 
	 * @param entry
	 * @return
	 */
	public boolean createEntry(Entry entry);
	
	/**
	 * Create a comment :D
	 * 
	 * @param comment
	 * @return
	 */
	public boolean createComment(Comment comment);
	
}
