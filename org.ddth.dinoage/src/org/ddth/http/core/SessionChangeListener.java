package org.ddth.http.core;

public interface SessionChangeListener {

	/**
	 * Invoked when the session changed its state.
	 * 
	 * @param event
	 */
	public void sessionChanged(SessionChangeEvent event);
}
