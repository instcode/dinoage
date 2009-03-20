/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 1, 2008 7:45:55 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core.content;



public class ContentAdapter<T> implements Content<T> {

	private T content;
	
	@Override
	public void setContent(T content) {
		this.content = content;
	}
	
	@Override
	public T getContent() {
		return content;
	}
}
