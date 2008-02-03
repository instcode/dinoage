package org.ddth.grabber.core.connection;

import org.ddth.grabber.core.handler.Processor;

public interface Request {
	public String getURL();
	public Processor getProcessor();
}
