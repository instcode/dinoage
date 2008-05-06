/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.grabber.impl.connection;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.ddth.grabber.core.connection.Request;

public class MultiConnectionModel extends SingleConnectionModel {
	/**
	 * Make sure we don't flood the server continuously :D...
	 * We should appear as an "well-educated" grabber ;-) 
	 */
	private static final long TIME_INTERVAL_BETWEEN_TWO_REQUESTS = 5000;
	
	private ThreadPoolExecutor executor;

	public MultiConnectionModel(HttpClient httpClient, int poolSize) {
		super(httpClient);
		int corePoolSize = 2;
		int keepToAlive = 10000;
		int maxPoolSize = (poolSize < corePoolSize) ? corePoolSize : poolSize;
		SynchronousQueue<Runnable> synchronousQueue = new SynchronousQueue<Runnable>();
		executor = new ThreadPoolExecutor(
				corePoolSize, maxPoolSize, keepToAlive, TimeUnit.MILLISECONDS, synchronousQueue,
				new ThreadPoolExecutor.AbortPolicy());
	}
	
	public void sendRequest(final Request request) {
		executor.execute(new Runnable() {
			public void run() {
				MultiConnectionModel.super.sendRequest(request);
				try {
					Thread.sleep(TIME_INTERVAL_BETWEEN_TWO_REQUESTS);
				}
				catch (InterruptedException e) {
				}
			}
		});
	}
}