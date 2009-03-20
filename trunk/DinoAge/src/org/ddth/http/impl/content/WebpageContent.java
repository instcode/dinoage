/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 1, 2008 7:45:55 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.impl.content;

import java.io.InputStream;

import org.ddth.http.core.content.ContentAdapter;

public class WebpageContent extends ContentAdapter<InputStream> {

	private String charset;

	public WebpageContent(InputStream content, String charset) {
		this.charset = charset;
		setContent(content);
	}

	public String getCharset() {
		return charset;
	}
}
