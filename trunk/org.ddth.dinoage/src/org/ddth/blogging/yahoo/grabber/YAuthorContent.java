package org.ddth.blogging.yahoo.grabber;

import org.ddth.blogging.Author;
import org.ddth.http.impl.content.DomTreeContent;

public class YAuthorContent extends DomTreeContent {

	private Author author;
	
	public YAuthorContent(DomTreeContent content, Author author) {
		super(content.getContent(), content.getDocument());
		this.author = author;
	}

	public Author getAuthor() {
		return author;
	}
}
