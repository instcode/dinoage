/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.impl.content.handler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

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
			InputStream byteStream = consume(webpage.getContent(), webpage.getCharset());
			Document doc = parse(byteStream, webpage.getCharset());
			byteStream.reset();
			domTreeContent = new DomTreeContent(doc);
			domTreeContent.setContent(byteStream);
		}
		catch (Exception e) {
			logger.error("Error", e);
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
			//parser.setFeature("http://cyberneko.org/html/features/report-errors", true);
			parser.parse(inputSource);
			doc = parser.getDocument();
		}
		catch (SAXException e) {
			logger.error("Error", e);
		}
		catch (IOException e) {
			logger.error("Error", e);
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
	 * 		An {@link InputStream}
	 * @param charset
	 * 		The charset encoding of the given stream
	 * @return
	 * 		An markable {@link InputStream}.
	 */
	protected ByteArrayInputStream consume(InputStream inputStream, String charset) throws UnsupportedEncodingException {
		ByteArrayOutputStream savedBytes = new ByteArrayOutputStream(64000) {
			@Override
			public synchronized byte[] toByteArray() {
				// Don't have to copy the buffer again and again. However, remember
				// that the buffer length is different from the stream buffer size.
				return buf;
			}
		};
		OutputStreamWriter writer = new OutputStreamWriter(savedBytes, charset);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (savedBytes.size() > 16384) {
					writer.flush();
				}
				line = filter(line);
				writer.write(line);
				writer.append('\n');
			}
		}
		catch (IOException e) {
			logger.error("Error", e);
		}
		finally {
			try {
				writer.close();
			}
			catch (IOException e) {
			}
		}
		
		return new ByteArrayInputStream(savedBytes.toByteArray(), 0, savedBytes.size());
	}

	/**
	 * Apply filtering to the given input.<br>
	 * <br>
	 * Subclass might override this method and will have a chance to modify the
	 * input before putting it to the buffer.<br>
	 * <br>
	 * 
	 * @param input
	 * 		A line of text from the original HTML document.
	 * @return
	 * 		The line output after modifying.
	 */
	protected String filter(String input) {
		return input;
	}
}
