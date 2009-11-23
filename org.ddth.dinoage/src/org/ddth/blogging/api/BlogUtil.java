/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 2:14:06 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.ddth.blogging.Author;
import org.ddth.blogging.Blog;
import org.ddth.blogging.BlogPost;
import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;

import com.ibm.icu.text.Normalizer;

public class BlogUtil {
	
	public static String getFriendlyURL(String blogURL, BlogPost post) {
		if (post.getPostId() == 0) {
			return blogURL + "/guestbook";
		}
		String creationDateTime = DateFormatUtils.format(post.getDate(), "yyyy/MM/dd");
		return blogURL + "/" + creationDateTime + "/" + normalize(post.getTitle()).toLowerCase();
	}
	
	public static String getPostType(BlogPost post) {
		return post.getPostId() == 0 ? "page" : "post";
	}
	
	public static String normalize(String text) {
		String dD = "\u0110\u0111";
		String normalizedText = text.replace(dD.charAt(1), 'D').replace(dD.charAt(0), 'd');
		normalizedText = Normalizer.normalize(normalizedText, Normalizer.NFD);
		normalizedText = normalizedText.replaceAll("[\u0100-\uffff]+", "");
		normalizedText = normalizedText.replaceAll("\\W+", "-").replaceAll("^_+", "").replaceAll("_+$", "").replaceAll("_+", "-");
		return normalizedText;
	}

	private static Map<String, DateFormat> formatters = new HashMap<String, DateFormat>();
	public static String format(Date date, String pattern) {
		DateFormat formatter = formatters.get(pattern);
		if (formatter == null) {
			formatter = new SimpleDateFormat(pattern);
			formatters.put(pattern, formatter);
		}
		return formatter.format(date);
	}
	
	public static String[] getTags(BlogPost post) {
		return post.getTags().split(", ");
	}
	
	/**
	 * Rebuild all entries, comments of the given blog. All authors of
	 * entries will be replaced with the blog author. All comments if
	 * made by the authors of this blog will be replaced as well. In
	 * addition, this will rebuilds all tags of the blog.<br>
	 * <br>
	 * <b>Important</b>: The given blog will be modified!
	 * 
	 * @param blog
	 * @param author
	 */
	public static void rebuild(Blog blog) {
		Collection<Entry> entries = blog.getEntries();
		Author author = blog.getAuthor();
		Map<String, String> map = new HashMap<String, String>();
		for (Entry entry : entries) {
			String postTags = entry.getPost().getTags();
			if (postTags != null && postTags.trim().length() > 0) {
				String[] tags = postTags.split(", ");
				for (String tag : tags) {
					map.put(tag, tag);
				}
			}
			for (Comment comment : entry.getComments()) {
				if (comment.getAuthor().getUserId().equals(entry.getPost().getAuthor().getUserId())) {
					comment.setAuthor(author);
				}
			}
		}
		blog.setTags(map.keySet().toArray(new String[map.size()]));
	}
}
