package org.ddth.blogging.yahoo;

import org.ddth.blogging.Entry;
import org.ddth.blogging.BlogPost;

public class YahooBlogEntry extends Entry {
	private String nextURL;
	private String blogId;
	
	public YahooBlogEntry(String entryURL, BlogPost post) {
		super(post);
		setUrl(entryURL);
	}

	public void setBlogId(String blogId) {
		this.blogId = blogId;
	}
	
	public String getBlogId() {
		return blogId;
	}
	
	public void setNextURL(String nextURL) {
		this.nextURL = nextURL;
	}
	
	public String getNextURL() {
		return nextURL;
	}
}
