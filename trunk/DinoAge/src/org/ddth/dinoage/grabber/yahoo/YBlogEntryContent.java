package org.ddth.dinoage.grabber.yahoo;

import org.ddth.blogging.BlogEntry;
import org.ddth.http.core.content.ContentAdapter;
import org.ddth.http.impl.content.DomTreeContent;

public class YBlogEntryContent extends ContentAdapter<DomTreeContent> {

	private BlogEntry entry;
	private String nextEntryURL;

	public YBlogEntryContent(BlogEntry entry, String nextEntryURL) {
		this.entry = entry;
		this.nextEntryURL = nextEntryURL;
	}
	
	public BlogEntry getEntry() {
		return entry;
	}
	
	public String getNextURL() {
		return nextEntryURL;
	}
}
