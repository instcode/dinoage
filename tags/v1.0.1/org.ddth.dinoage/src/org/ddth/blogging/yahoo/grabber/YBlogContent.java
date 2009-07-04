package org.ddth.blogging.yahoo.grabber;

import org.ddth.blogging.yahoo.YahooBlog;
import org.ddth.http.impl.content.DomTreeContent;

public class YBlogContent extends DomTreeContent {

	private YahooBlog blog;
	
	public YBlogContent(DomTreeContent content, YahooBlog blog) {
		super(content.getContent(), content.getDocument());
		this.blog = blog;
	}

	public YahooBlog getBlog() {
		return blog;
	}
}
