/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.yahoo.grabber.handler;

import org.ddth.blogging.yahoo.YahooBlogUtil;
import org.ddth.blogging.yahoo.grabber.YEntryImageContent;
import org.ddth.http.core.content.Content;
import org.ddth.http.impl.content.DomTreeContent;
import org.ddth.http.impl.content.handler.WebpageContentHandler;
import org.w3c.dom.Document;

public class YEntryImageContentHandler extends WebpageContentHandler {

	@Override
	public Content<?> handle(Content<?> content) {
		DomTreeContent domTreeContent = (DomTreeContent) super.handle(content);
		Document doc = domTreeContent.getDocument();
		String hiresImageURL = YahooBlogUtil.parsePopupSlideshowForHiResImage(doc);
		YEntryImageContent entryImageContent = new YEntryImageContent(domTreeContent, hiresImageURL);
		return entryImageContent;
	}
}
