package org.ddth.dinoage.core;

import org.ddth.http.core.connection.Request;

public interface RequestStorage {

	public Request[] getRequests();
	
	public void putRequest(Request request);
}
