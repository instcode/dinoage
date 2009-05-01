/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core.connection;

import java.util.Map;

/**
 * A request holds all information needed to construct an HTTP request to the
 * server. It contains a URL, a list of parameters (form, request parameters...).
 * However, in the implementation, if it has parameters, the HTTP client will
 * make a POST to the server. That's the reason why you should put all your GET
 * parameters in the {@link #url} itself, not in the {@link #parameters} map.<br>
 * <br>
 * @author khoa.nguyen
 * 
 */
public class Request {
	private String url;
	private Map<String, String> parameters;

	/**
	 * @param url
	 */
	public Request(String url) {
		this.url = url;
	}
	
	/**
	 * Construct a request with the given url and request parameters<br>
	 * <br>
	 * 
	 * @param url
	 *            The url that will be retrieved.
	 * @param parameters
	 *            The request parameters. Any pair of parameters in this input
	 *            will guide the HTTP client to make a POST request to the
	 *            server. Therefore, if you want to send a GET request, please
	 *            put your parameters in the {@link url} itself and remove them
	 *            from {@link parameters}.
	 */
	public Request(String url, Map<String, String> parameters) {
		this(url);
		this.parameters = parameters;
	}

	/**
	 * Get the request URL.
	 * 
	 * @return
	 * 		The url of this request, of course =))
	 */
	public String getURL() {
		return url;
	}
	
	/**
	 * Get the parameters map.<br>
	 * <br>
	 * You have full access to this map. This might violate the encapsulation
	 * rule of OOP.. But I know you need speed, and you are a genius developer,
	 * an you won't make reference to it every where, will you? :D<br>
	 * <br>
	 * 
	 * @return The request parameters map.
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}
}
