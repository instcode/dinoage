/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo.handler;

import org.ddth.http.core.Session;
import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.handler.ContentHandler;

public class GuestbookContentHandler implements ContentHandler {
	private static final String NEXT_PREVIOUS_URL_XPATH = "DIV[2]/DIV/DIV[2]/SPAN[2]/SPAN";
	private Session session;
	
	public GuestbookContentHandler(Session session) {
		this.session = session;
	}

	@Override
	public Content<?> handle(Content<?> content) {
		//session.getState().save(new ByteArrayInputStream(buffer), Persistence.GUESTBOOK, "0");
		return null;
	}
}
