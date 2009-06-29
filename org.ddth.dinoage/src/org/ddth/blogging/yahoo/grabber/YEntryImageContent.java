package org.ddth.blogging.yahoo.grabber;

import org.ddth.http.core.content.ContentAdapter;
import org.ddth.http.impl.content.DomTreeContent;

public class YEntryImageContent extends ContentAdapter<DomTreeContent> {

	private String imageURL;
	
	public YEntryImageContent(DomTreeContent content, String imageURL) {
		setContent(content);
		this.imageURL = imageURL;
	}

	public String getImageURL() {
		return imageURL;
	}
}
