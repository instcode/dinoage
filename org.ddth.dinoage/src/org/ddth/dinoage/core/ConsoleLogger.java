package org.ddth.dinoage.core;


/**
 * Provider a simple interface which connects to
 * an external logger.  
 * 
 * @author khoa.nguyen
 *
 */
public interface ConsoleLogger {
	public static final ConsoleLogger DEFAULT_CONSOLE_LOGGER = new ConsoleLogger() {
		@Override
		public void println(String message) {
		}
	};
	
	/**
	 * Appends the specified message to this logger, followed by a line
	 * separator string.
	 * 
	 * @param message message to print
	 */
	public void println(String message);
}
