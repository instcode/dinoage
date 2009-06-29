package org.ddth.blogging.yahoo;

import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;
import org.ddth.blogging.api.BasicBlogAPI;
import org.ddth.blogging.yahoo.grabber.handler.YBlogEntryContentHandler;
import org.ddth.blogging.yahoo.grabber.handler.YEntryImageContentHandler;
import org.ddth.blogging.yahoo.grabber.handler.YEntryListContentHandler;
import org.ddth.blogging.yahoo.grabber.handler.YGuestbookContentHandler;
import org.ddth.http.core.content.handler.ContentHandlerDispatcher;
import org.ddth.http.core.content.handler.DefaultHandler;

public class YahooBlogAPI extends BasicBlogAPI {

	public static final String YAHOO_360_HOST = "360.yahoo.com";
	public static final String YAHOO_360_PROFILE_URL = "http://360.yahoo.com/profile-";
	public static final String YAHOO_360_GUESTBOOK_URL = "http://360.yahoo.com/guestbook-";
	public static final String YAHOO_360_BLOG_URL = "http://blog.360.yahoo.com/blog-";
	
	public static final ContentHandlerDispatcher YAHOO_360_CONTENT_DISPATCHER = new ContentHandlerDispatcher();
	static {
		YAHOO_360_CONTENT_DISPATCHER.registerHandler("http://.*/guestbook-.*", new YGuestbookContentHandler());
		YAHOO_360_CONTENT_DISPATCHER.registerHandler("http://.*/blog-.*\\?.*p=(\\d+).*", new YBlogEntryContentHandler());
		YAHOO_360_CONTENT_DISPATCHER.registerHandler("http://.*/blog-[^?]*", new YEntryListContentHandler());
		YAHOO_360_CONTENT_DISPATCHER.registerHandler("http://.*/blog/slideshow.html.*", new YEntryImageContentHandler());
		YAHOO_360_CONTENT_DISPATCHER.registerHandler("http://.*yahoofs[.]com/blog/.*jpg.*", new DefaultHandler());
	}

	public static String parseProfileId(String profileURL) {
		if (profileURL == null) {
			return "";
		}
		int begin = YahooBlogAPI.YAHOO_360_PROFILE_URL.length();
		int end = profileURL.indexOf("?", begin);
		end = (end < 0) ? profileURL.length() : end;
		return end <= begin ? "" : profileURL.substring(begin, end);
	}

	public boolean createEntry(Entry entry) {
		return true;
	}

	public boolean createComment(Comment comment) {
		return false;
	}
}
