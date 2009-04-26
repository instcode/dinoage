/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 4:04:42 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.yahoo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.EnumMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.ddth.blogging.BlogEntry;
import org.ddth.http.impl.content.DomTreeContent;
import org.ddth.http.impl.content.WebpageContent;
import org.ddth.http.impl.content.handler.WebpageContentHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class YahooBlogEntryUtil {
	/**
	 * Example: Monday December 24, 2007 - 11:36pm (ICT)
	 */
	private static final DateFormat BLOG_DATE_FORMAT = new SimpleDateFormat("EEEE MMMM d, y - HH:mma (z)");
	
	private static final String ENTRY_URL_XPATH = "DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL/DT/A";
	private static final String NEXT_PREVIOUS_URL_XPATH = "DIV[2]/DIV/DIV[2]/SPAN[2]/SPAN";
	private static final String CHECK_VALID_BODY_XPATH = "DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL/DT";
	private static final String NEXT_ENTRY_URL_XPATH = "DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL/DD/DIV[3]/P[2]/SPAN[2]/A";
	private static final String FIRST_ENTRY_URL_XPATH = "DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL/DD/DIV[3]/SPAN[2]/A";
	
	private enum BlogEntryKey {
		BLOG_ENTRY,
		TITLE,
		BODY,
		CREATED_DATE,
		TAGS;
	}
	
	private static final EnumMap<BlogEntryKey, XPathExpression> YAHOO_BLOG_ENTRY_XPES =
		new EnumMap<BlogEntryKey, XPathExpression>(BlogEntryKey.class);
	
	static {
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			YAHOO_BLOG_ENTRY_XPES.put(BlogEntryKey.BLOG_ENTRY,
					xpath.compile("/HTML/BODY/DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL"));
			YAHOO_BLOG_ENTRY_XPES.put(BlogEntryKey.TITLE,
					xpath.compile("DT"));
			YAHOO_BLOG_ENTRY_XPES.put(BlogEntryKey.BODY,
					xpath.compile("DD/DIV[2]"));
			YAHOO_BLOG_ENTRY_XPES.put(BlogEntryKey.CREATED_DATE,
					xpath.compile("DD/DIV[3]/P"));
			YAHOO_BLOG_ENTRY_XPES.put(BlogEntryKey.TAGS,
					xpath.compile("DD/DIV[3]/SPAN/SPAN/A"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String parseNavigationLink(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @param inputStream
	 * @return
	 */
	public static BlogEntry parseEntry(Document doc) {
		BlogEntry blogEntry = new BlogEntry();
		try {
			Node entry = (Node) YAHOO_BLOG_ENTRY_XPES.get(BlogEntryKey.BLOG_ENTRY).evaluate(doc, XPathConstants.NODE);
			Node title = (Node) YAHOO_BLOG_ENTRY_XPES.get(BlogEntryKey.TITLE).evaluate(entry, XPathConstants.NODE);
			Node body = (Node) YAHOO_BLOG_ENTRY_XPES.get(BlogEntryKey.BODY).evaluate(entry, XPathConstants.NODE);
			Node date = (Node) YAHOO_BLOG_ENTRY_XPES.get(BlogEntryKey.CREATED_DATE).evaluate(entry, XPathConstants.NODE);
			NodeList tags = (NodeList) YAHOO_BLOG_ENTRY_XPES.get(BlogEntryKey.TAGS).evaluate(entry, XPathConstants.NODESET);
			
			StringBuilder buffer = new StringBuilder();  
			int length = tags.getLength();
			for (int i = 0; i < length; i++) {
				Node node = tags.item(i);
				buffer.append(node.getFirstChild().getNodeValue()).append((i < length - 1) ? ", " : "");
			}
			blogEntry.setTitle(title.getFirstChild().getNodeValue());
			blogEntry.setTags(buffer.toString());
			blogEntry.setContent(getRawText(body));
			blogEntry.setDate(BLOG_DATE_FORMAT.parse(date.getFirstChild().getNodeValue()));
		}
		catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		catch (DOMException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return blogEntry;
	}
	
	public static String getRawText(Node node) {
		StringWriter writer = new StringWriter();
		HTMLNodeBuilder builder = new HTMLNodeBuilder(writer);
		try {
			builder.serialize(node);
		} catch (IOException e) {
		}
		return writer.getBuffer().toString();
	}

	public static void main(String[] args) throws FileNotFoundException {
		WebpageContentHandler contentHandler = new WebpageContentHandler();
		WebpageContent webContent = new WebpageContent(new FileInputStream("entry.html"), "utf-8");
		DomTreeContent content = (DomTreeContent)contentHandler.handle(webContent);
		BlogEntry entry = YahooBlogEntryUtil.parseEntry(content.getDocument());
		
		System.out.println(
				"Blog: " + entry.getTitle() +
				"\nBody: " + entry.getContent() +
				"\nTags: " + entry.getTags() +
				"\nDate: " + entry.getDate());
	}
}
