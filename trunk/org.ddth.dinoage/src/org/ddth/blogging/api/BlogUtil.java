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
import org.ddth.blogging.Blog;
import org.ddth.blogging.BlogPost;
import org.ddth.blogging.Entry;

import com.ibm.icu.text.Normalizer;

public class BlogUtil {
	
	public static String getFriendlyURL(String blogURL, BlogPost post) {
		String creationDateTime = DateFormatUtils.format(post.getDate(), "yyyy/MM/dd");
		return blogURL + "/" + creationDateTime + "/" + normalize(post.getTitle()).toLowerCase();
	}
	
	public static String normalize(String text) {
		String dD = "\u0110\u0111";
		String normalizedText = text.replace(dD.charAt(1), 'D').replace(dD.charAt(0), 'd');
		normalizedText = Normalizer.normalize(normalizedText, Normalizer.NFD);
		normalizedText = normalizedText.replaceAll("[\u0100-\uffff]+", "");
		normalizedText = normalizedText.replaceAll("\\W+", "-").replaceAll("^_+", "").replaceAll("_+$", "").replaceAll("_+", "-");
		return normalizedText;
	}
	
	public static String[] getTags(BlogPost post) {
		String postTags = post.getTags();
		if (postTags == null || postTags.trim().length() == 0) {
			return null;
		}
		return postTags.split(", ");
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
	
	public static String[] buildTags(Blog blog) {
		Collection<Entry> entries = blog.getEntries();
		Map<String, String> map = new HashMap<String, String>();
		for (Entry entry : entries) {
			String postTags = entry.getPost().getTags();
			if (postTags == null || postTags.trim().length() == 0) {
				continue;
			}
			String[] tags = postTags.split(", ");
			for (String tag : tags) {
				map.put(tag, tag);
			}
		}
		return map.keySet().toArray(new String[map.size()]);
	}
}
