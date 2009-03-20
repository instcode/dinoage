/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core.connection;

import java.util.Map;


public class Request {
	private String url;
	private Map<String, String> parameters;

	public Request(String url) {
		this.url = url;
	}
	
	public Request(String link, Map<String, String> parameters) {
		this(link);
		this.parameters = parameters;
	}

	public String getURL() {
		return url;
	}
	
	public Map<String, String> getParameters() {
		return parameters;
	}
}
