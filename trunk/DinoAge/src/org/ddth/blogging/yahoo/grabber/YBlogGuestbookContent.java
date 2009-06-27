package org.ddth.blogging.yahoo.grabber;

import org.ddth.http.core.content.ContentAdapter;
import org.ddth.http.impl.content.DomTreeContent;

public class YBlogGuestbookContent extends ContentAdapter<DomTreeContent> {

	public YBlogGuestbookContent(DomTreeContent content) {
		setContent(content);
	}
}
