package org.ddth.http;

import org.ddth.http.core.Session;
import org.ddth.http.core.connection.Request;
import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.handler.ChainContentHandler;
import org.ddth.http.core.content.handler.ContentHandlerDispatcher;
import org.ddth.http.impl.ThreadPoolSession;
import org.ddth.http.impl.content.handler.WebpageContentHandler;

public class Grabber {

	private static ContentHandlerDispatcher createDispatcher() {
		ChainContentHandler handler = new ChainContentHandler();
		handler.add(new WebpageContentHandler());
		
		ContentHandlerDispatcher dispatcher = new ContentHandlerDispatcher();
		dispatcher.registerHandler(".*", handler);
		
		return dispatcher;
	}
	
	public static void main(String[] args) {
		demo();
	}

	private static void demo() {
		ContentHandlerDispatcher dispatcher = createDispatcher();
		
		Session session = new ThreadPoolSession(dispatcher) {
			@Override
			protected void content(Content<?> content) {
				System.out.println("Handle the given content goes here!");
			}
		};
		session.start();
		
		session.queue(new Request("http://instcode.wordpress.com"));
		session.queue(new Request("http://instcode.blogspot.com"));
		session.queue(new Request("http://360.yahoo.com/nullpointer82"));
		
		session.shutdown();
	}
}
