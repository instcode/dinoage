/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo.handler;

import org.ddth.blogging.BlogEntry;
import org.ddth.blogging.yahoo.YahooBlogEntryUtil;
import org.ddth.dinoage.grabber.yahoo.YBlogEntryContent;
import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.handler.ContentHandler;
import org.ddth.http.impl.content.DomTreeContent;
import org.w3c.dom.Document;

public class YBlogEntryContentHandler implements ContentHandler {

	@Override
	public Content<?> handle(Content<?> content) {
		Document doc = ((DomTreeContent) content).getDocument();
		String nextURL = YahooBlogEntryUtil.parsePreviousBlogEntryLink(doc);
		BlogEntry entry = YahooBlogEntryUtil.parseEntry(doc);
		YBlogEntryContent blogEntryContent = new YBlogEntryContent(entry, nextURL);
		blogEntryContent.setContent((DomTreeContent) content);
		return blogEntryContent;
	}
}
