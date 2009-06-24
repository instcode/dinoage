package org.ddth.blogging.yahoo.grabber;

import org.ddth.blogging.Blog;
import org.ddth.http.impl.content.NavigationContent;

public class YBlogContent extends NavigationContent {

	private Blog blog;
	
	public YBlogContent(String[] urls, Blog blog) {
		super(urls);
		this.blog = blog;
	}

	public Blog getBlog() {
		return blog;
	}
}
