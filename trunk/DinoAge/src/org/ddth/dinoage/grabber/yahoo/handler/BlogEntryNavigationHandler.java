/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.grabber.yahoo.handler;

import java.io.ByteArrayInputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.ddth.dinoage.ResourceManager;
import org.ddth.dinoage.model.Persistence;
import org.ddth.grabber.core.connection.Session;
import org.ddth.grabber.impl.handler.RegExpNavigationHandler;
import org.w3c.dom.Node;

public class BlogEntryNavigationHandler extends RegExpNavigationHandler {
	private static XPathExpression ENTRY_BODY_VALID_EXPRESSION;
	
	private Session session;
	private Persistence persistence;
	
	public BlogEntryNavigationHandler(Persistence persistence, Session session) {
		super(ResourceManager.getMessage(ResourceManager.KEY_ENCODING),
				new String[] {
					"DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL/DD/DIV[3]/P[2]/SPAN[2]/A",
	        	},
	        	PATTERN_TYPE_INCLUSIVE_LINK,
	        	new String[] {
					ResourceManager.getMessage(ResourceManager.KEY_BLOG_URL) + ".*"
        		}
        );
		
		this.session = session;
		this.persistence = persistence;
	}
	
	@Override
	protected boolean checkBody(Node body) throws Exception {
		if (ENTRY_BODY_VALID_EXPRESSION == null) {
			XPath xpath = XPathFactory.newInstance().newXPath();
			ENTRY_BODY_VALID_EXPRESSION = xpath.compile("DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL/DT");
		}
		Node node = (Node) ENTRY_BODY_VALID_EXPRESSION.evaluate(body, XPathConstants.NODE);
		return (node != null);
	}
	
	@Override
	protected void handleContent(byte[] buffer) {
		persistence.write(new ByteArrayInputStream(buffer), Persistence.BLOG_ENTRY);
	}
	
	@Override
	protected void handleLink(String link) {
		session.queueRequest(link, new BlogEntryNavigationHandler(persistence, session));
	}
}
