package org.ddth.http.core.content.handler;

import org.ddth.http.core.content.Content;

public class DefaultHandler implements ContentHandler {

	public Content<?> handle(Content<?> content) {
		return content;
	}

}
