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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cyberneko.html.parsers.DOMParser;
import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.handler.ContentHandler;
import org.ddth.http.impl.content.DomTreeContent;
import org.ddth.http.impl.content.WebpageContent;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A {@link ContentHandler} for a normal web page. It takes the
 * {@link WebpageContent} as an input content and produces the
 * {@link DomTreeContent} object as an output.<br>
 * <br>
 * The input stream within the output content is a markable/resetable
 * {@link InputStream}. It means you could reset its position to the
 * beginning and start reading it over again. This might be useful in
 * case you want to serialize the stream into an external file.<br>
 * <p> 
 * @author khoa.nguyen
 *
 */
public class WebpageContentHandler implements ContentHandler {
	private Log logger = LogFactory.getLog(WebpageContentHandler.class);
	
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
	
	/**
	 * Parse the given input stream to a DOM tree with the
	 * specific encoding. 
	 * 
	 * @param inputStream
	 * 		The inputStream to be read.
	 * @param encoding
	 * 		The selective encoding of the given stream.
	 * @return
	 */
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
	
	/**
	 * Consume the input stream buffer and store it to an on-memory buffer.<br>
	 * <br>
	 * In fact, this code cannot handle universal cases because it reads all the
	 * streaming data into a buffer at once. If the buffer is large enough (or
	 * lack of memory), the {@link OutOfMemoryError} will be thrown immediately.<br>
	 * <br>
	 * However, usual web page size always has lower than 1MB, and it's no
	 * problem to read all the data into a {@link ByteArrayOutputStream} object.<br>
	 * <br>
	 * Besides, the initial buffer size is only ~64KB but it can expand to any
	 * size provide that you have enough memory for it :D.. However, in case you
	 * know that your web page data always be around, e.g. 70KB or more, you
	 * should modify this value to that higher value so it doesn't have to
	 * re-copy a whole array whenever it doubles the size of the buffer.<br>
	 * <br>
	 * 
	 * @param inputStream
	 * @return
	 */
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
			logger.error("Error", e);
		}
		return savedBytes.toByteArray();
	}
}
