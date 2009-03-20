/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo.handler;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.ddth.blogging.yahoo.YahooBlog;
import org.ddth.http.core.Session;
import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.handler.ContentHandler;

public class BlogEntryContentHandler implements ContentHandler {
	private static final String KEY_BLOG_ENTRY_REGEXP = YahooBlog.YAHOO_360_BLOG_URL + ".*p=([\\d+]).*";
	
	private static final String CHECK_VALID_BODY_XPATH = "DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL/DT";
	private static final String NEXT_ENTRY_URL_XPATH = "DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL/DD/DIV[3]/P[2]/SPAN[2]/A";
	private static final String FIRST_ENTRY_URL_XPATH = "DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL/DD/DIV[3]/SPAN[2]/A";
	
	private static XPathExpression ENTRY_BODY_VALID_EXPRESSION;
	private Session session;
	
	static {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			ENTRY_BODY_VALID_EXPRESSION = xpath.compile(CHECK_VALID_BODY_XPATH);
		}
		catch (XPathExpressionException e) {
		}
	}
	
	public BlogEntryContentHandler(Session session) {
		this.session = session;
	}

	@Override
	public Content<?> handle(Content<?> content) {
		// TODO Auto-generated method stub
		return null;
	}
}
