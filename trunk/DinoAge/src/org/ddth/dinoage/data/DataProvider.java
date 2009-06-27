/****************************************************
 * $Project: DinoAge
 * $Date:: Mar 21, 2008
 * $Revision: 
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.dinoage.data;

import java.util.List;

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
public interface DataProvider {
	
	public void createAuthor(Author author) throws UpdateDataException;

	public void createBlog(Blog blog) throws UpdateDataException;
	
	public void createEntry(String blogId, Entry entry) throws UpdateDataException;
	
	public void createComment(long entryId, Comment comment) throws UpdateDataException;

	public Author getAuthor(String userId) throws QueryDataException;

	public Blog getBlog(String blogId) throws QueryDataException;
	
	public List<Entry> getEntries(String blogId) throws QueryDataException;
	
	public List<Comment> getComments(long entryId) throws QueryDataException;
}
