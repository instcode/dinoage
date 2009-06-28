package org.ddth.dinoage.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.RequestFuture;
import org.ddth.http.core.content.handler.ContentHandlerDispatcher;
import org.ddth.http.impl.ThreadPoolSession;

public abstract class BrowsingSession extends ThreadPoolSession {
	private Map<String, RequestFuture> requests = new ConcurrentHashMap<String, RequestFuture>();

	private Workspace workspace;
	protected SessionProfile profile;

	public BrowsingSession(SessionProfile profile, Workspace workspace, ContentHandlerDispatcher dispatcher) {
		super(dispatcher);
		this.workspace = workspace;
		this.profile = profile;
	}

	public void restore() {
		super.start();
		queue(new Request(profile.getBeginningURL()));
	}

	@Override
	public void start() {
		super.start();
		profile.saveURL(null);
		requests.clear();
		queue(new Request(profile.getBeginningURL()));
	}

	@Override
	public RequestFuture queue(Request request) {
		RequestFuture future = null;
		String sURL = request.getURL();
		if (sURL != null && !requests.containsKey(sURL)) {
			profile.saveURL(request.getURL());
			workspace.saveProfile(profile);
			future = super.queue(request);
			requests.put(sURL, future);
		}
		return future;
	}

	public boolean isRestorable() {
		return profile.isNewlyCreated();
	}
}
