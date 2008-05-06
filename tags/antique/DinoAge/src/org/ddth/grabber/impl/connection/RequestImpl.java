/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.grabber.impl.connection;

import org.ddth.grabber.core.connection.Request;
import org.ddth.grabber.core.handler.Processor;

public class RequestImpl implements Request {

	private String link;
	private Processor processor;
	
	public RequestImpl(String link, Processor processor) {
		this.link = link;
		this.processor = processor;
	}
	
	public Processor getProcessor() {
		return processor;
	}

	public String getURL() {
		return link;
	}
}
