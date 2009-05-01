/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core.content.handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.ContentAdapter;

/**
 * This handler is used to hold a chain of {@link ContentHandler} objects for
 * stacking the content processing. Think about any content is handled by this
 * handler has to go through a list of handlers, and each handler will refine
 * the content a little bit... It is like a filter chain which applies a small
 * set of rules to the object called "token". That's why the order of each
 * handler is very important.<br>
 * <br>
 * This implementation uses *Composite* design patterns. Do you want me to
 * explain this design patterns like I did in the {@link ContentAdapter}? =))<br>
 * <br>
 * 
 * @author khoa.nguyen
 * 
 */
public class ChainContentHandler implements ContentHandler {

	/**
	 * The chain of handlers. The order of each handler in this list is very
	 * important.
	 */
	private List<ContentHandler> handlers = new CopyOnWriteArrayList<ContentHandler>();

	@Override
	public Content<?> handle(Content<?> content) {
		Content<?> token = content;
		for (ContentHandler handler : handlers) {
			token = handler.handle(token);
		}
		return token;
	}

	/**
	 * Add the handler to the chain of handlers.
	 * 
	 * @param handler
	 *            The handler to be added.
	 */
	public void add(ContentHandler handler) {
		handlers.add(handler);
	}

	/**
	 * Remove the specific handler.
	 * 
	 * @param handler
	 *            The handler to be removed.
	 * @return true if the removal is successfully (the handler is in the list).
	 */
	public boolean remove(ContentHandler handler) {
		return handlers.remove(handler);
	}

	@Override
	public ChainContentHandler clone() {
		ChainContentHandler chainHandler = new ChainContentHandler();
		for (ContentHandler handler : handlers) {
			chainHandler.add(handler);
		}
		return chainHandler;
	}

	/**
	 * Clear all the internal handlers.
	 */
	public void clear() {
		handlers.clear();
	}
}
