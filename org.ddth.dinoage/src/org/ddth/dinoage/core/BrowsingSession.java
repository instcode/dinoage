package org.ddth.dinoage.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.RequestFuture;
import org.ddth.http.core.content.handler.ContentHandlerDispatcher;
import org.ddth.http.impl.ThreadPoolSession;

public abstract class BrowsingSession extends ThreadPoolSession {
	private Map<String, RequestFuture> requests = new ConcurrentHashMap<String, RequestFuture>();

	public BrowsingSession(ContentHandlerDispatcher dispatcher) {
		super(dispatcher);
	}

	public void restore() {
		super.start();
		if (isRestorable()) {
			Request[] requests = getRestorable();
			for (Request request : requests) {
				queue(request);
			}
		}
	}

	protected abstract Request[] getRestorable();

	@Override
	public void start() {
		requests.clear();
		super.start();
	}

	@Override
	public RequestFuture queue(Request request) {
		RequestFuture future = null;
		String url = request.getURL();
		if (url != null && !requests.containsKey(url)) {
			future = super.queue(request);
			requests.put(url, future);
		}
		return future;
	}
}
