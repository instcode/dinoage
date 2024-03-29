/****************************************************
 * $Project: org.ddth.dinoage
 * $Date:: Jun 30, 2009
 * $Revision:
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.dinoage.core;

import org.ddth.blogging.Author;
import org.ddth.http.core.content.handler.ContentHandlerDispatcher;
import org.ddth.http.impl.ThreadPoolSession;

/**
 * @author khoanguyen
 *
 */
public abstract class ProfileGrabberSession extends ThreadPoolSession {

	public ProfileGrabberSession(ContentHandlerDispatcher dispatcher) {
		super(dispatcher);
	}

	public abstract Author getAuthor();

}