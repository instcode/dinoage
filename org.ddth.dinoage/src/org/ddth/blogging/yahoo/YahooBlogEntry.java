package org.ddth.blogging.yahoo;

import org.ddth.blogging.Entry;
import org.ddth.blogging.BlogPost;

public class YahooBlogEntry extends Entry {
	private String nextURL;
	private String imageURL;
	private String popupURL;
	private String[] sessionKeepAliveURLs;
	
	public YahooBlogEntry(BlogPost post) {
		super(post);
		setEntryId(post.getPostId());
		if (post.getPostId() == 0) {
			setUrl(YahooBlogAPI.YAHOO_360_GUESTBOOK_URL + post.getAuthor().getUserId());
		}
		else {
			setUrl(YahooBlogAPI.YAHOO_360_BLOG_URL + post.getAuthor().getUserId() + "?p=" + getEntryId());
		}
	}

	public void setNextURL(String nextURL) {
		this.nextURL = nextURL;
	}
	
	public String getNextURL() {
		return nextURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	
	public String getImageURL() {
		return imageURL;
	}
	
	public void setPopupURL(String popupURL) {
		this.popupURL = popupURL;
	}
	
	public String getPopupURL() {
		return popupURL;
	}

	public void setKeepSessionAliveURL(String[] sessionKeepAliveURLs) {
		this.sessionKeepAliveURLs = sessionKeepAliveURLs;
	}
	
	public String[] getSessionKeepAliveURLs() {
		return sessionKeepAliveURLs;
	}
}
