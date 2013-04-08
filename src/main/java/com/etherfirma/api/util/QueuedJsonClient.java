package com.etherfirma.api.util;

import java.util.concurrent.*;

import org.apache.log4j.*;
import org.json.*;

/**
 * 
 * @author crawford
 *
 */

public class QueuedJsonClient
{
	private static final Logger logger = Logger.getLogger (QueuedJsonClient.class); 
	
	public
	QueuedJsonClient (final String url, final int threads)
	{
		final JsonClient client = new JsonClient (url); 
		setClient (client); 

		final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool (threads);
		setExecutor (executor); 
		
		return; 
	}
	
	public static final long SPILL_AFTER = 16383L;
	private long spillAfter = SPILL_AFTER; 
	public long getSpillAfter () { return this.spillAfter; } 
	public void setSpillAfter (final long spillAfter) { this.spillAfter = spillAfter; return; } 
	
	private ThreadPoolExecutor executor;
	public ThreadPoolExecutor getExecutor () { return this.executor; } 
	public void setExecutor (final ThreadPoolExecutor executor) { this.executor = executor; return; } 

	protected JsonClient client; 
	public JsonClient getClient () { return this.client; }
	public void setClient (final JsonClient client) { this.client = client; return; }

	public
	void send (final JSONObject req)
		throws Exception
	{
		final long pending = executor.getTaskCount (); 
		if (pending > SPILL_AFTER) { 
			logger.warn ("Task queue filled up; spilled " + pending + " requests.");
			executor.getQueue ().clear (); 
		}
		final Runnable r = new Runnable () {
			public void run () { 
				try {
					@SuppressWarnings ("unused")
					final JSONObject res = getClient ().send (req);
					// NOTE: Doing nothing with the response; could have returned a FutureValue<Object> 
				}
				catch (final Exception e) { 
					logger.warn (e.getMessage (), e); 
				}
			}
		}; 
		executor.execute (r); 
		return ; 
	}
}

// EOF
