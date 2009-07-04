package org.ddth.blogging.yahoo.grabber;

import org.ddth.blogging.yahoo.YahooBlogEntry;
import org.ddth.http.impl.content.DomTreeContent;

public class YBlogEntryContent extends DomTreeContent {

	private YahooBlogEntry entry;

	public YBlogEntryContent(DomTreeContent content, YahooBlogEntry entry) {
		super(content.getContent(), content.getDocument());
		this.entry = entry;
	}
	
	public YahooBlogEntry getEntry() {
		return entry;
	}
}
