/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo.handler;

import javax.xml.xpath.XPathExpression;

import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.handler.ContentHandler;

public class EntryListContentHandler implements ContentHandler {
	
	private static XPathExpression ENTRY_BODY_VALID_EXPRESSION;
	private static final String XX = "DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL/DT";
	private static final String YY = "DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL/DT/A";
	
	public EntryListContentHandler() {
	}

	@Override
	public Content<?> handle(Content<?> content) {
		// TODO Auto-generated method stub
		return null;
	}
}
