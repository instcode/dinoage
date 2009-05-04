/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo.handler;

import org.ddth.blogging.yahoo.YahooBlogEntry;
import org.ddth.blogging.yahoo.YahooBlogEntryUtil;
import org.ddth.dinoage.grabber.yahoo.YBlogEntryContent;
import org.ddth.http.core.content.Content;
import org.ddth.http.impl.content.DomTreeContent;
import org.w3c.dom.Document;

public class YBlogEntryContentHandler extends YahooBlogContentHandler {

	@Override
	public Content<?> handle(Content<?> content) {
		DomTreeContent domTreeContent = (DomTreeContent) super.handle(content);
		Document doc = domTreeContent.getDocument();
		YahooBlogEntry entry = YahooBlogEntryUtil.parseEntry(doc);
		YBlogEntryContent blogEntryContent = new YBlogEntryContent(entry);
		blogEntryContent.setContent(domTreeContent);
		return blogEntryContent;
	}
}
