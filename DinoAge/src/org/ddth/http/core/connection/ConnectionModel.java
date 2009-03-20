/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core.connection;


public interface ConnectionModel {

	/**
	 * @param request
	 * @return
	 */
	public RequestFuture sendRequest(Request request);

	/**
	 * 
	 */
	public void open();
	
	/**
	 * @return
	 */
	public boolean running();
	
	/**
	 * 
	 */
	public void close();

}