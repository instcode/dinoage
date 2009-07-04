package org.ddth.dinoage.core;

import java.io.File;

import org.ddth.http.core.connection.Request;
import org.ddth.http.core.content.Content;

public interface LocalStorage {

	/**
	 * Set local resource of a request has been expired. The caller
	 * should go online and retrieve new content.
	 */
	public static final String RESOURCE_EXPIRED_ATTR = "__expired__";
	
	/**
	 * Set request's result must not be cached locally.  
	 */
	public static final String NO_CACHING_ATTR = "__no_cache__";
	
	/**
	 * Check if the result of current request is available locally.
	 * 
	 * @param request
	 * @return a local file
	 */
	public File getLocalResource(Request request);
	
	/**
	 * Save to resource to external memory (hard-disk). If the file
	 * exists, it will be overwritten.
	 * 
	 * @param request
	 * @param content
	 */
	public void cacheResource(Request request, Content<?> content);
}
