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
import org.ddth.grabber.core.handler.NavigationHandler;

public class MultiConnectionModel extends SingleConnectionModel {
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
	
	public void sendRequest(final String sURL, final NavigationHandler contentHandler) {
		executor.execute(new Runnable() {
			public void run() {
				try {
					MultiConnectionModel.super.sendRequest(sURL, contentHandler);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}