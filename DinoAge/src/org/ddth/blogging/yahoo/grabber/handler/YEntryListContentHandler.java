/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.yahoo.grabber.handler;

import org.ddth.blogging.yahoo.YahooBlog;
import org.ddth.blogging.yahoo.YahooBlogUtil;
import org.ddth.blogging.yahoo.grabber.YBlogContent;
import org.ddth.http.core.content.Content;
import org.ddth.http.impl.content.DomTreeContent;
import org.w3c.dom.Document;

public class YEntryListContentHandler extends YahooBlogContentHandler {

	@Override
	public Content<?> handle(Content<?> content) {
		DomTreeContent domTreeContent = (DomTreeContent) super.handle(content);
		Document doc = domTreeContent.getDocument();
		YahooBlog yahooBlog = YahooBlogUtil.parseYahooBlog(doc);
		YBlogContent yahooBlogContent = new YBlogContent(domTreeContent, yahooBlog);
		return yahooBlogContent;
	}
}
