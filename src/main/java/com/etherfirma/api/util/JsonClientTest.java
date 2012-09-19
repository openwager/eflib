package com.etherfirma.api.util;

import org.json.*;

/**
 * 
 * @author crawford
 *
 */

public class JsonClientTest
{
	public static
	void main (final String [] args)
		throws Exception
	{
		final String url = "http://localhost:8080/hauntsapi/api/json/v2"; 
		final JsonClient c = new JsonClient (url); 
//		c.setDebug (true); 

		final JSONObject json = new JSONObject ();
		json.put ("_t", "pingReq"); 
		final JSONObject req = c.getRequest (json); 
		
		final JSONObject res = c.send (req); 
		System.err.println (res.toString (2)); 
		
		return; 
	}
}


// EOF