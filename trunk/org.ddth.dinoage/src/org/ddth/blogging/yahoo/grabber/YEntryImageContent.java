package org.ddth.blogging.yahoo.grabber;

import org.ddth.http.impl.content.DomTreeContent;

public class YEntryImageContent extends DomTreeContent {

	private String imageURL;
	private String postId;
	
	public YEntryImageContent(DomTreeContent content, String imageURL, String postId) {
		super(content.getContent(), content.getDocument());
		this.imageURL = imageURL;
		this.postId = postId;
	}

	public String getPostId() {
		return postId;
	}
	
	public String getImageURL() {
		return imageURL;
	}
}
