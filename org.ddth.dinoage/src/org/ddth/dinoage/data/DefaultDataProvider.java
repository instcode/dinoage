/****************************************************
 * $Project: DinoAge
 * $Date:: Mar 21, 2008
 * $Revision: 
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.dinoage.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ddth.blogging.Author;
import org.ddth.blogging.Blog;
import org.ddth.blogging.BlogPost;
import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;
import org.ddth.blogging.Post;
import org.ddth.dinoage.data.exception.QueryDataException;
import org.ddth.dinoage.data.exception.UpdateDataException;

/**
 * Do not access this provider directly, use
 * {@link org.ddth.dinoage.data.DataManager} instead.<br>
 * 
 * @author khoa.nguyen
 */
class DefaultDataProvider implements DataProvider {
	
	private static final String CREATE_AUTHOR = "INSERT INTO Author (userId, name, url, email, avatar) VALUES (?,?,?,?,?)";
	private static final String CREATE_BLOG = "INSERT INTO Blog (blogId, userId, url, title, description) VALUES (?,?,?,?,?)";
	private static final String CREATE_POST = "INSERT INTO Post (userId, content, creation) VALUES (?,?,?)";
	private static final String CREATE_ENTRY = "INSERT INTO Entry (entryId, blogId, title, postId, tags) VALUES (?,?,?,?,?)";
	private static final String CREATE_COMMENT = "INSERT INTO Comment (entryId, postId) VALUES (?,?)";

	private static final String GET_AUTHOR = "SELECT name, url, email, avatar FROM Author WHERE userId=?";
	private static final String GET_BLOG = "SELECT userid, url, title, description FROM Blog WHERE b.blogId=?";
	private static final String GET_ENTRIES = "SELECT e.entryId, e.title, e.tags, p.postId, p.userId, p.content, p.creation FROM Entry AS e, Post AS p WHERE e.blogId=? AND e.postId=p.postId";
	private static final String GET_COMMENTS = "SELECT c.commentId, p.postId, p.userId, p.content, p.creation FROM Comment AS c, Post AS p WHERE c.entryId=? AND c.postId=p.postId";
	
	private ConnectionManager manager;
	
	public DefaultDataProvider(ConnectionManager manager) {
		this.manager = manager;
	}
	
