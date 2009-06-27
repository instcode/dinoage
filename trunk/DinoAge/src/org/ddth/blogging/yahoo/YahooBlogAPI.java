package org.ddth.blogging.yahoo;

import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;
import org.ddth.blogging.api.BasicBlogAPI;

public class YahooBlogAPI extends BasicBlogAPI {

	public static final String YAHOO_360_PROFILE_URL = "http://360.yahoo.com/profile-";
	public static final String YAHOO_360_GUESTBOOK_URL = "http://360.yahoo.com/guestbook-";
	public static final String YAHOO_360_BLOG_URL = "http://blog.360.yahoo.com/blog-";

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
