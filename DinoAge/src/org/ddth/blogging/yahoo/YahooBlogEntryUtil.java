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
	
	private static class NavigationEntryKey {
		static XPathExpression BLOG_ENTRY_URL;
		static XPathExpression PREVIOUS_BLOG_ENTRY_URL;
	}
	
	private static class BlogEntryKey {
		static XPathExpression BLOG_ENTRY;
		static XPathExpression TITLE;
		static XPathExpression BODY;
		static XPathExpression CREATED_DATE;
		static XPathExpression TAGS;
		static XPathExpression COMMENTS;
	}
	
	static {
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			BlogEntryKey.BLOG_ENTRY = xpath.compile("/HTML/BODY/DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL");
			BlogEntryKey.TITLE = xpath.compile("DT");
			BlogEntryKey.BODY = xpath.compile("DD/DIV[2]");
			BlogEntryKey.CREATED_DATE = xpath.compile("DD/DIV[3]/P");
			BlogEntryKey.TAGS = xpath.compile("DD/DIV[3]/SPAN/SPAN/A");
			BlogEntryKey.COMMENTS = xpath.compile("//*[@id=\"num_next\"]");
			
			NavigationEntryKey.BLOG_ENTRY_URL = xpath.compile("/HTML/BODY/DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL/DD/DIV[3]/SPAN[2]/A");
			NavigationEntryKey.PREVIOUS_BLOG_ENTRY_URL = xpath.compile("/HTML/BODY/DIV[2]/DIV/DIV[2]/DIV[2]/DIV/DIV/DL/DD/DIV[3]/P[2]/SPAN[2]/A");
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String parsePreviousBlogEntryLink(Document doc) {
		Node link = null;
		try {
			link = (Node) NavigationEntryKey.PREVIOUS_BLOG_ENTRY_URL.evaluate(doc, XPathConstants.NODE);
		}
		catch (XPathExpressionException e) {
		}
		return link != null ? link.getAttributes().getNamedItem("href").getNodeValue() : "";
	}
	
	public static String parseNavigationLink(Document doc) {
		Node link = null;
		try {
			link = (Node) NavigationEntryKey.BLOG_ENTRY_URL.evaluate(doc, XPathConstants.NODE);
		}
		catch (XPathExpressionException e) {
		}
		return link != null ? link.getAttributes().getNamedItem("href").getNodeValue() : "";
	}
	
	/**
	 * @param inputStream
	 * @return
	 */
	public static BlogEntry parseEntry(Document doc) {
		BlogEntry blogEntry = new BlogEntry();
		try {
			Node entry = (Node) BlogEntryKey.BLOG_ENTRY.evaluate(doc, XPathConstants.NODE);
			Node title = (Node) BlogEntryKey.TITLE.evaluate(entry, XPathConstants.NODE);
			Node body = (Node) BlogEntryKey.BODY.evaluate(entry, XPathConstants.NODE);
			Node date = (Node) BlogEntryKey.CREATED_DATE.evaluate(entry, XPathConstants.NODE);
			//Node comment = (Node) BlogEntryKey.COMMENTS.evaluate(entry, XPathConstants.NODE);
			NodeList tags = (NodeList) BlogEntryKey.TAGS.evaluate(entry, XPathConstants.NODESET);
			
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
		WebpageContent webContent = new WebpageContent(new FileInputStream("./workspace/w1/onlyu/entry/entry-0.html"), "utf-8");
		DomTreeContent content = (DomTreeContent)contentHandler.handle(webContent);
		BlogEntry entry = YahooBlogEntryUtil.parseEntry(content.getDocument());
		
		System.out.println(
				"Blog: " + entry.getTitle() +
				"\nBody: " + entry.getContent() +
				"\nTags: " + entry.getTags() +
				"\nDate: " + entry.getDate());
	}
}
