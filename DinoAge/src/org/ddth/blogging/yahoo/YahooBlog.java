/****************************************************
 * $Project: DinoAge
 * $Date:: Jun 26, 2009
 * $Revision:
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.blogging.yahoo;

import org.ddth.blogging.Blog;

/**
 * @author khoanguyen
 *
 */
public class YahooBlog extends Blog {

	private String firstEntryURL;
	private String guestbookURL;
	
	/**
	 * @return the guestbookURL
	 */
	public String getGuestbookURL() {
		return guestbookURL;
	}
	
	/**
	 * @param guestbookURL the guestbookURL to set
	 */
	public void setGuestbookURL(String guestbookURL) {
		this.guestbookURL = guestbookURL;
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
