/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo.handler;

import java.io.ByteArrayInputStream;

import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.grabber.yahoo.YBackupState;
import org.ddth.dinoage.model.Persistence;
import org.ddth.grabber.core.connection.Session;
import org.ddth.grabber.impl.handler.RegExpProcessor;

public class GuestbookProcessor extends RegExpProcessor {
	private Session<YBackupState> session;
	
	public GuestbookProcessor(Session<YBackupState> session) {
		super(ResourceManager.KEY_ENCODING, new String[] {
					"DIV[2]/DIV/DIV[2]/SPAN[2]/SPAN"
				},
				PATTERN_TYPE_INCLUSIVE_LINK,
    			new String[] { ".*" }
        );
		
		this.session = session;
	}


	@Override
	protected void handleContent(byte[] buffer) {
		session.getState().write(new ByteArrayInputStream(buffer), Persistence.GUESTBOOK);
	}
	
	@Override
	protected void handleLink(String link) {
		session.queueRequest(link);
	}
}
