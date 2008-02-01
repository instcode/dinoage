/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 2:14:06 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging;

public interface Blog {

	public String getAuthor();
	public String getPassword();
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
	public boolean createEntry(BlogEntry entry);
	
	/**
	 * Create a comment :D
	 * 
	 * @param comment
	 * @return
	 */
	public boolean createComment(BlogComment comment);
}
