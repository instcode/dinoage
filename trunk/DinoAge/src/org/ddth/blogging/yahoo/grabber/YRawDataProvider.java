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
import org.ddth.dinoage.core.DataLoadEvent;
import org.ddth.dinoage.core.DataLoadMonitor;
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
	
	private File profileFolder;
	private DataLoadMonitor monitor;
	private static final String ENTRY_TEXT = YahooPersistence.CATEGORIES[YahooPersistence.BLOG_ENTRY];
	private static final String ENTRY_LIST = ENTRY_TEXT + File.separator + ENTRY_TEXT + "-" + YahooPersistence.SUFFIX_LIST + ".html";
	
	public YRawDataProvider(File profileFolder, DataLoadMonitor monitor) {
		this.profileFolder = profileFolder;
		this.monitor = monitor;
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
			Document doc = getDocument(new File(profileFolder, ENTRY_LIST));
			Author author = YahooBlogUtil.parseYahooProfile(doc);
			if (!userId.equals(author.getUserId())) {
				throw new QueryDataException("This provider only supports querying the author of a blog.");
			}
			monitor.loaded(new DataLoadEvent(author, DataLoadEvent.STEP_LOADED));
			return author;
		}
		catch (IOException e) {
			throw new QueryDataException("Error", e);
		}
	}

	public Blog getBlog(String blogId) throws QueryDataException {
		try {
			Document doc = getDocument(new File(profileFolder, ENTRY_LIST));
			YahooBlog blog = YahooBlogUtil.parseYahooBlog(doc);
			monitor.loaded(new DataLoadEvent(blog, DataLoadEvent.STEP_LOADED));
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
		File entryFolder = new File(profileFolder, ENTRY_TEXT);
		// A blog entry which has more than 50 comments will have several
		// entry files. They were named as "entry-<post-id>-<index>.html".
		// In order to parse the post & comments for this entry, we use a
		// map to group all entry files which have the same post-id and
		// store the parsed entry for later usage.
		final Map<Long, Object> groupEntries = new HashMap<Long, Object>();
		
		File[] files = entryFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String[] tokens = name.split("-");
				if (tokens.length == 3) {
					Long postId = new Long(tokens[1]);
					groupEntries.put(postId, new Object());
				}
				// Make sure we don't mess with entry-list.html
				return name.startsWith(ENTRY_TEXT) && name.indexOf(YahooPersistence.SUFFIX_LIST) == -1;
			}
		});
		List<Entry> entries = new ArrayList<Entry>();
		try {
			for (File file : files) {
				Document doc = getDocument(file);
				YahooBlogEntry entry = YahooBlogUtil.parseEntry(doc);
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
					monitor.loaded(new DataLoadEvent(entry, DataLoadEvent.STEP_LOADED));
				}
			}
			return entries;
		}
		catch (IOException e) {
			throw new QueryDataException("Error when parsing entry", e);
		}
	}
}
