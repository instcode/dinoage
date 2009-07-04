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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.ddth.blogging.BlogPost;
import org.ddth.blogging.Comment;
import org.ddth.blogging.yahoo.grabber.YBrowsingSession;
import org.ddth.blogging.yahoo.grabber.handler.YahooBlogContentHandler;
import org.ddth.http.impl.content.DomTreeContent;
import org.ddth.http.impl.content.WebpageContent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class YahooBlogUtil {
	private static Log logger = LogFactory.getLog(YBrowsingSession.class);

	/**
	 * This can parse date time similar to the following
	 * sample: Monday December 24, 2007 - 11:36pm (ICT)
	 * <br>
	 * Because {@link SimpleDateFormat} is not thread-safe,
	 * the constant below only contains a parsing pattern.
	 */
	private static final String BLOG_DATE_FORMAT = "EEEE MMMM d, y - HH:mma (z)";
	private static final String GUESTBOOK_DATE_FORMAT = "EEE MMM dd HH:mma z";
	
	/**
	 * Pattern to extract post-id from tag-error-<b>#post-id</b>
	 */
	private static final Pattern PATTERN_TO_EXTRACT_POST_ID = Pattern.compile("tag-error-(\\d+)");
	
	/**
	 * Pattern to extract post-id & id from an action url in slideshow page
	 * Ex: /blog/popup_slideshow.html?p=859&id=n75YJ78_fL5JcEVFlIE1
	 */
	private static final Pattern PATTERN_TO_EXTRACT_POST_ID_AND_ID = Pattern.compile(".*p=(\\d+)&id=(.*)");
	
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
				// Again, XPath evaluation is not thread-safe
				synchronized (expression) {
					return expression.evaluate(item, returnType);
				}
			}
			catch (XPathExpressionException e) {
				logger.debug("Error", e);
			}
			return null;
		}
	}
	
	private static class YahooBlogKey {
		/**
		 * Start from beginning
		 */
		static XPathKey YMGL_PROFILE			= new XPathKey("//*[@id=\"ymgl-profile\"]");
		
		/**
		 * Start from {@value #YMGL_PROFILE}
		 */
		static XPathKey PROFILE_NAME			= new XPathKey("DIV/DIV/H2/SPAN[2]");
		static XPathKey PROFILE_ANY_URL			= new XPathKey("DIV/DIV/P/A/@href");
		static XPathKey PROFILE_AVATAR			= new XPathKey("//*[@id=\"user-photos-full\"]/@src");
		
		/**
		 * Start from beginning
		 */
		static XPathKey YMGL_GUESTBOOK			= new XPathKey("//*[@id=\"ymgl-guestbook\"]");		
		static XPathKey GUESTBOOK_TOP_SPAN		= new XPathKey("/HTML/BODY/DIV[2]/DIV/DIV[2]/SPAN[2]/SPAN");
		static XPathKey GUESTBOOK_REDIRECT		= new XPathKey("/HTML/BODY/DIV/DIV/DIV/DIV/DIV/FIELDSET/FORM/INPUT[3]/@value");
		
		/**
		 * Start from {@value #GUESTBOOK_TOP_SPAN}
		 */
		static XPathKey GUESTBOOK_PREV_URL		= new XPathKey("//*[@id=\"num_prev\"]/@href");
		static XPathKey GUESTBOOK_LIMIT			= new XPathKey("/HTML/BODY/DIV[2]/DIV/DIV[2]/SPAN[2]/SPAN/EM[2]/text()");
		
		/**
		 * Start from {@value #YMGL_GUESTBOOK}
		 */
		static XPathKey GUESTBOOK_COMMENTS		= new XPathKey("DIV/DIV[2]");
		
		/**
		 * Start from beginning
		 */
		static XPathKey YMGL_BLOG				= new XPathKey("//*[@id=\"ymgl-blog\"]");

		/**
		 * Start from {@value #YMGL_BLOG}
		 */
		static XPathKey BLOG_DESCRIPTION		= new XPathKey("DIV/DIV/P/text()");
		static XPathKey FIRST_BLOG_ENTRY_URL	= new XPathKey("DIV/DL/DD[1]/DIV[3]/SPAN[2]/A");
		static XPathKey PREVIOUS_BLOG_ENTRY_URL	= new XPathKey("DIV/DL/DD/DIV[3]/P[2]/SPAN[2]/A/@href");
		static XPathKey BLOG_ENTRY_TAG_ERROR_ID	= new XPathKey("DIV/DL/DD/DIV[3]/DIV/@id");
		static XPathKey BLOG_ENTRY_TITLE		= new XPathKey("DIV/DL/DT");
		static XPathKey BLOG_ENTRY_LR_IMAGE		= new XPathKey("DIV/DL/DD/DIV[1]/IMG/@src");
		static XPathKey BLOG_ENTRY_POPUP_URL	= new XPathKey("DIV/DL/DD/DIV[1]/A/@href");
		static XPathKey BLOG_ENTRY_BODY			= new XPathKey("DIV/DL/DD/DIV[2]");
		static XPathKey BLOG_ENTRY_CREATED_DATE	= new XPathKey("DIV/DL/DD/DIV[3]/P");
		static XPathKey BLOG_ENTRY_TAGS			= new XPathKey("DIV/DL/DD/DIV[3]/SPAN/SPAN/A/text()");
		static XPathKey BLOG_ENTRY_COMMENTS		= new XPathKey("//*[@id=\"comments\"]");

		/**
		 * Start from beginning.
		 */
		static XPathKey BLOG_ENTRY_HR_IMAGE		= new XPathKey("/HTML/BODY/DIV[2]/DIV/DIV/DIV/FORM/DIV/DIV/IMG/@src");
		static XPathKey BLOG_ENTRY_POST_FORM	= new XPathKey("/HTML/BODY/DIV[2]/DIV/DIV/DIV/FORM/@action");
		
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

	public static Author parseYahooProfile(Document doc) {
		Node profile = YahooBlogKey.YMGL_PROFILE.getNode(doc);
		if (profile == null) {
			return new Author();
		}
		String profileName = YahooBlogKey.PROFILE_NAME.getText(profile);
		String yahooURL = YahooBlogKey.PROFILE_ANY_URL.getText(profile);
		String avatar = YahooBlogKey.PROFILE_AVATAR.getText(profile);
		String profileId = YahooBlogAPI.parseProfileId(yahooURL);
		
		return new Author(profileId, profileName, YahooBlogAPI.YAHOO_360_PROFILE_URL + profileId, avatar);
	}

	/**
	 * Parse comments in a guestbook page.
	 * 
	 * @param doc
	 * @return
	 */
	public static YahooBlogEntry parseGuestbook(Document doc) {

		Author author = parseYahooProfile(doc);
		BlogPost blogPost = new BlogPost();
		// 0 is a special post id that mark this entry is guestbook.
		blogPost.setPostId(0);
		blogPost.setAuthor(author);
		blogPost.setContent("");
		blogPost.setTags("");
		blogPost.setTitle("Guestbook");
		blogPost.setDate(new Date());
		
		YahooBlogEntry guestbook = new YahooBlogEntry(blogPost);
		String redirectURL = YahooBlogKey.GUESTBOOK_REDIRECT.getText(doc);
		
		if (!redirectURL.isEmpty()) {
			logger.debug("Session is protected.");
			guestbook.setNextURL(redirectURL);
			return guestbook;
		}
		
		Node span = YahooBlogKey.GUESTBOOK_TOP_SPAN.getNode(doc);
		String limit = YahooBlogKey.GUESTBOOK_LIMIT.getText(span);
		String prevURL = YahooBlogKey.GUESTBOOK_PREV_URL.getText(span);
		
		if (prevURL.isEmpty()) {
			int low = Integer.parseInt(limit) - 9;
			guestbook.setNextURL(YahooBlogAPI.YAHOO_360_GUESTBOOK_URL + author.getUserId() + "?l=" + low + "&u=" + limit + "&mx=" + limit);
		}
		else if (prevURL.indexOf("l=1&u=10") == -1) {
			guestbook.setNextURL(prevURL);
		}
		
		Node node = YahooBlogKey.YMGL_GUESTBOOK.getNode(doc);
		Node comment = YahooBlogKey.GUESTBOOK_COMMENTS.getNode(node);
		if (comment != null) {
			NodeList authors = YahooBlogKey.COMMENTS_AUTHOR.getNodeList(comment);
			NodeList comments = YahooBlogKey.COMMENTS_COMMENT.getNodeList(comment);
			
			String spammer = "";
			String spamContent = "";
			long postId = guestbook.getPost().getPostId();
			try {
				for (int i = 0; i < comments.getLength(); i++) {
					Comment blogComment = parseComment(authors.item(i), comments.item(i));
					blogComment.setPostId(postId);
					if (spammer.equals(blogComment.getAuthor().getName())) {
						if (!spamContent.equals(blogComment.getContent())) {
							guestbook.addComment(blogComment);
						}
					}
					else {
						spammer = blogComment.getAuthor().getName();
						guestbook.addComment(blogComment);
					}
				}
			}
			catch (ParseException e) {
				logger.debug("Error when parsing guestbook", e);
			}
		}
		return guestbook;
	}

	/**
	 * Extract the post-id & blog-id from the action URL, e.g.
	 * /blog/popup_slideshow.html?p=859&id=n75YJ78_fL5JcEVFlIE1
	 * and form a image file name. This text will be put to the
	 * end of the hi resolution image url as a URI fragment.
	 * E.g.
	 * http://path/to/the/image.jpg?crumb#hires_895_n75YJ78_fL5JcEVFlIE1.jpg
	 * 
	 * @param doc
	 * @return
	 */
	public static String[] parsePopupSlideshowForHiResImage(Document doc) {
		String action = YahooBlogKey.BLOG_ENTRY_POST_FORM.getText(doc);
		String imageURL = YahooBlogKey.BLOG_ENTRY_HR_IMAGE.getText(doc);
		Matcher matcher = PATTERN_TO_EXTRACT_POST_ID_AND_ID.matcher(action);
		String postId = "error";
		if (matcher.matches()) {
			postId = matcher.group(1);
		}
		return new String[] {imageURL, postId};
	}

	/**
	 * Parse basic information from a Yahoo blog page.
	 * 
	 * @param doc
	 * @return
	 */
	public static YahooBlog parseYahooBlog(Document doc) {
		Node entry = YahooBlogKey.YMGL_BLOG.getNode(doc);
		if (entry == null) {
			return null;
		}
		NodeList links = YahooBlogKey.FIRST_BLOG_ENTRY_URL.getNodeList(entry);
		int length = links.getLength();
		String firstEntryURL = null;
		for (int index = 0; index < length; index++) {
			Node node = links.item(index);
			String name = node.getFirstChild().getNodeValue();
			if (TEXT_NODE_PERMANENT_LINK.equals(name)) {
				firstEntryURL = node.getAttributes().getNamedItem("href").getNodeValue();
			}
		}
		Author author = parseYahooProfile(doc);
		String description = YahooBlogKey.BLOG_DESCRIPTION.getText(entry);
		YahooBlog blog = new YahooBlog(author, description);
		blog.setFirstEntryURL(firstEntryURL);
		return blog;
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
			Author author = parseYahooProfile(doc);
			blogPost.setAuthor(author);
			blogEntry = new YahooBlogEntry(blogPost);
			blogEntry.setPopupURL(YahooBlogKey.BLOG_ENTRY_POPUP_URL.getText(entry));
			String imageURL = YahooBlogKey.BLOG_ENTRY_LR_IMAGE.getText(entry);
			blogEntry.setImageURL(imageURL);
			
			String nextURL = null;
			Node comment = YahooBlogKey.BLOG_ENTRY_COMMENTS.getNode(doc);
			if (comment != null) {
				NodeList authors = YahooBlogKey.COMMENTS_AUTHOR.getNodeList(comment);
				NodeList comments = YahooBlogKey.COMMENTS_COMMENT.getNodeList(comment);
				
				String spammer = "";
				String spamContent = "";
				int spamCount = 0;
				long postId = blogEntry.getPost().getPostId();
				for (int i = 0; i < comments.getLength(); i++) {
					Comment blogComment = parseComment(authors.item(i), comments.item(i));
					blogComment.setPostId(postId);
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
				// No more entry left, starting with guestbook
				if (nextURL == null || nextURL.length() == 0) {
					nextURL = YahooBlogAPI.YAHOO_360_GUESTBOOK_URL + author.getUserId();
				}
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

		String entryTag = YahooBlogKey.BLOG_ENTRY_TAG_ERROR_ID.getText(ymglBlog);
		Matcher matcher = PATTERN_TO_EXTRACT_POST_ID.matcher(entryTag);
		String postId = "error";
		if (matcher.matches()) {
			postId = matcher.group(1);
		}
		String title = YahooBlogKey.BLOG_ENTRY_TITLE.getText(ymglBlog);
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
		blogPost.setPostId(Long.parseLong(postId));
		blogPost.setTitle(title);
		blogPost.setTags(buffer.toString());
		blogPost.setContent(getRawText(body));
		blogPost.setDate(new SimpleDateFormat(BLOG_DATE_FORMAT).parse(date));
		return blogPost;
	}

	private static Comment parseComment(Node author, Node comment) throws ParseException {
		String text = YahooBlogKey.COMMENTS_COMMENT_TEXT.getText(comment);
		String time = YahooBlogKey.COMMENTS_COMMENT_DATE.getText(comment);

		String author_name = YahooBlogKey.COMMENTS_AUTHOR_NAME.getText(author);
		String photo = YahooBlogKey.COMMENTS_AUTHOR_PHOTO.getText(author);
		String author_url = YahooBlogKey.COMMENTS_AUTHOR_URL.getText(author);
		String userId = YahooBlogAPI.parseProfileId(author_url);
		String author_photo = "";
		
		if (photo != null) {
			author_photo = photo;
		}

		Date date = null;
		try {
			date = new SimpleDateFormat(BLOG_DATE_FORMAT).parse(time);
		}
		catch (ParseException e) {
			date = new SimpleDateFormat(GUESTBOOK_DATE_FORMAT).parse(time);
		}
		return new Comment(new Author(userId, author_name, author_url, author_photo), text, date);
	}
	
	private static String getRawText(Node node) {
		StringWriter writer = new StringWriter();
		HTMLNodeBuilder builder = new HTMLNodeBuilder(writer);
		try {
			builder.serialize(node, false);
		}
		catch (IOException e) {
			logger.debug("Error", e);
		}
		return writer.getBuffer().toString();
	}

	public static void main(String[] args) throws IOException {
		blogEntry();
	}

	private static void blogEntry() throws FileNotFoundException {
		YahooBlogContentHandler contentHandler = new YahooBlogContentHandler();
		FileInputStream inputStream = new FileInputStream("D:/Temp/Workspaces/w1/instcode/entry/entry-2035.html");
		WebpageContent webContent = new WebpageContent(inputStream, "utf-8");
		DomTreeContent content = (DomTreeContent)contentHandler.handle(webContent);
	
		YahooBlog yahooBlog = YahooBlogUtil.parseYahooBlog(content.getDocument());
		System.out.println(yahooBlog.getAuthor());
		System.out.println(yahooBlog.getFirstEntryURL());

		//YahooBlogEntry entry = parseGuestbook(content.getDocument());
		YahooBlogEntry entry = YahooBlogUtil.parseEntry(content.getDocument());
		BlogPost post = entry.getPost();
		System.out.println(
				"Link: " + entry.getNextURL() +
				"\nEntry: " + post.getPostId() +
				"\nBlog: " + post.getTitle() +
				"\nPopup: " + entry.getPopupURL() +
				"\nImage: " + entry.getImageURL() +
				"\nBody: " + post.getContent() +
				"\nTags: " + post.getTags() +
				"\nDate: " + post.getDate());
		List<Comment> comments = entry.getComments();
		for (Comment comment : comments) {
			logger.debug(comment);
		}
	}
}
