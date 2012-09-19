package com.etherfirma.api.util;

import org.json.*;

import com.weaselworks.util.*;

public class QueuedJsonClientTest
{
	public static
	void main (final String [] args)
		throws Exception
	{
		final String url = "http://localhost:8080/hauntsapi/api/json/v2"; 
		final QueuedJsonClient c = new QueuedJsonClient (url, 10); 
		c.getClient ().setDebug (true); 

		final JSONObject json = new JSONObject ();
		json.put ("_t", "pingReq"); 
		final JSONObject req = c.getClient ().getRequest (json); 

		final Stopwatch sw = new Stopwatch (); 
		sw.start (); 
		for (int i = 0; i < 20; i ++) { 
			c.send (req); 
		}
		sw.stop (); 
		System.err.println ("elapsed: " + sw); 
		
		return;
	}
}

// EOF
