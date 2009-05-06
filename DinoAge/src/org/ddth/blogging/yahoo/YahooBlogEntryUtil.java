/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 4:04:42 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.yahoo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.blogging.Author;
import org.ddth.blogging.BlogComment;
import org.ddth.blogging.BlogPost;
import org.ddth.dinoage.grabber.yahoo.YBrowsingSession;
import org.ddth.dinoage.grabber.yahoo.handler.YahooBlogContentHandler;
import org.ddth.http.impl.content.DomTreeContent;
import org.ddth.http.impl.content.WebpageContent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.icu.text.Normalizer;

public class YahooBlogEntryUtil {
	private static Log logger = LogFactory.getLog(YBrowsingSession.class);

	/**
	 * Example: Monday December 24, 2007 - 11:36pm (ICT)
	 */
	private static final DateFormat BLOG_DATE_FORMAT = new SimpleDateFormat("EEEE MMMM d, y - HH:mma (z)");
	
	/**
	 * Pattern to extract post-id from tag-error-<b>#post-id</b>
	 */
	private static final Pattern PATTERN_TO_EXTRACT_POST_ID = Pattern.compile("tag-error-(\\d+)");
	
	/**
	 * Text to determine which link is next navigation
	 * link from blog page.
	 */
	private static final String TEXT_NODE_PERMANENT_LINK = "Permanent Link";
	
	private static class XPathKey {
		private static final XPath UNTHREADSAFE_XPATH = XPathFactory.newInstance().newXPath();
		private XPathExpression expression;
		
		public XPathKey(String path) {
			try {
				expression = UNTHREADSAFE_XPATH.compile(path);
			}
			catch (XPathExpressionException e) {
				logger.debug("Error", e);
			}
		}
		
		public String getText(Node node) {
			return (String) evaluate(node, XPathConstants.STRING);
		}
		
		public Node getNode(Node node) {
			return (Node) evaluate(node, XPathConstants.NODE);
		}
		
		public NodeList getNodeList(Node node) {
			return (NodeList) evaluate(node, XPathConstants.NODESET);
		}
		
		private Object evaluate(Object item, QName returnType) {
			try {
				return expression.evaluate(item, returnType);
			}
			catch (XPathExpressionException e) {
				logger.debug("Error", e);
			}
			return null;
		}
	}
	
	private static class YahooBlogKey {
		static XPathKey YMGL_BLOG			= new XPathKey("//*[@id=\"ymgl-blog\"]");
		
		/**
		 * Start from {@value #YMGL_BLOG}
		 */
		static XPathKey FIRST_BLOG_ENTRY_URL	= new XPathKey("DIV/DL/DD[1]/DIV[3]/SPAN[2]/A");
		static XPathKey PREVIOUS_BLOG_ENTRY_URL	= new XPathKey("DIV/DL/DD/DIV[3]/P[2]/SPAN[2]/A/@href");
		static XPathKey BLOG_ENTRY_TAG_ERROR_ID	= new XPathKey("DIV/DL/DD/DIV[3]/DIV/@id");
		static XPathKey BLOG_ENTRY_TITLE		= new XPathKey("DIV/DL/DT");
		static XPathKey BLOG_ENTRY_BODY			= new XPathKey("DIV/DL/DD/DIV[2]");
		static XPathKey BLOG_ENTRY_CREATED_DATE	= new XPathKey("DIV/DL/DD/DIV[3]/P");
		static XPathKey BLOG_ENTRY_TAGS			= new XPathKey("DIV/DL/DD/DIV[3]/SPAN/SPAN/A/text()");
		static XPathKey BLOG_ENTRY_COMMENTS		= new XPathKey("//*[@id=\"comments\"]");

		/**
		 * Start from {@value #BLOG_ENTRY_COMMENTS}
		 */
		static XPathKey BLOG_ENTRY_NEXT_PAGE	= new XPathKey("//*[@id=\"num_next\"]/@href");
		static XPathKey COMMENTS_AUTHOR			= new XPathKey("DIV/DIV[1]");
		static XPathKey COMMENTS_COMMENT		= new XPathKey("DIV/DIV[2]");

