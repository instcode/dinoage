package org.ddth.http.core.content.handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ddth.http.core.content.Content;


public class ChainContentHandler implements ContentHandler {
	
	private List<ContentHandler> handlers = new CopyOnWriteArrayList<ContentHandler>();

	@Override
	public Content<?> handle(Content<?> content) {
		Content<?> token = content;
		for (ContentHandler handler : handlers) {
			token = handler.handle(token);
		}
		return token;
	}

	public void add(ContentHandler handler) {
		handlers.add(handler);
	}

	public boolean remove(ContentHandler handler) {
		return handlers.remove(handler);
	}

	public void clear() {
		handlers.clear();
	}
}
