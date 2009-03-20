package org.ddth.http.impl.content.handler;

import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.handler.ContentHandler;

public class AdvancedContentHandler implements ContentHandler {

	@Override
	public Content<?> handle(Content<?> content) {
		return content;
	}
}
