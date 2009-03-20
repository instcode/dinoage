package org.ddth.http.core.content.handler;

import org.ddth.http.core.content.Content;


public interface ContentHandler {
	
	public Content<?> handle(Content<?> content);
}
