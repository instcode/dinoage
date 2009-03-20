package org.ddth.http.core.connection;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestFuture implements Future<Response> {

	private Future<Response> future;
	
	public RequestFuture(Future<Response> future) {
		this.future = future;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return future.cancel(mayInterruptIfRunning);
	}

	@Override
	public Response get() throws InterruptedException,
			ExecutionException {
		return future.get();
	}

	@Override
	public Response get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException,
			TimeoutException {
		return future.get(timeout, unit);
	}

	@Override
	public boolean isCancelled() {
		return future.isCancelled();
	}

	@Override
	public boolean isDone() {
		return future.isDone();
	}
}
