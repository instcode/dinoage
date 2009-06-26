/****************************************************
 * $Project: DinoAge
 * $Date:: Mar 21, 2008
 * $Revision: 
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.dinoage.data;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.blogging.Author;
import org.ddth.blogging.Blog;
import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;
import org.ddth.dinoage.data.exception.QueryDataException;
import org.ddth.dinoage.data.exception.UpdateDataException;

/**
 * @author khoa.nguyen
 *
 */
public class DataManager {
	private static Log logger = LogFactory.getLog(DataManager.class);
	
	private DataProvider provider = new DefaultDataProvider();
	private static final DataManager instance = new DataManager();

	public static DataManager getInstance() {
		return instance;
	}
	
	public void createAuthor(Author author) {
		try {
			provider.createAuthor(author);
		} catch (UpdateDataException e) {
			logger.error("Cannot create new author", e);
		}
	}

	public void createBlog(Blog blog) {
		try {
			provider.createBlog(blog);
		} catch (UpdateDataException e) {
			logger.error("Cannot create blog", e);
		}
	}

	public void createEntry(String blogId, Entry entry) {
		try {
			provider.createEntry(blogId, entry);
		} catch (UpdateDataException e) {
			logger.error("Cannot create entry", e);
		}
	}

	public void createComment(long entryId, Comment comment) {
		try {
			provider.createComment(entryId, comment);
		} catch (UpdateDataException e) {
			logger.error("Cannot create comment", e);
		}
	}

	public Author getAuthor(String userId) {
		try {
			return provider.getAuthor(userId);
		}
		catch (QueryDataException e) {
			logger.error("Cannot load author", e);
		}
		return null;
	}

	public Blog getBlog(String blogId) {
		try {
			return provider.getBlog(blogId);
		} catch (QueryDataException e) {
			logger.error("Cannot load blog", e);
		}
		return null;
	}

	public List<Comment> getComments(long entryId) {
		try {
			return provider.getComments(entryId);
		} catch (QueryDataException e) {
			logger.error("Cannot load comments", e);
		}
		return null;
	}

	public List<Entry> getEntries(String blogId) {
		try {
			return provider.getEntries(blogId);
		} catch (QueryDataException e) {
			logger.error("Cannot load entries", e);
		}
		return null;
	}
}
