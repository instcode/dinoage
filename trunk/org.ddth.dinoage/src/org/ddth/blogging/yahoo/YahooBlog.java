/****************************************************
 * $Project: DinoAge
 * $Date:: Jun 26, 2009
 * $Revision:
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.blogging.yahoo;

import org.ddth.blogging.Author;
import org.ddth.blogging.Blog;

/**
 * @author khoanguyen
 *
 */
public class YahooBlog extends Blog {

	private String firstEntryURL;
	
	public YahooBlog(Author author, String description) {
		addAuthor(author);
		setBlogId(author.getUserId());
		setUrl(YahooBlogAPI.YAHOO_360_BLOG_URL + getBlogId());
		setTitle(author.getName());
		setDescription(description);
	}
	/**
	 * @return the entryURL
	 */
	public String getFirstEntryURL() {
		return firstEntryURL;
	}
	
	/**
	 * @param entryURL the entryURL to set
	 */
	public void setFirstEntryURL(String entryURL) {
		this.firstEntryURL = entryURL;
	}
}
