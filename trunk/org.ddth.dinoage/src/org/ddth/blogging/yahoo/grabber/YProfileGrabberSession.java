package org.ddth.blogging.yahoo.grabber;

import org.ddth.blogging.Author;
import org.ddth.blogging.yahoo.YahooBlogAPI;
import org.ddth.dinoage.core.ProfileGrabberSession;
import org.ddth.http.core.connection.Request;
import org.ddth.http.core.content.Content;

public class YProfileGrabberSession extends ProfileGrabberSession {

	private Author author;
	
	public YProfileGrabberSession() {
		super(YahooBlogAPI.YAHOO_360_PROFILE_CONTENT_DISPATCHER);
	}

	public Author getAuthor() {
		return author;
	}
	
	@Override
	protected void handle(Request request, Content<?> content) {
		author = ((YAuthorContent)content).getAuthor();
	}
}