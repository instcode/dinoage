package org.ddth.http.impl.content;

import java.io.InputStream;

import org.ddth.http.core.content.ContentAdapter;
import org.w3c.dom.Document;

public class DomTreeContent extends ContentAdapter<InputStream> {

	private Document node;
	
	public DomTreeContent(Document doc) {
		node = doc;
	}
	
	public Document getDocument() {
		return node;
	}
}