		/**
		 * Start from {@value #COMMENTS_AUTHOR}
		 */
		static XPathKey COMMENTS_AUTHOR_PHOTO	= new XPathKey("DIV/A/IMG/@src");
		static XPathKey COMMENTS_AUTHOR_URL		= new XPathKey("UL/LI/A/@href");
		static XPathKey COMMENTS_AUTHOR_NAME	= new XPathKey("UL/LI/A/@title");

		/**
		 * Start from {@value #COMMENTS_COMMENT}
		 */
		static XPathKey COMMENTS_COMMENT_TEXT	= new XPathKey("P[1]");
		static XPathKey COMMENTS_COMMENT_DATE	= new XPathKey("P[2]");
	}

	/**
	 * @param doc
	 * @return
	 */
	public static String parseNavigationLink(Document doc) {
		Node entry = YahooBlogKey.YMGL_BLOG.getNode(doc);
		NodeList links = YahooBlogKey.FIRST_BLOG_ENTRY_URL.getNodeList(entry);
		int length = links.getLength();
		String navigationURL = null;
		for (int index = 0; index < length; index++) {
			Node node = links.item(index);
			String name = node.getFirstChild().getNodeValue();
			if (TEXT_NODE_PERMANENT_LINK.equals(name)) {
				navigationURL = node.getAttributes().getNamedItem("href").getNodeValue();
			}
		}
		return navigationURL;
	}

	/**
	 * Extract Yahoo blog entry information from the given DOM tree.<br>
	 * <br>
	 * Because Yahoo 360 blog doesn't have spam filter, well-known blogs
	 * sometimes are flooded with a lot of comments from spammers. This will
	 * create many comment pages with identical content. If the backup doesn't
	 * detect this situation, it will suffer from lot of useless comments in its
	 * data storage.<br>
	 * <br>
	 * DinoAge added a very basic spam filter so it doesn't have to follow &
	 * grab all the comments. The spam detection algorithm is implemented as:<br>
	 * <ul>
	 * <li>Increase the spamCount if 2 consecutive comments are made by the same
	 * author. Keep only one content if they are the same.</li>
	 * <li>Prepare the next URL to collect more comments only if there is at
	 * least one "real" comment, not made by spammer. Otherwise, it should jump
	 * to next blog entry because it assumes there is no more interesting
	 * comments on the next page to be collected.</li>
	 * </ul>
	 * Great feature, right? :D
	 * <p>
	 * 
	 * @param doc
	 *            The document root of the DOM tree.
	 * @return A YahooBlogEntry object which contains all blog post, tags,
	 *         comments..
	 */
	public static YahooBlogEntry parseEntry(Document doc) {
		YahooBlogEntry blogEntry = null;
		try {
			Node entry = YahooBlogKey.YMGL_BLOG.getNode(doc);
			BlogPost blogPost = parseBlogPost(entry);
			blogEntry = new YahooBlogEntry(blogPost);
			
			String nextURL = null;
			Node comment = YahooBlogKey.BLOG_ENTRY_COMMENTS.getNode(doc);
			if (comment != null) {
				NodeList authors = YahooBlogKey.COMMENTS_AUTHOR.getNodeList(comment);
				NodeList comments = YahooBlogKey.COMMENTS_COMMENT.getNodeList(comment);
				
				String spammer = "";
				String spamContent = "";
				int spamCount = 0;

				for (int i = 0; i < comments.getLength(); i++) {
					BlogComment blogComment = parseComment(authors.item(i), comments.item(i));
					if (spammer.equals(blogComment.getAuthor().getName())) {
						spamCount++;
						if (!spamContent.equals(blogComment.getContent())) {
							blogEntry.addComment(blogComment);
						}
					}
					else {
						spammer = blogComment.getAuthor().getName();
						spamCount = 0;
						blogEntry.addComment(blogComment);
					}
				}
				if (spamCount < comments.getLength() - 1) {
					nextURL = YahooBlogKey.BLOG_ENTRY_NEXT_PAGE.getText(entry);
				}
			}
			if (nextURL == null || nextURL.length() == 0) {
				nextURL = YahooBlogKey.PREVIOUS_BLOG_ENTRY_URL.getText(entry);
			}
			blogEntry.setNextURL(nextURL);
		}
		catch (ParseException e) {
			logger.debug("Error", e);
		}
		return blogEntry;
	}

