/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core.content.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.Response;
import org.ddth.http.core.content.Content;

/**
 * Normally, after making a request, we will wait for the response and will
 * handle it by the time it comes. Because the response for each request
 * contains a specific content in it, for example: if we request for the
 * entrance of a blog, we will get only a list of blog posts, but if we send a
 * request for a blog entry, we will see the post and all the comments.. That's
 * why {@link ContentHanlder} & {@link ContentHandlerDispatcher} were born. The
 * dispatcher has a list of handlers, and when it knows a content handler can
 * process the output of this request, it then gives the content to that handler
 * without caring about how to analyze the content inside...<br>
 * <br>
 * 
 * @author khoa.nguyen
 * 
 */
public class ContentHandlerDispatcher {
	/**
	 * Internally used. Kept in a map.
	 * 
	 * @author khoa.nguyen
	 *
	 */
	private class ContentHandlerEntry {
		Pattern pattern;
		ContentHandler handler;
	}
	
	/**
	 * List of all handlers.
	 */
	private List<ContentHandlerEntry> handlers = new ArrayList<ContentHandlerEntry>();

	/**
	 * The content will be dispatched to a proper handler by testing if its URL
	 * pattern is matched with the given request.<br>
	 * <br>
	 * The dispatcher will iterate through all the handlers it has and select
	 * the a suitable one for handling the response. Because the handlers are
	 * stored in ordered state, if more than one handlers support parsing the
	 * content of the given request, only the <b>FIRST</b> one in the loop will
	 * be chose. So, be careful with the order of your handlers.<br>
	 * <br>
	 * 
	 * @param request
	 *            The request to be checked.
	 * @param response
	 *            The response to be used.
	 * @return The processed content.
	 */
	public Content<?> handle(Request request, Response response) {
		Content<?> content = null;
		for (ContentHandlerEntry entry : handlers) {
			Matcher matcher = entry.pattern.matcher(request.getURL());
			if (matcher.find()) {
				content = entry.handler.handle(response.getContent());
				break;
			}
		}
		return content;
	}
	
	/**
	 * Register a handler to the dispatcher.<br>
	 * <br>
	 * 
	 * @param path
	 *            A regular expression URL which is used to determine which
	 *            handler has responsibility for specific contents in that URL.
	 * @param handler
	 *            The handler for this kind of path.
	 */
	public void registerHandler(String path, ContentHandler handler) {
		ContentHandlerEntry entry = new ContentHandlerEntry();
		entry.pattern = Pattern.compile(path);
		entry.handler = handler;
		handlers.add(entry);
	}
	
	/**
	 * Clean up all the handlers.
	 */
	public void clear() {
		handlers.clear();
	}
}
