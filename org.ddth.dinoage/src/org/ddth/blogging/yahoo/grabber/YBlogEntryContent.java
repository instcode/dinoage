package org.ddth.blogging.yahoo.grabber;

import org.ddth.blogging.yahoo.YahooBlogEntry;
import org.ddth.http.core.content.ContentAdapter;
import org.ddth.http.impl.content.DomTreeContent;

public class YBlogEntryContent extends ContentAdapter<DomTreeContent> {

	private YahooBlogEntry entry;

	public YBlogEntryContent(DomTreeContent content, YahooBlogEntry entry) {
		this.entry = entry;
		this.setContent(content);
	}
	
	public YahooBlogEntry getEntry() {
		return entry;
	}
}
