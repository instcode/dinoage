/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.impl.content.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.handler.ContentHandler;
import org.ddth.http.impl.content.DomTreeContent;
import org.ddth.http.impl.content.WebpageContent;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WebpageContentHandler implements ContentHandler {
	private Logger logger = Logger.getLogger(WebpageContentHandler.class);
	
	@Override
	public Content<?> handle(Content<?> content) {
		DomTreeContent domTreeContent = null;
		try {
			WebpageContent webpage = (WebpageContent)content;
			byte[] buffer = consume(webpage.getContent());
			ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer);			
			Document doc = parse(byteStream, webpage.getCharset());
			byteStream.reset();
			domTreeContent = new DomTreeContent(doc);
			domTreeContent.setContent(byteStream);
		}
		catch (Exception e) {
			logger.error(e);
		}
		return domTreeContent;
	}
	
	private Document parse(InputStream inputStream, String encoding) {
		DOMParser parser = new DOMParser();
		InputSource inputSource = new InputSource(inputStream);
		inputSource.setEncoding(encoding);
		Document doc = null;
		try {
			parser.parse(inputSource);
			doc = parser.getDocument();
		}
		catch (SAXException e) {
			logger.error(e);
		}
		catch (IOException e) {
			logger.error(e);
		}
		return doc;
	}
	
	private byte[] consume(InputStream inputStream) {
		ByteArrayOutputStream savedBytes = new ByteArrayOutputStream(64000);
		try {
			byte[] buffer = new byte[256];
			int bytesread = 0;
			do {
				bytesread = inputStream.read(buffer, 0, 256);
				if (bytesread > 0) {
					savedBytes.write(buffer, 0, bytesread);
				}
			} while (bytesread > 0);
		}
		catch (IOException e) {
			logger.error(e);
		}
		return savedBytes.toByteArray();
	}
}
