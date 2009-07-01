/****************************************************
 * $Project: DinoAge
 * $Date:: Mar 21, 2008
 * $Revision: 
 * $Author:: khoanguyen
 * $Comment::
 **************************************************/
package org.ddth.blogging;

public class Author {
	private String userId;
	private String name;
	private String email;
	private String url;
	private String avatar;

	public Author() {
		this("", "", "", "");
	}
	
	public Author(String userId, String name, String url, String avatar) {
		setUserId(userId);
		setName(name);
		setUrl(url);
		setAvatar(avatar);
		setEmail("");
	}

	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	@Override
	public String toString() {
		return userId + "/" + name + "/" + url + "/" + avatar;
	}
}
