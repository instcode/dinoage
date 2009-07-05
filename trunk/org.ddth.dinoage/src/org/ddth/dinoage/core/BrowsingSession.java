package org.ddth.dinoage.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ddth.http.core.ConnectionEvent;
import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.RequestFuture;
import org.ddth.http.core.connection.Response;
import org.ddth.http.core.content.Content;
import org.ddth.http.core.content.handler.ContentHandler;
import org.ddth.http.core.content.handler.ContentHandlerDispatcher;
import org.ddth.http.impl.ThreadPoolSession;
import org.ddth.http.impl.content.WebpageContent;

public abstract class BrowsingSession extends ThreadPoolSession {

	private Log logger = LogFactory.getLog(BrowsingSession.class);

	protected ConsoleLogger consoleLogger = ConsoleLogger.DEFAULT_CONSOLE_LOGGER;
	protected LocalStorage storage;
	protected RequestStorage requests;
	
	/**
	 * Use one thread to serve cached content in order to not
	 * blocking the UI.
	 */
	private Executor service = Executors.newFixedThreadPool(1);
	private volatile int queued;
	
	public BrowsingSession(ContentHandlerDispatcher dispatcher, LocalStorage storage, RequestStorage requests) {
		super(dispatcher);
		this.storage = storage;
		this.requests = requests;
	}

	/**
	 * Attach a console logger to this session. Has no effect if
	 * the given console logger is null.
	 * 
	 * @param logger logger to be attached with this session.
	 */
	public void attach(ConsoleLogger logger) {
		if (logger == null) {
			return;
		}
		this.consoleLogger = logger;
	}
	
	/**
	 * Restore previous session
	 */	
	public void restore() {
		super.start();
		consoleLogger.println("Session started.");
		for (Request request : requests.getRequests()) {
			queue(request);
		}
	}

	/**
	 * Check if the session is resumable.
	 * @return
	 */
	public boolean isRestorable() {
		return (requests.getRequests().length > 0);
	}

	/**
	 * Process the given content
	 * 
	 * @param content
	 */
	protected abstract void process(Content<?> content);
	
	@Override
	public RequestFuture queue(final Request request) {
		if (!isRunning()) {
			return null;
		}
		requests.putRequest(request);
		final File resource = storage.getLocalResource(request);
		if (resource == null) {
			consoleLogger.println("Downloading " + request.getURL());
			return super.queue(request);
		}
		// Found something in local storage
		final Content<?> content = getContent(request, resource);
		queued++;
		service.execute(new Runnable() {
			public void run() {
				try {
					process(content);
				}
				catch (Exception e) {
					// Something went wrong, we should remove cache
					// and go online to retrieve new content...
					logger.debug("Local resource is invalid.", e);
					request.getParameters().put(LocalStorage.RESOURCE_EXPIRED_ATTR, "");
					queue(request);
				}
				finally {
					queued--;
					notifyEvent(new ConnectionEvent(ConnectionEvent.REQUEST_FINISHED, request, new Response(content)));
				}
			}
		});
		return newRequestFuture(content);
	}
	
	@Override
	protected boolean canFinish() {
		return super.canFinish() && queued == 0;
	}
	
	@Override
	public void shutdown() {
		consoleLogger.println("Session stopped.");
		// Unattach logger
		consoleLogger = null;
		super.shutdown();
	}
	
	@Override
	protected void handle(Request request, Content<?> content) {
		try {
			storage.cacheResource(request, content);
			process(content);
		}
		catch (Exception e) {
			// Something went wrong
			logger.debug("Stopping current session...", e);
			shutdown();
		}
	}

	/**
	 * Load content from the given external file. This is only
	 * invoked when a cache file on external memory is found.
	 *  
	 * @param request
	 * @param cacheFile
	 * @return
	 */
	private Content<?> getContent(Request request, File cacheFile) {
		consoleLogger.println("Cache hit " + request.getURL() + " <==> " + cacheFile);
		logger.info("Found local resource for " + request.getURL() + " in " + cacheFile);
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(cacheFile);
			WebpageContent webContent = new WebpageContent(inputStream, "utf-8");
			ContentHandler handler = dispatcher.findHandler(request);
			return handler.handle(webContent);
		}
		catch (IOException e) {
			logger.debug("Error parsing local resource", e);
		}
		finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				}
				catch (IOException e) {
				}
			}
		}
		return null;
	}

	/**
	 * Wraps content into a {@link RequestFuture} object.
	 * 
	 * @param content
	 * @return
	 */
	private RequestFuture newRequestFuture(Content<?> content) {
		final Response response = new Response(content);
		return new RequestFuture(new Future<Response>() {
			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public Response get() throws InterruptedException,
					ExecutionException {
				return response;
			}

			@Override
			public Response get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				return response;
			}

			@Override
			public boolean isCancelled() {
				return false;
			}

			@Override
			public boolean isDone() {
				return true;
			}
		});
	}
}
