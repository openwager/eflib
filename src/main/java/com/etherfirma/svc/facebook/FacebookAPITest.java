package com.etherfirma.svc.facebook;

import java.util.*;

import org.json.*;

/**
 * 
 * @author crawford
 *
 */

public class FacebookAPITest
{
	public static
	void main (final String [] args)
		throws Exception
	{
		final String myId = "500043828";
		final String accessToken = "AAACJfEBVeuIBADl1bhKVE5DFgtSd8f1L7ygxP1GggSkx7NMQeL217ZAJqgdyW9tbTTxXfzHapfjrgOY0uvEeMQMtxyncXMjVt1NHmmwZDZD";
		org.apache.log4j.BasicConfigurator.configure();
		
		// Try the me
		
		{
			final FacebookAPI api1 = new FacebookAPI ("graph.facebook.com/me");
			api1.setDebug (true); 
			final JSONObject json1 = api1.invokeToJson (accessToken, null); 
			System.err.println ("==== ME 1 ====\n" + json1.toString (2));  
		}
		
		// Another way to do me
		
		{
			final FacebookAPI fb = new FacebookAPI (); 
			fb.configureMe (); 
			JSONObject json = fb.invokeToJson (accessToken, null); 
			System.err.println ("==== ME 2 ====\n" + json.toString (2)); 
			
			fb.configureAppRequests (myId); 
			json = fb.invokeToJson (accessToken, null); 
			System.err.println ("==== APP REQUESTS ====\n" + json.toString (2)); 
			
		}
		
		if (true) { return; } 
		
		//api1.getMe (accessToken, params);
		//api1.callAppRequests (nodeId, accessToken, params)
		// result = new FacebookAPI().me().accessToken(token).json();
		// facebook -= new Facebook().me().accessToken(token);
		//result = facebook.json();
		// result2 = facebook.auth().json()
		
//		final String s1 = api1.getMe (accessToken);
//		final JSONObject me2 = new JSONObject (s1); 
//		System.err.println (me2.toString (2));
		
		// Try doing an app request
		
		final FacebookAPI api2 = new FacebookAPI ("graph.facebook.com/" + myId + "/apprequests"); 
		api2.setDebug (true); 
		api2.setParameter ("method", "post");
		final Map<String, String> params = new HashMap<String, String> (); 
		params.put ("message", "hello world");
		final String str1 = api2.invoke (accessToken, params); 
		System.err.println (str1); 
		
		// Get the app access token
		
		String appId = "180758555272335"; 
		String appSecret = "d4d929d816b0eb122ea292c857c3ff59"; 
		final FacebookAPI api3 = new FacebookAPI ("graph.facebook.com/oauth/access_token");
		api3.setDebug (true); 
		api3.setParameter ("client_id", appId); 
		api3.setParameter ("client_secret", appSecret); 
		api3.setParameter ("grant_type", "client_credentials"); 
		final String results = api3.invoke (); 
		System.err.println (results); 
		
 
		
		
		return; 
	}
}

// EOF