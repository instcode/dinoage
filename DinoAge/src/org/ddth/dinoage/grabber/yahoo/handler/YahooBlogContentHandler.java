/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ddth.http.impl.content.handler.WebpageContentHandler;

public class YahooBlogContentHandler extends WebpageContentHandler {
	private static final Pattern FIX_MALFORMED_BLOG_PATTERN = Pattern.compile("<script type=\"text/javascript\">yfla.wrap.*(<embed [^>]*>)(.*)<\\\\/embed>");
	
	@Override
	protected String filter(String input) {
		String line = input;
		int script = line.indexOf("<script type=\"text/javascript\">yfla.wrap");
		if (script > 0) {
			int noscript = line.indexOf("</noscript>");
			Matcher matcher = FIX_MALFORMED_BLOG_PATTERN.matcher(line.substring(script, noscript));
			if (matcher.find()) {
				String embed = (matcher.group(1) + "</embed>" + matcher.group(2)).replace("\\/", "/").replace("\\\"", "\"");
				StringBuilder buffer = new StringBuilder(line.length());
				line = buffer.append(line.substring(0, script)).append(embed).append(line.substring(noscript)).toString();
			}
		}
		return line;
	}
}
