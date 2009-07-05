/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.impl.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.ddth.http.core.ConnectionEvent;
import org.ddth.http.core.ConnectionListener;
import org.ddth.http.core.connection.ConnectionModel;
import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.RequestFuture;
import org.ddth.http.core.connection.Response;
import org.ddth.http.impl.content.WebpageContent;

/**
 * This is an implementation of {@link ConnectionModel} interface uses a
 * threadpool to support non-blocking invocation.<br>
 * <br>
 * It also uses <a href="http://hc.apache.org">HTTPComponent</a> as a compatible
 * browser to virtually surf the web ;-).<br>
 * <br>
 * Every queued request will be executed within a thread in a well scheduled
 * threadpool so I'm sure that all requests will be executed properly.<br>
 * <br>
 * 
 * @author khoa.nguyen
 * 
 */
public class ThreadPoolConnectionModel implements ConnectionModel {
	private static final String FIREFOX_COOKIES_ENABLED = "firefox.cookies";
	private static final String NUMBER_OF_CONNECTIONS_PER_ROUTE = "connection.route";
	private static final String CONNECTION_TIME_OUT = "connection.timeout";
	private static final String NUMBER_OF_CONCURRENT_CONNECTIONS = "connection.concurrent";
	
	private static final Log logger = LogFactory.getLog(ThreadPoolConnectionModel.class);
	private HttpClient httpClient;
	private ScheduledExecutorService executor;

	private ConnectionListener monitor;
	
	/**
	 * Support adding a ConnectionListener during its creation time.
	 * 
	 * @param listener
	 */
	public ThreadPoolConnectionModel(ConnectionListener listener) {
		monitor = listener;
		httpClient = createHttpClient();
	}

	public void open() {
		executor = Executors.newScheduledThreadPool(Integer.parseInt(System.getProperty(NUMBER_OF_CONCURRENT_CONNECTIONS, "4")));
	}

	public boolean running() {
		return executor != null;
	}
	
	public void close() {
		if (running()) {
			executor.shutdown();
			executor = null;
		}
	}

	public RequestFuture sendRequest(final Request request) {
		if (!running()) {
			return null;
		}
		final HttpUriRequest httpRequest = createHttpRequest(request);
		final Future<Response> future = executor.submit(new Callable<Response>() {
			public Response call() throws Exception {
				return request(request, httpRequest);
			}
		});
		
		return new RequestFuture(future) {
			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				httpRequest.abort();
				return super.cancel(mayInterruptIfRunning);
			}
		};
	}

	/**
	 * Send a request and wait for the responding in a blocking way. Also notify
	 * to the monitor every change in every state.
	 * 
	 * @param request
	 *            The original/internal request.
	 * @param httpRequest
	 *            The GET/POST request by Apache HTTPComponent.
	 * @return A Response object which contains every responding data from
	 *         server.
	 */
	private Response request(final Request request, final HttpUriRequest httpRequest) {
		HttpEntity entity = null;
		Response response = null;
		RequestFuture future = null;
		try {
			monitor.notifyEvent(new ConnectionEvent(request));
			//printHeader(httpRequest.getAllHeaders());
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			entity = httpResponse.getEntity();
			if (entity != null) {
				InputStream inputStream = entity.getContent();
				// Check if the body data is actually in GZIP format 
				if (entity.getContentEncoding() != null && "gzip".equals(entity.getContentEncoding().getValue())) {
					// Wrap content with GZIP input stream
					inputStream = new GZIPInputStream(inputStream);
				}
				response = new Response(new WebpageContent(inputStream, "utf-8"));
				monitor.notifyEvent(new ConnectionEvent(ConnectionEvent.RESPONSE_RECEIVED, request, response));
			}
		}
		catch (Exception e) {
			logger.error("Error when processing an http request", e);
			if (e instanceof ConnectTimeoutException) {
				// If there's something wrong with current request
				// which is related to IO (time out, socket error).
				// Request again...
				future = sendRequest(request);
			}
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
				catch (IOException e) {
					logger.error("Error when consuming an http stream", e);
				}
			}
			// Don't fire a finished event if the request
			// is being sent again
			if (future == null) {
				// The request was completed successfully.
				monitor.notifyEvent(new ConnectionEvent(ConnectionEvent.REQUEST_FINISHED, request, response));
			}
		}
		return response;
	}

	/**
	 * Setup all configuration needed for a new client. This will create a
	 * threadsafe client and it can serve multiple requests at a time.
	 * 
	 * @return
	 * 		An apache HttpClient.
	 */
	private final HttpClient createHttpClient() {
		SchemeRegistry supportedSchemes = new SchemeRegistry();

		supportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		supportedSchemes.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		HttpParams params = new BasicHttpParams();
		// Stupid fake client =)) Should grab the incredible long user-agent
		// value from Firefox
		HttpProtocolParams.setUserAgent(params, "Mozilla/5.0");
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);
		
		// Support configuring number of concurrent connections so the bandwidth
		// won't be throttled.
		ConnManagerParams.setMaxTotalConnections(params, Integer.parseInt(System.getProperty(NUMBER_OF_CONCURRENT_CONNECTIONS, "4")));
		ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRoute() {
			private final int connectionCount = Integer.parseInt(System.getProperty(NUMBER_OF_CONNECTIONS_PER_ROUTE, "2"));
			
			public int getMaxForRoute(HttpRoute route) {
				return connectionCount;
			}
		});
		ConnManagerParams.setTimeout(params, Long.parseLong(System.getProperty(CONNECTION_TIME_OUT, "10000")));

		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, supportedSchemes);
		DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);
		// Support configuring proxy... Either simple proxy or SOCK, will check it later :D
		//final HttpHost proxy = new HttpHost("127.0.0.1", 8080, "http");
		//httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		// Load all cookies from Firefox
		if (Boolean.parseBoolean(System.getProperty(FIREFOX_COOKIES_ENABLED, "true"))) {
			httpClient.setCookieStore((new CookiesReader()).readBrowserCookies());
			logger.info("Loaded all cookies from default Firefox profile.");
		}
		return httpClient;
	}

	private HttpUriRequest createHttpRequest(final Request request) {
		HttpUriRequest httpRequest = null;
		if (request.isPostRequest()) {
			HttpPost httpPost = new HttpPost(request.getURL());
			List <NameValuePair> nvps = new ArrayList<NameValuePair>();
			
			Map<String, String> parameters = request.getParameters();
			Iterator<String> iterator = parameters.keySet().iterator();
			while (iterator.hasNext()) {
				String parameter = iterator.next();
				String value = parameters.get(parameter);
				nvps.add(new BasicNameValuePair(parameter, value));
			}
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			}
			catch (UnsupportedEncodingException e) {
				logger.error("Create an HTTP request failed.", e);
			}
			httpRequest = httpPost;
		}
		else {
			HttpGet httpGet = new HttpGet(request.getURL());
			httpRequest = httpGet;
		}
		
		// Prefer GZIP for optimizing bandwidth
		httpRequest.addHeader("Referer", request.getURL());
		httpRequest.addHeader("Accept-Encoding", "gzip");
		return httpRequest;
	}

	protected void printHeader(Header[] headers) {
		logger.debug("----------------------------------------");
		for (int i = 0; i < headers.length; i++) {
			logger.debug(headers[i]);
		}
		logger.debug("----------------------------------------");
	}
}