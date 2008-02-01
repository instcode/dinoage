/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.grabber.core.connection;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.PlainSocketFactory;
import org.apache.http.conn.Scheme;
import org.apache.http.conn.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.ddth.grabber.core.handler.ConnectionListener;
import org.ddth.grabber.core.handler.NavigationHandler;
import org.ddth.grabber.core.handler.SessionListener;

public class Session implements Runnable, ConnectionListener {
	private static final long DELAY_TIME = 1000;

	private Log logger = LogFactory.getLog(Session.class);
	private HttpClient httpClient;
	private ConnectionModel connectionModel;
	private SessionListener listener;
	private State state;

	private boolean isRunning;
	private Thread workerThread;

	public Session(String charsetEncoding, CookieStore cookieStore) {
		SchemeRegistry supportedSchemes = new SchemeRegistry();

		supportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		supportedSchemes.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setUserAgent(params, "Mozilla/5.0");
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, charsetEncoding);
		HttpProtocolParams.setUseExpectContinue(params, true);

		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, supportedSchemes);
		httpClient = new DefaultHttpClient(ccm, params);
		((DefaultHttpClient)httpClient).setCookieStore(cookieStore);
	}
	
	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void queueRequest(String sURL, NavigationHandler contentHandler) {
		if (state == null) {
			throw new RuntimeException("Unknown session state. Should start/resume the session first.");
		}

		if (!state.getCompletedMap().containsKey(sURL)) {
			if (!state.getOutgoingMap().containsKey(sURL)) {
				logger.info(sURL + "... Queued!");
			}
			state.getOutgoingMap().put(sURL, contentHandler);
		}
	}

	public void setConnectionModel(ConnectionModel connectionModel) {
		if (connectionModel != null) {
			this.connectionModel = connectionModel;
			this.connectionModel.registerConnectionListener(this);
		}
	}
	
	public ConnectionModel getConnectionModel() {
		return connectionModel;
	}

	/**
	 * Resume to the given state
	 * @param state
	 */
	public void resume(State state) {
		if (isRunning) {
			throw new IllegalStateException("You have to stop the current state first.");
		}
		this.state = state;
	}

	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * Start the current worker thread
	 */
	public void start() {
		if (!isRunning && workerThread == null) {
			isRunning = true;
			workerThread = new Thread(this);
			workerThread.setPriority(Thread.MIN_PRIORITY);
			workerThread.start();
		}
	}

	/**
	 * Stop the current worker thread with waiting...
	 * 
	 * @see #stop()
	 */
	public void pause() {
		isRunning = false;
	}

	public void run() {
		if (listener != null) {
			listener.sessionStarted();
		}
		while (isRunning) {
			Iterator<String> links = state.getOutgoingMap().keySet().iterator();
			while (isRunning && links.hasNext()) {
				String sURL = links.next();
				NavigationHandler contentHandler = state.getOutgoingMap().get(sURL);
				state.getCompletedMap().put(sURL, Boolean.FALSE);
				connectionModel.sendRequest(sURL, contentHandler);
				try {
					Thread.sleep(DELAY_TIME);
				}
				catch (InterruptedException e) {
				}
			}
		}
		// Wake up all waiting threads on this thread
		if (workerThread != null) {
			workerThread = null;
		}
		if (listener != null) {
			listener.sessionStopped();
		}
	}

	public void notifyFinished(String sURL, boolean isCompletedWithoutError) {
		logger.debug(sURL + "... Done!");
		if (isCompletedWithoutError) {
			state.getCompletedMap().put(sURL, Boolean.TRUE);
			state.getOutgoingMap().remove(sURL);
		}
		else {
			state.getCompletedMap().remove(sURL);
		}
	}

	public void notifyRequesting(String sURL) {
	}

	public void registerSessionListener(SessionListener listener) {
		this.listener = listener;
	}
}
