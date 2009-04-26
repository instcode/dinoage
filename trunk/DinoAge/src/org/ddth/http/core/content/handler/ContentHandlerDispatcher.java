package org.ddth.http.core.content.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.Response;
import org.ddth.http.core.content.Content;

public class ContentHandlerDispatcher {
	private class ContentHandlerEntry {
		Pattern pattern;
		ContentHandler handler;
	}
	
	private List<ContentHandlerEntry> handlers = new ArrayList<ContentHandlerEntry>();

	public Content<?> handle(Request request, Response response) {
		Content<?> content = null;
		for (ContentHandlerEntry entry : handlers) {
			Matcher matcher = entry.pattern.matcher(request.getURL());
			if (matcher.find()) {
				content = entry.handler.handle(response.getContent());
				break;
			}
		}
		return content;
	}
	
	/**
	 * @param path
	 * @param handler
	 */
	public void registerHandler(String path, ContentHandler handler) {
		ContentHandlerEntry entry = new ContentHandlerEntry();
		entry.pattern = Pattern.compile(path);
		entry.handler = handler;
		handlers.add(entry);
	}
	
	public void clear() {
		handlers.clear();
	}
}
