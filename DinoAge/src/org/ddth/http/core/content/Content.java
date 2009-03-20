package org.ddth.http.core.content;

public interface Content<T> {

	/**
	 * @param content
	 */
	public void setContent(T content);

	/**
	 * @return
	 */
	public T getContent();

}