package org.ddth.blogging.yahoo.grabber;

import org.ddth.blogging.Author;
import org.ddth.http.core.content.ContentAdapter;
import org.ddth.http.impl.content.DomTreeContent;

public class YAuthorContent extends ContentAdapter<DomTreeContent> {

	private Author author;
	
	public YAuthorContent(DomTreeContent content, Author author) {
		setContent(content);
		this.author = author;
	}

	public Author getAuthor() {
		return author;
	}
}
