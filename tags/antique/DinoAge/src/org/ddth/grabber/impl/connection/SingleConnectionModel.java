/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.grabber.impl.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.ddth.grabber.core.connection.ConnectionModel;
import org.ddth.grabber.core.connection.Request;
import org.ddth.grabber.core.connection.Session;
import org.ddth.grabber.core.handler.ConnectionListener;
import org.ddth.grabber.core.handler.Processor;

public class SingleConnectionModel implements ConnectionModel {

	/**
	 * Sub-class use for directly requesting
	 */
	private HttpClient httpClient;
	
	private Log logger = LogFactory.getLog(Session.class);
	private List<ConnectionListener> listeners;
	
	public SingleConnectionModel(HttpClient httpClient) {
		this.httpClient = httpClient;
		this.listeners = new ArrayList<ConnectionListener>();
	}

	public void sendRequest(final Request request) {
		String sURL = request.getURL();
		Processor contentHandler = request.getProcessor();
	
		HttpEntity entity = null;
		boolean isSuccess = false;
		try {
			HttpGet httpGet = new HttpGet(sURL);
			// Prefer gzip for optimizing bandwidth
			httpGet.addHeader("Accept-Encoding", "gzip");
			printHeader(httpGet.getAllHeaders());
			notifyRequesting(sURL);
			
			HttpResponse rsp = httpClient.execute(httpGet);
			entity = rsp.getEntity();
			if (entity != null) {
				if (entity.getContentEncoding() != null && "gzip".equals(entity.getContentEncoding().getValue())) {
					isSuccess = contentHandler.handleContent(new GZIPInputStream(entity.getContent()));
				}
				else {
					isSuccess = contentHandler.handleContent(entity.getContent());
				}
			}
		}
		catch (Exception e) {
			logger.debug(e);
		}
		finally {
			// If we could be sure that the stream of the entity has been
			// closed, we wouldn't need this code to release the connection.
			// If there is no entity, the connection is already released
			if (entity != null && !entity.isStreaming()) {
				try {
					// Release connection gracefully
					entity.consumeContent();
				}
				catch (Exception e) {
					logger.debug(e);
				}
			}
			notifyFinished(sURL, isSuccess);
		}
	}

	private void printHeader(Header[] headers) {
		logger.debug("----------------------------------------");
		for (int i = 0; i < headers.length; i++) {
			logger.debug(headers[i]);
		}
		logger.debug("----------------------------------------");
	}

	private void notifyRequesting(String sURL) {
		for (ConnectionListener listener : listeners) {
			listener.notifyRequesting(sURL);
		}
	}
	
	private void notifyFinished(String sURL, boolean isSuccess) {
		for (ConnectionListener listener : listeners) {
			listener.notifyFinished(sURL, isSuccess);
		}
	}
	
	public void registerConnectionListener(ConnectionListener listener) {
		listeners.add(listener);	
	}

	public void unregisterConnectionListener(ConnectionListener listener) {
		listeners.remove(listener);
	}
}