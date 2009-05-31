/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo.handler;

import org.ddth.blogging.yahoo.YahooBlogEntryUtil;
import org.ddth.dinoage.grabber.yahoo.YBlogContent;
import org.ddth.http.core.content.Content;
import org.ddth.http.impl.content.DomTreeContent;
import org.w3c.dom.Document;

public class YEntryListContentHandler extends YahooBlogContentHandler {

	@Override
	public Content<?> handle(Content<?> content) {
		DomTreeContent domTreeContent = (DomTreeContent) super.handle(content);
		Document doc = domTreeContent.getDocument();
		String firstEntryURL = YahooBlogEntryUtil.parseNavigationLink(doc);
		YBlogContent yahooBlogContent = new YBlogContent(new String[] { firstEntryURL }, null);
		yahooBlogContent.setContent(domTreeContent);
		return yahooBlogContent;
	}
}
