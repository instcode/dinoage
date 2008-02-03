/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 6, 2008 12:24:03 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo;

import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.grabber.yahoo.handler.BlogEntryProcessor;
import org.ddth.dinoage.grabber.yahoo.handler.EntryListProcessor;
import org.ddth.dinoage.grabber.yahoo.handler.GuestbookProcessor;
import org.ddth.grabber.core.connection.Request;
import org.ddth.grabber.core.connection.RequestFactory;
import org.ddth.grabber.core.connection.Session;
import org.ddth.grabber.core.handler.Processor;
import org.ddth.grabber.impl.connection.RequestImpl;

public class YahooRequestFactory implements RequestFactory<YBackupState> {

	public Request createRequest(String link, Session<YBackupState> session) {
		if (link == null || link.length() == 0) {
			return null;
		}
		Processor processor = null;
		if (link.startsWith(ResourceManager.KEY_BLOG_URL)) {
			if (link.endsWith(ResourceManager.KEY_BLOG_LIST_PARAMETER_VALUE)) {
				processor = new EntryListProcessor(session);
			}
			else {
				processor = new BlogEntryProcessor(session);
			}
		}
		else if (link.startsWith(ResourceManager.KEY_GUESTBOOK_URL)) {
			processor = new GuestbookProcessor(session);
		}
		if (processor != null) {
			return new RequestImpl(link, processor);
		}
		return null;
	}
}
