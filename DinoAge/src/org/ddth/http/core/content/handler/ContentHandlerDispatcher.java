package org.ddth.http.core.content.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.Response;

public class ContentHandlerDispatcher {
	private class ContentHandlerEntry {
		Pattern pattern;
		ContentHandler handler;
	}
	
	private Map<String, ContentHandlerEntry> handlers = new HashMap<String, ContentHandlerEntry>();

	public void handle(Request request, Response response) {
		Iterator<ContentHandlerEntry> iterator = handlers.values().iterator();
		while (iterator.hasNext()) {
			ContentHandlerEntry entry = iterator.next();
			Matcher matcher = entry.pattern.matcher(request.getURL());
			if (matcher.find()) {
				entry.handler.handle(response.getContent());
				break;
			}
		}
	}
	
	/**
	 * @param path
	 * @param handler
	 */
	public void registerHandler(String path, ContentHandler handler) {
		ContentHandlerEntry entry = handlers.get(path);
		if (entry == null) {
			entry = new ContentHandlerEntry();
			entry.pattern = Pattern.compile(path);
			handlers.put(path, entry);
		}
		entry.handler = handler;
	}
	
	/**
	 * @param path
	 * @return
	 */
	public ContentHandler unregisterHandler(String path) {
		ContentHandlerEntry entry = handlers.remove(path);
		if (entry != null) {
			return entry.handler;
		}
		return null;
	}
}
