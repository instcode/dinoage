package org.ddth.http.core;

import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.Response;

public class ConnectionEvent {

	private Request request;
	private Response response;
	
	public ConnectionEvent(Request request, Response response) {
		this.request = request;
		this.response = response;
	}
	
	public ConnectionEvent(Request request) {
		this(request, null);
	}
	
	public Request getRequest() {
		return request;
	}
	
	public Response getResponse() {
		return response;
	}
}
