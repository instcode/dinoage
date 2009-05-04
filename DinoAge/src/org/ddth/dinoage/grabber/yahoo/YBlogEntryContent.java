package org.ddth.dinoage.grabber.yahoo;

import org.ddth.blogging.yahoo.YahooBlogEntry;
import org.ddth.http.core.content.ContentAdapter;
import org.ddth.http.impl.content.DomTreeContent;

public class YBlogEntryContent extends ContentAdapter<DomTreeContent> {

	private YahooBlogEntry entry;

	public YBlogEntryContent(YahooBlogEntry entry) {
		this.entry = entry;
	}
	
	public YahooBlogEntry getEntry() {
		return entry;
	}
}
