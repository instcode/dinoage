/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.yahoo.grabber.handler;

import org.ddth.blogging.Author;
import org.ddth.blogging.yahoo.YahooBlogUtil;
import org.ddth.blogging.yahoo.grabber.YAuthorContent;
import org.ddth.http.core.content.Content;
import org.ddth.http.impl.content.DomTreeContent;
import org.w3c.dom.Document;

public class YProfilePageContentHandler extends YahooBlogContentHandler {

	@Override
	public Content<?> handle(Content<?> content) {
		DomTreeContent domTreeContent = (DomTreeContent) super.handle(content);
		Document doc = domTreeContent.getDocument();
		Author author = YahooBlogUtil.parseYahooProfile(doc);
		return new YAuthorContent(domTreeContent, author);
	}
}
