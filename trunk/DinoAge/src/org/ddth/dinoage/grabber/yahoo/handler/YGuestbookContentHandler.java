/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo.handler;

import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.handler.ContentHandler;

public class YGuestbookContentHandler implements ContentHandler {

	@Override
	public Content<?> handle(Content<?> content) {
		//session.getState().save(new ByteArrayInputStream(buffer), Persistence.GUESTBOOK, "0");
		return null;
	}
}
