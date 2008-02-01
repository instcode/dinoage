/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 6, 2008 12:24:03 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo;

import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.grabber.yahoo.handler.BlogEntryNavigationHandler;
import org.ddth.dinoage.grabber.yahoo.handler.EntryListNavigationHandler;
import org.ddth.dinoage.grabber.yahoo.handler.GuestbookNavigationHandler;
import org.ddth.dinoage.model.Persistence;
import org.ddth.grabber.core.connection.Session;
import org.ddth.grabber.core.handler.NavigationHandler;

public class ContentHandlerFactory {
	private static ContentHandlerFactory instance = new ContentHandlerFactory();

	public static ContentHandlerFactory getInstance() {
		return instance;
	}
	
	public NavigationHandler createContentHandler(String sURL, Persistence persistence, Session session) {
		if (sURL == null || sURL.length() == 0) {
			return null;
		}
		if (sURL.startsWith(ResourceManager.KEY_BLOG_URL)) {
			if (sURL.endsWith(ResourceManager.KEY_BLOG_LIST_PARAMETER_VALUE)) {
				return new EntryListNavigationHandler(persistence, session);
			}
			return new BlogEntryNavigationHandler(persistence, session);
		}
		if (sURL.startsWith(ResourceManager.KEY_GUESTBOOK_URL)) {
			return new GuestbookNavigationHandler(persistence, session);
		}
		return null;
	}
}
