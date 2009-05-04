package org.ddth.blogging.yahoo;

import org.ddth.blogging.BlogEntry;
import org.ddth.blogging.BlogPost;

public class YahooBlogEntry extends BlogEntry {
	private String nextURL;
	
	public YahooBlogEntry(BlogPost post) {
		super(post);
	}

	public void setNextURL(String nextURL) {
		this.nextURL = nextURL;
	}
	
	public String getNextURL() {
		return nextURL;
	}
}