	private static BlogPost parseBlogPost(Node ymglBlog) throws ParseException {
		BlogPost blogPost = new BlogPost();

		String entryURL = YahooBlogKey.BLOG_ENTRY_TAG_ERROR_ID.getText(ymglBlog);
		Matcher matcher = PATTERN_TO_EXTRACT_POST_ID.matcher(entryURL);
		String postId = "error";
		if (matcher.matches()) {
			postId = matcher.group(1);
		}
		String title = YahooBlogKey.BLOG_ENTRY_TITLE.getText(ymglBlog);
		// For testing purpose
		Normalizer.normalize(title, Normalizer.NFD);
		String date = YahooBlogKey.BLOG_ENTRY_CREATED_DATE.getText(ymglBlog);
		Node body = YahooBlogKey.BLOG_ENTRY_BODY.getNode(ymglBlog);
		NodeList tags = YahooBlogKey.BLOG_ENTRY_TAGS.getNodeList(ymglBlog);
		
		StringBuilder buffer = new StringBuilder("");
		if (tags != null) {
			int length = tags.getLength();
			for (int i = 0; i < length; i++) {
				Node node = tags.item(i);
				buffer.append(node.getNodeValue()).append((i < length - 1) ? ", " : "");
			}
		}
		blogPost.setPostId(postId);
		blogPost.setTitle(title);
		blogPost.setTags(buffer.toString());
		blogPost.setContent(getRawText(body));
		blogPost.setDate(BLOG_DATE_FORMAT.parse(date));
		return blogPost;
	}

	private static BlogComment parseComment(Node author, Node comment) throws ParseException {
		String text = YahooBlogKey.COMMENTS_COMMENT_TEXT.getText(comment);
		String time = YahooBlogKey.COMMENTS_COMMENT_DATE.getText(comment);

		String author_name = YahooBlogKey.COMMENTS_AUTHOR_NAME.getText(author);
		String photo = YahooBlogKey.COMMENTS_AUTHOR_PHOTO.getText(author);
		String author_url = YahooBlogKey.COMMENTS_AUTHOR_URL.getText(author);
		String author_photo = "";
		
		if (photo != null) {
			author_photo = photo;
		}
		
		BlogComment blogComment = new BlogComment(new Author(author_name, author_url, author_photo), text);
		blogComment.setDate(BLOG_DATE_FORMAT.parse(time));
		return blogComment;
	}
	
	private static String getRawText(Node node) {
		StringWriter writer = new StringWriter();
		HTMLNodeBuilder builder = new HTMLNodeBuilder(writer);
		try {
			builder.serialize(node);
		}
		catch (IOException e) {
			logger.debug("Error", e);
		}
		return writer.getBuffer().toString();
	}

	public static void main(String[] args) throws IOException {
		YahooBlogContentHandler contentHandler = new YahooBlogContentHandler();
		FileInputStream inputStream = new FileInputStream("./workspaces/w1/instcode/entry/entry-1590.html");
		WebpageContent webContent = new WebpageContent(inputStream, "utf-8");
		DomTreeContent content = (DomTreeContent)contentHandler.handle(webContent);
	
		System.out.println(YahooBlogEntryUtil.parseNavigationLink(content.getDocument()));

		YahooBlogEntry entry = YahooBlogEntryUtil.parseEntry(content.getDocument());
		BlogPost post = entry.getPost();
		System.out.println(
				"Link: " + entry.getNextURL() +
				"\nEntry: " + post.getPostId() +
				"\nBlog: " + post.getTitle() +
				"\nBody: " + post.getContent() +
				"\nTags: " + post.getTags() +
				"\nDate: " + post.getDate());
		List<BlogComment> comments = entry.getComments();
		for (BlogComment comment : comments) {
			logger.debug(comment);
		}
	}
}
