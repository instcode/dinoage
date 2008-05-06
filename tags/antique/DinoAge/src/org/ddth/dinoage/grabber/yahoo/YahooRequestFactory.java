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
import org.ddth.dinoage.model.Profile;
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
		
		Profile profile = session.getState().getProfile();
		// Blog entry...
		if (profile.isBackupEntry()) {
			if (link.startsWith(ResourceManager.KEY_BLOG_URL)) {
				if (link.endsWith(ResourceManager.KEY_BLOG_LIST_PARAMETER_VALUE)) {
					processor = new EntryListProcessor(session);
				}
				else {
					processor = new BlogEntryProcessor(session);
				}
			}
		}
		// Guest book...
		if (profile.isBackupGuestbook()) {
			if (link.startsWith(ResourceManager.KEY_GUESTBOOK_URL)) {
				processor = new GuestbookProcessor(session);
			}
		}
		return new RequestImpl(link, processor);
	}
}
