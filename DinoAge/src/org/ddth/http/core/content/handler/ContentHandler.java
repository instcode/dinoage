/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core.content.handler;

import org.ddth.http.core.content.Content;

/**
 * Handle a specific content in a web page.<br>
 * <br>
 * Just implement your own {@link ContentHandler} for handling, extracting
 * information from your own web page.
 * 
 * @author khoa.nguyen
 * 
 */
public interface ContentHandler {

	/**
	 * Handle the given content and produce another useful content.<br>
	 * <br>
	 * 
	 * @param content
	 *            The content to be processed.
	 * @return The new content after processing.
	 */
	public Content<?> handle(Content<?> content);
}
