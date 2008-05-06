package org.ddth.grabber.core.connection;

import org.ddth.grabber.core.handler.Processor;

public interface Request {
	/**
	 * @return
	 */
	public String getURL();
	
	/**
	 * @return
	 */
	public Processor getProcessor();
}
