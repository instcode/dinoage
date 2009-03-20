package org.ddth.http.core.connection;

import org.ddth.http.core.content.Content;

public class Response {

	private Content<?> content;
	
	public Response(Content<?> content) {
		this.content = content;
	}
	
	public Content<?> getContent() {
		return content;
	}
}
