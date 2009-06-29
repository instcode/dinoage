/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 3:35:11 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.blogging.wordpress;

import java.util.HashMap;
import java.util.Map;

import org.ddth.blogging.Comment;
import org.ddth.blogging.Entry;
import org.ddth.blogging.api.BasicBlogAPI;

import redstone.xmlrpc.XmlRpcClient;

public class WordpressBlogAPI extends BasicBlogAPI {
	private static final String WORDPRESS_BLOG_ID = "1";

	// XML-RPC methods are supported by Wordpress  
	private static final String MW_NEW_POST_METHOD_NAME = "metaWeblog.newPost";
	private static final String WP_NEW_CATEGORY_METHOD_NAME = "wp.newCategory";
	
	private Boolean publish = Boolean.TRUE;
	private String category = "yahoo";
	
	@Override
	public void setup(String author, String password, String blogURL) {
		super.setup(author, password, blogURL);
		createCategory(category);
	}
	
	public int createCategory(String category) {
		int categoryId = 0;
		try {
			XmlRpcClient client = new XmlRpcClient(getBlogURL(), true);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", category);
			Object[] params = new Object[] { WORDPRESS_BLOG_ID, getAuthor(), getPassword(), map};
			Object value = client.invoke(WP_NEW_CATEGORY_METHOD_NAME, params);
			categoryId = Integer.parseInt(value.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return categoryId;
	}
	
	public boolean createEntry(Entry entry) {
		boolean success = false;
		try {
			XmlRpcClient client = new XmlRpcClient(getBlogURL(), true);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", entry.getPost().getTitle());
			map.put("description", entry.getPost().getContent());
			map.put("categories", new String[] {category});
			map.put("mt_keywords", entry.getPost().getTags());
			map.put("dateCreated", entry.getPost().getDate());

			Object[] params = new Object[] { WORDPRESS_BLOG_ID, getAuthor(), getPassword(), map, publish };

			// Make our method call
			Object postId = client.invoke(MW_NEW_POST_METHOD_NAME, params);
			if (postId != null) {
				success = true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}

	public boolean createComment(Comment comment) {
		throw new UnsupportedOperationException("Unsupported operation");
	}
}
