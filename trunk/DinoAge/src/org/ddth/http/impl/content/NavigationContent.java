package org.ddth.http.impl.content;

import org.ddth.http.core.content.ContentAdapter;

public class NavigationContent extends ContentAdapter<DomTreeContent> {

	private String[] urls;

	public NavigationContent(String[] urls) {
		this.urls = urls;
	}
	
	public String[] getNextURLs() {
		return urls;
	}
}
