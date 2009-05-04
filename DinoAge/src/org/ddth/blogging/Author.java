package org.ddth.blogging;

public class Author {

	private String name;
	private String url;
	private String avatar;

	public Author(String name, String url, String avatar) {
		setName(name);
		setUrl(url);
		setAvatar(avatar);
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
		return name + "/" + url + "/";
	}
}
