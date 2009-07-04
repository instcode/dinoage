/****************************************************
 * $Project: DinoAge
 * $Date:: Mar 21, 2008
 * $Revision: 
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.blogging.yahoo.grabber;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ddth.blogging.Author;
import org.ddth.blogging.Blog;
import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;
import org.ddth.blogging.yahoo.YahooBlog;
import org.ddth.blogging.yahoo.YahooBlogEntry;
import org.ddth.blogging.yahoo.YahooBlogUtil;
import org.ddth.blogging.yahoo.grabber.handler.YahooBlogContentHandler;
import org.ddth.dinoage.data.DataProvider;
import org.ddth.dinoage.data.exception.QueryDataException;
import org.ddth.dinoage.data.exception.UpdateDataException;
import org.ddth.http.impl.content.DomTreeContent;
import org.ddth.http.impl.content.WebpageContent;
import org.w3c.dom.Document;

/**
 * @author khoa.nguyen
 *
 */
class YRawDataProvider implements DataProvider {
	
	private YahooProfile profile;
	private static final String ENTRY = YahooPersistence.ENTRY;
	private static final String ENTRY_LIST = ENTRY + "-" + YahooPersistence.SUFFIX_LIST + ".html";
	
	public YRawDataProvider(YahooProfile profile) {
		this.profile = profile;
	}
	
	public void createAuthor(Author author) throws UpdateDataException {
		throw new UpdateDataException("Unimplemented.");
	}

	public void createBlog(Blog blog) throws UpdateDataException {
		throw new UpdateDataException("Unimplemented.");
	}

	public void createComment(long entryId, Comment comment)
			throws UpdateDataException {
		throw new UpdateDataException("Unimplemented.");
	}

	public void createEntry(String blogId, Entry entry)
			throws UpdateDataException {
		throw new UpdateDataException("Unimplemented.");
	}

	private Document getDocument(File file) throws IOException {
		YahooBlogContentHandler contentHandler = new YahooBlogContentHandler();
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			WebpageContent webContent = new WebpageContent(inputStream, "utf-8");
			DomTreeContent content = (DomTreeContent)contentHandler.handle(webContent);
			return content.getDocument();
		}
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	public Author getAuthor(String userId) throws QueryDataException {
		try {
			Document doc = getDocument(new File(profile.getBlogFolder(), ENTRY_LIST));
			Author author = YahooBlogUtil.parseYahooProfile(doc);
			if (!userId.equals(author.getUserId())) {
				throw new QueryDataException("This provider only supports querying the author of a blog.");
			}
			return author;
		}
		catch (IOException e) {
			throw new QueryDataException("Error", e);
		}
	}

	public Blog getBlog(String blogId) throws QueryDataException {
		try {
			Document doc = getDocument(new File(profile.getBlogFolder(), ENTRY_LIST));
			YahooBlog blog = YahooBlogUtil.parseYahooBlog(doc);
			profile.add(blog);
			return blog;
		}
		catch (IOException e) {
			throw new QueryDataException("Error", e);
		}
	}

	public List<Comment> getComments(long entryId) throws QueryDataException {
		throw new QueryDataException("Unimplemented.");
	}

	public List<Entry> getEntries(String blogId) throws QueryDataException {
		File blogFolder = profile.getBlogFolder();
		// A blog entry which has more than 50 comments will have several
		// entry files. They were named as "entry-<post-id>-<index>.html".
		// In order to parse the post & comments for this entry, we use a
		// map to group all entry files which have the same post-id and
		// store the parsed entry for later usage.
		final Map<Long, Object> groupEntries = new HashMap<Long, Object>();

		File[] files = blogFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// Inline parsing for guestbook
				if (name.startsWith(YahooPersistence.GUESTBOOK)) {
					Document doc = null;
					try {
						doc = getDocument(new File(dir, name));
						YahooBlogEntry entry = YahooBlogUtil.parseGuestbook(doc);
						profile.add(entry);
					}
					catch (IOException e) {
					}
					return false;
				}
				String[] tokens = name.split("-");
				if (tokens.length == 3) {
					try {
						Long postId = new Long(tokens[1]);
						groupEntries.put(postId, new Object());
					}
					catch (NumberFormatException e) {
					}
				}
				// Make sure we don't mess with entry-list.html
				return name.startsWith(ENTRY) && name.indexOf(YahooPersistence.SUFFIX_LIST) == -1;
			}
		});
		List<Entry> entries = new ArrayList<Entry>();
		try {
			for (File file : files) {
				if (Thread.currentThread().isInterrupted()) {
					// This task consumes time and it's normally executed
					// in a thread. To be nice to the caller, it should
					// check for interrupted flag every step to make
					// decision on whether continue running or not.
					break;
				}
				Document doc = getDocument(file);
				YahooBlogEntry entry = null;
				entry = YahooBlogUtil.parseEntry(doc);
				Long postId = new Long(entry.getPost().getPostId());
				Object data = groupEntries.get(postId);
				if (data instanceof Entry) {
					// If the parsed entry has been existed, we only
					// need to add its comments to the original entry
					((Entry) data).getComments().addAll(entry.getComments());
				}
				else {
					// Check if this entry has several entry files...
					if (data != null) {
						groupEntries.put(postId, entry);
					}
					entries.add(entry);
					profile.add(entry);
				}
			}
			return entries;
		}
		catch (IOException e) {
			throw new QueryDataException("Error when parsing entry", e);
		}
	}
}
