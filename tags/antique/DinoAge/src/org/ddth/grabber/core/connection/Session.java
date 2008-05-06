/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.grabber.core.connection;

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
import org.ddth.grabber.core.handler.SessionListener;

public class Session<T extends State> implements Runnable {
	private static final long DELAY_TIME_BETWEEN_TWO_REQUESTS = 1000;

	private Log logger = LogFactory.getLog(Session.class);

	private boolean isRunning;
	private Thread workerThread;
	
	private HttpClient httpClient;
	private T state;
	private ConnectionModel connectionModel;
	private SessionListener listener;

	private RequestFactory<T> requestFactory;

	public Session(String charsetEncoding, CookieStore cookieStore, RequestFactory<T> handlerFactory) {
		this.requestFactory = handlerFactory;
		
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

	public T getState() {
		return state;
	}

	/**
	 * Resume to the given state
	 * @param state
	 */
	public void setState(T state) {
		if (isRunning) {
			throw new IllegalStateException("You have to stop the current state first.");
		}
		this.state = state;
	}

	public void queueRequest(String link) {
		if (state == null) {
			throw new IllegalStateException("Unknown session state");
		}
		Request request = requestFactory.createRequest(link, this);
		if (request != null) {
			state.queue(request);
		}
	}
	
	public void registerSessionListener(SessionListener listener) {
		this.listener = listener;
	}
	
	public void setConnectionModel(ConnectionModel connectionModel) {
		if (connectionModel != null) {
			this.connectionModel = connectionModel;
		}
	}
	
	public ConnectionModel getConnectionModel() {
		return connectionModel;
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
	 * Stop the current worker thread without blocking...
	 * 
	 * @see #stop()
	 */
	public void pause() {
		isRunning = false;
	}
	
	/**
	 * Stop the current worker thread and block until the
	 * thread is completely exited
	 * 
	 * @see #pause()
	 */
	public void stop() {
		pause();
	}

	public void run() {
		if (listener != null) {
			listener.sessionStarted();
		}
		while (isRunning) {
			Request request = state.poll();
			if (request != null) {
				connectionModel.sendRequest(request);
			}
			else {
				logger.debug("Couldn't make '" + request + "' request");
				break;
			}
			try {
				Thread.sleep(DELAY_TIME_BETWEEN_TWO_REQUESTS);
			}
			catch (InterruptedException e) {
			}
		}
		isRunning = false;
		// Wake up all waiting threads on this thread
		if (workerThread != null) {
			workerThread = null;
		}
		if (listener != null) {
			listener.sessionStopped();
		}
	}
}
