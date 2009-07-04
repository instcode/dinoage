/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 3:35:11 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.google;

import java.io.IOException;
import java.net.URL;
import java.util.TimeZone;

import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;
import org.ddth.blogging.api.BasicBlogAPI;

import com.google.gdata.client.GoogleService;
import com.google.gdata.data.Category;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Feed;
import com.google.gdata.data.Person;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class BloggerAPI extends BasicBlogAPI {
	private static final String BLOGGER_POST_AUTHOR = "Post author";
	private static final String URI_BLOGGER_NAMESPACE = "http://www.blogger.com/atom/ns#";
	private static final String METAFEED_URL = "http://www.blogger.com/feeds/default/blogs";
	private static final String FEED_URI_BASE = "http://www.blogger.com/feeds";
	private static final String POSTS_FEED_URI_SUFFIX = "/posts/default";
	private static final String COMMENTS_FEED_URI_SUFFIX = "/comments/default";

	private Boolean isDraft = Boolean.FALSE;
	private GoogleService service = new GoogleService("blogger", "");

	@Override
	public void setup(String blogURL, String author, String password) {
		try {
			service.setUserCredentials(author, password);
			// Get the blogId from the meta-feed.
			String blogId = getBlogId(service);
			String feedURL = FEED_URI_BASE + "/" + blogId;
			super.setup(feedURL, author, password);
		}
		catch (AuthenticationException e) {
			e.printStackTrace();
		}
		catch (ServiceException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getBlogId(GoogleService service) throws ServiceException, IOException {
		final URL feedUrl = new URL(METAFEED_URL);
		Feed resultFeed = service.getFeed(feedUrl, Feed.class);

		// If the user has a blog then return the id (which comes after 'blog-')
		if (resultFeed.getEntries().size() > 0) {
			com.google.gdata.data.Entry entry = resultFeed.getEntries().get(0);
			return entry.getId().split("blog-")[1];
		}
		throw new IOException("User has no blogs!");
	}

	public boolean createEntry(Entry entry) {
		boolean success = false; 
		com.google.gdata.data.Entry blogEntry = new com.google.gdata.data.Entry();
		blogEntry.setTitle(new PlainTextConstruct(entry.getPost().getTitle()));
		blogEntry.setContent(new PlainTextConstruct(entry.getPost().getContent()));
		blogEntry.setDraft(isDraft);
		blogEntry.setPublished(new DateTime(entry.getPost().getDate(), TimeZone.getDefault()));
		
		Person author = new Person(BLOGGER_POST_AUTHOR, null, getAuthor());
		blogEntry.getAuthors().add(author);
		
		Category category = new Category();
		category.setScheme(URI_BLOGGER_NAMESPACE);
		category.setTerm(entry.getPost().getTags());
		blogEntry.getCategories().add(category);
		
		// Ask the service to insert the new entry
		try {
			URL postUrl = new URL(getBlogURL() + POSTS_FEED_URI_SUFFIX);
			if (service.insert(postUrl, blogEntry) != null) {
				success = true;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ServiceException e) {
			e.printStackTrace();
		}
		return success;
	}

	public boolean createComment(Comment comment) {
		// Build the comment feed URI
		String commentsFeedUri = getBlogURL() + "/" + comment.getPostId() + COMMENTS_FEED_URI_SUFFIX;
		boolean success = false;
		try {
			URL feedUrl = new URL(commentsFeedUri);
			// Create a new entry for the comment and submit it to the GoogleService
			com.google.gdata.data.Entry entry = new com.google.gdata.data.Entry();
			entry.setContent(new PlainTextConstruct(comment.getContent()));
			if (service.insert(feedUrl, entry) != null) {
				success = true;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ServiceException e) {
			e.printStackTrace();
		}
		return success;
	}
}