	public void createAuthor(Author author) throws UpdateDataException {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = manager.getConnection();
			pstmt = con.prepareStatement(CREATE_AUTHOR);
			pstmt.setString(1, author.getUserId());
			pstmt.setString(2, author.getName());
			pstmt.setString(3, author.getUrl());
			pstmt.setString(4, author.getEmail());
			pstmt.setString(5, author.getAvatar());
			pstmt.execute();
		} catch (Exception e) {
			// Leave out duplicate key value
			if (!((SQLException) e).getSQLState().equals("23505")) {
				throw new UpdateDataException(e);
			}
		}
		finally {
			manager.close(pstmt);
		}
	}

	public void createBlog(Blog blog) throws UpdateDataException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = manager.getConnection();
			pstmt = con.prepareStatement(CREATE_BLOG);

			Author author = blog.getAuthors().get(0);
			pstmt.setString(1, blog.getBlogId());
			pstmt.setString(2, author.getUserId());
			pstmt.setString(3, blog.getUrl());
			pstmt.setString(4, blog.getTitle());
			pstmt.setString(5, blog.getDescription());
			pstmt.execute();
		} catch (Exception e) {
			throw new UpdateDataException(e);
		} finally {
			manager.close(pstmt);
		}
	}

	private void createPost(Post post) throws UpdateDataException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = manager.getConnection();
			pstmt = con.prepareStatement(CREATE_POST,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, post.getAuthor().getUserId());
			pstmt.setString(2, post.getContent());
			pstmt.setLong(3, post.getDate().getTime());
			pstmt.execute();
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			generatedKeys.next();
			post.setPostId(generatedKeys.getLong(1));
			
		} catch (Exception e) {
			throw new UpdateDataException(e);
		} finally {
			manager.close(pstmt);
		}
	}

	public void createEntry(String blogId, Entry entry)
			throws UpdateDataException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			createPost(entry.getPost());
			con = manager.getConnection();
			pstmt = con.prepareStatement(CREATE_ENTRY,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, entry.getEntryId());
			pstmt.setString(2, blogId);
			pstmt.setString(3, entry.getPost().getTitle());
			pstmt.setLong(4, entry.getPost().getPostId());
			pstmt.setString(5, entry.getPost().getTags());
			pstmt.execute();
		} catch (Exception e) {
			throw new UpdateDataException(e);
		} finally {
			manager.close(pstmt);
		}
	}

	public void createComment(long entryId, Comment comment)
			throws UpdateDataException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			createPost(comment);
			con = manager.getConnection();
			pstmt = con.prepareStatement(CREATE_COMMENT,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, entryId);
			pstmt.setLong(2, comment.getPostId());
			pstmt.execute();
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			generatedKeys.next();
			comment.setCommentId(generatedKeys.getLong(1));
		} catch (Exception e) {
			throw new UpdateDataException(e);
		} finally {
			manager.close(pstmt);
		}
	}

	public Author getAuthor(String userId) throws QueryDataException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = manager.getConnection();
			pstmt = con.prepareStatement(GET_AUTHOR);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			if (!rs.next()) {
				throw new QueryDataException();
			}
			String name = rs.getString(1);
			String url = rs.getString(2);
			String email = rs.getString(3);
			String avatar = rs.getString(4);
			Author author = new Author(userId, name, url, avatar);
			author.setEmail(email);
			return author;
		} catch (Exception e) {
			throw new QueryDataException(e);
		} finally {
			manager.close(pstmt);
		}
	}
	
	public Blog getBlog(String blogId) throws QueryDataException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = manager.getConnection();
			pstmt = con.prepareStatement(GET_BLOG);
			pstmt.setString(1, blogId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				throw new QueryDataException();
			}
			Blog blog = new Blog();
			blog.setBlogId(blogId);
			blog.addAuthor(getAuthor(rs.getString(1)));
			blog.setUrl(rs.getString(2));
			blog.setTitle(rs.getString(3));
			blog.setDescription(rs.getString(4));
			return blog;
		} catch (Exception e) {
			throw new QueryDataException(e);
		} finally {
			manager.close(pstmt);
		}
	}

	public List<Entry> getEntries(String blogId) throws QueryDataException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = manager.getConnection();
			pstmt = con.prepareStatement(GET_ENTRIES);
			pstmt.setString(1, blogId);
			rs = pstmt.executeQuery();
			List<Entry> entries = new ArrayList<Entry>();
			while (rs.next()) {
				BlogPost post = new BlogPost();
				Entry entry = new Entry(post);
				entry.setEntryId(rs.getLong(1));
				post.setTitle(rs.getString(2));
				post.setTags(rs.getString(3));
				post.setPostId(rs.getLong(4));
				post.setAuthor(getAuthor(rs.getString(5)));
				post.setContent(rs.getString(6));
				post.setDate(new Date(rs.getLong(7)));
				entries.add(entry);
			}
			return entries;
		}
		catch (Exception e) {
			throw new QueryDataException(e);
		}
		finally {
			manager.close(pstmt);
		}
	}

	public List<Comment> getComments(long entryId) throws QueryDataException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = manager.getConnection();
			pstmt = con.prepareStatement(GET_COMMENTS);
			pstmt.setLong(1, entryId);
			rs = pstmt.executeQuery();
			List<Comment> comments = new ArrayList<Comment>();
			while (rs.next()) {
				Comment comment = new Comment(getAuthor(rs.getString(3)), rs.getString(4), new Date(rs.getLong(5)));
				comment.setCommentId(rs.getLong(1));
				comment.setPostId(rs.getLong(2));
				comments.add(comment);
			}
			return comments;
		}
		catch (Exception e) {
			throw new QueryDataException(e);
		}
		finally {
			manager.close(pstmt);
		}
	}
}
