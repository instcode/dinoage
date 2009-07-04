/****************************************************
 * $Project: DinoAge
 * $Date:: Mar 21, 2008
 * $Revision: 
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.dinoage.data.exception;


/**
 * @author khoa.nguyen
 *
 */
public class QueryDataException extends Exception {

	public QueryDataException(String message, Throwable e) {
		super(message, e);
	}

	public QueryDataException(String message) {
		super(message);
	}
	
	public QueryDataException(Exception e) {
		super(e);
	}

	public QueryDataException() {
	}
}
