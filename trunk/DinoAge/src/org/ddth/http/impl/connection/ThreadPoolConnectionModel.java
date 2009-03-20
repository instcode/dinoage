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
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
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
import org.apache.log4j.Logger;
import org.ddth.http.core.ConnectionEvent;
import org.ddth.http.core.ConnectionListener;
import org.ddth.http.core.connection.ConnectionModel;
import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.RequestFuture;
import org.ddth.http.core.connection.Response;
import org.ddth.http.impl.content.WebpageContent;

public class ThreadPoolConnectionModel implements ConnectionModel {
	private static final Properties props = new Properties();
	private static final String NUMBER_OF_CONNECTIONS_PER_ROUTE = "connection.route";
	private static final String CONNECTION_TIME_OUT = "connection.timeout";
	private static final String NUMBER_OF_CONCURRENT_CONNECTIONS = "connection.concurrent";
	
	static {
		props.put(NUMBER_OF_CONNECTIONS_PER_ROUTE, Integer.valueOf(4));
		props.put(NUMBER_OF_CONCURRENT_CONNECTIONS, Integer.valueOf(2));
		props.put(CONNECTION_TIME_OUT, new Long(1000*60*30));
	}
	
	private Logger logger = Logger.getLogger(ThreadPoolConnectionModel.class);
	private HttpClient httpClient;
	private ScheduledExecutorService executor;

	private ConnectionListener monitor;
	
	public ThreadPoolConnectionModel(ConnectionListener listener) {
		monitor = listener;
		httpClient = createHttpClient();
	}

	@Override
	public void open() {
		executor = Executors.newScheduledThreadPool(((Integer)props.get(NUMBER_OF_CONCURRENT_CONNECTIONS)).intValue());
	}

	@Override
	public boolean running() {
		return executor != null;
	}
	
	@Override
	public void close() {
		executor.shutdown();
		try {
			executor.awaitTermination(30, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
		}
		if (!executor.isShutdown()) {
			executor.shutdownNow();
		}
		executor = null;
	}

	@Override
	public RequestFuture sendRequest(final Request request) {
		final Future<Response> future = executor.submit(new Callable<Response>() {
			@Override
			public Response call() throws Exception {
				return request(request);
			}
		});
		
		return new RequestFuture(future);
	}

	public void setup(CookieStore cookieStore) {
		((DefaultHttpClient)httpClient).setCookieStore(cookieStore);
	}

	private Response request(final Request request) {
		HttpEntity entity = null;
		Response response = null;
		try {
			monitor.notifyRequesting(new ConnectionEvent(request));
			HttpUriRequest httpRequest = createHttpRequest(request);
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			entity = httpResponse.getEntity();
			if (entity != null) {
				InputStream inputStream = entity.getContent(); 
				if (entity.getContentEncoding() != null && "gzip".equals(entity.getContentEncoding().getValue())) {
					// Wrap content with GZIP input stream
					inputStream = new GZIPInputStream(inputStream);
				}
				String charset = entity.getContentType().getValue().split("=")[1];
				response = new Response(new WebpageContent(inputStream, charset));
				monitor.notifyResponding(new ConnectionEvent(request, response));
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
				catch (IOException e) {
					logger.debug(e);
				}
			}
			monitor.notifyFinished(new ConnectionEvent(request));
		}
		return response;
	}

	private final HttpClient createHttpClient() {
		SchemeRegistry supportedSchemes = new SchemeRegistry();

		supportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		supportedSchemes.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setUserAgent(params, "Mozilla/5.0");
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);
		
		ConnManagerParams.setMaxTotalConnections(params, ((Integer)props.get(NUMBER_OF_CONCURRENT_CONNECTIONS)).intValue());
		ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRoute() {
			private final int connectionCount = ((Integer)props.get(NUMBER_OF_CONNECTIONS_PER_ROUTE)).intValue();
			
			@Override
			public int getMaxForRoute(HttpRoute route) {
				return connectionCount;
			}
		});
		ConnManagerParams.setTimeout(params, ((Long)props.get(CONNECTION_TIME_OUT)).longValue());

		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, supportedSchemes);
		DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		return httpClient;
	}

	private HttpUriRequest createHttpRequest(final Request request) throws UnsupportedEncodingException {
		HttpUriRequest httpRequest = null;
		if (request.getParameters() != null) {
			HttpPost httpPost = new HttpPost(request.getURL());
			List <NameValuePair> nvps = new ArrayList<NameValuePair>();
			
			Map<String, String> parameters = request.getParameters();
			Iterator<String> iterator = parameters.keySet().iterator();
			while (iterator.hasNext()) {
				String parameter = iterator.next();
				String value = parameters.get(parameter);
				nvps.add(new BasicNameValuePair(parameter, value));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			httpRequest = httpPost;
		}
		else {
			HttpGet httpGet = new HttpGet(request.getURL());
			httpRequest = httpGet;
		}
		
		// Prefer GZIP for optimizing bandwidth
		httpRequest.addHeader("Referer", request.getURL());
		httpRequest.addHeader("Accept-Encoding", "gzip");
		printHeader(httpRequest.getAllHeaders());
		return httpRequest;
	}

	private void printHeader(Header[] headers) {
		logger.debug("----------------------------------------");
		for (int i = 0; i < headers.length; i++) {
			logger.debug(headers[i]);
		}
		logger.debug("----------------------------------------");
	}
}