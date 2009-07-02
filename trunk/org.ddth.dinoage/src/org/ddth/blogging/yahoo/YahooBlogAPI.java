package org.ddth.blogging.yahoo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;
import org.ddth.blogging.api.BasicBlogAPI;
import org.ddth.blogging.yahoo.grabber.handler.YBlogEntryContentHandler;
import org.ddth.blogging.yahoo.grabber.handler.YEntryImageContentHandler;
import org.ddth.blogging.yahoo.grabber.handler.YEntryListContentHandler;
import org.ddth.blogging.yahoo.grabber.handler.YGuestbookContentHandler;
import org.ddth.blogging.yahoo.grabber.handler.YProfilePageContentHandler;
import org.ddth.http.core.content.handler.ContentHandlerDispatcher;
import org.ddth.http.core.content.handler.DefaultHandler;

public class YahooBlogAPI extends BasicBlogAPI {

	public static final String YAHOO_360_HOST = "360.yahoo.com";
	public static final String YAHOO_360_PROFILE_URL = "http://360.yahoo.com/profile-";
	public static final String YAHOO_360_GUESTBOOK_URL = "http://360.yahoo.com/guestbook-";
	public static final String YAHOO_360_BLOG_URL = "http://blog.360.yahoo.com/blog-";
	
	private static final Pattern PATTERN_TO_EXTRACT_PROFILE_ID = Pattern.compile(
			".*360.yahoo.com/(profile|guestbook|blog|feeds|friends)-([^\\?]*).*");
	
	public static final ContentHandlerDispatcher YAHOO_360_CONTENT_DISPATCHER = new ContentHandlerDispatcher();
	public static final ContentHandlerDispatcher YAHOO_360_PROFILE_CONTENT_DISPATCHER = new ContentHandlerDispatcher();
	static {
		YAHOO_360_CONTENT_DISPATCHER.registerHandler("http://.*/guestbook-.*", new YGuestbookContentHandler());
		YAHOO_360_CONTENT_DISPATCHER.registerHandler("http://.*/blog-.*\\?.*p=(\\d+).*", new YBlogEntryContentHandler());
		YAHOO_360_CONTENT_DISPATCHER.registerHandler("http://.*/blog-[^?]*", new YEntryListContentHandler());
		YAHOO_360_CONTENT_DISPATCHER.registerHandler("http://.*/blog/slideshow.html.*", new YEntryImageContentHandler());
		YAHOO_360_CONTENT_DISPATCHER.registerHandler("http://.*[.]yahoofs[.].*/blog/.*jpg.*", new DefaultHandler());
		YAHOO_360_CONTENT_DISPATCHER.registerHandler("http://.*[.]yahoo[.].*/b[\\?]P.*", new DefaultHandler());
		
		
		YAHOO_360_PROFILE_CONTENT_DISPATCHER.registerHandler("http://.*", new YProfilePageContentHandler());
	}

	public static String parseProfileId(String url) {
		if (url != null) {
			Matcher matcher = PATTERN_TO_EXTRACT_PROFILE_ID.matcher(url);
			if (matcher.matches()) {
				return matcher.group(2);
			}
		}
		return "";
	}

	public boolean createEntry(Entry entry) {
		return true;
	}

	public boolean createComment(Comment comment) {
		return false;
	}
}
