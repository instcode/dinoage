package org.ddth.blogging.yahoo.grabber;

import org.ddth.blogging.yahoo.YahooBlog;
import org.ddth.http.core.content.ContentAdapter;
import org.ddth.http.impl.content.DomTreeContent;

public class YBlogContent extends ContentAdapter<DomTreeContent> {

	private YahooBlog blog;
	
	public YBlogContent(DomTreeContent content, YahooBlog blog) {
		setContent(content);
		this.blog = blog;
	}

	public YahooBlog getBlog() {
		return blog;
	}
}
