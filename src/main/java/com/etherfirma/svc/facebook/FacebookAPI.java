package com.etherfirma.svc.facebook;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.servlet.http.*;

import org.apache.log4j.*;
import org.json.*;

import com.weaselworks.io.*;

/**
 * 
 * @author crawford
 *
 */

public class FacebookAPI
{
	protected static final Logger logger = Logger.getLogger (FacebookAPI.class); 

	public static class FBURL 
	{
		public static String getMe () { return "graph.facebook.com/me"; } 
		public static String getAppRequests (final String nodeId) { return "graph.facebook.com/" + nodeId + "/apprequests"; } 
		public static String getAccessToken () { return "graph.facebook.com/oauth/access_token"; } 
	}
	
	public
	FacebookAPI ()
	{
		this (null, null, null); 
		return; 
	}
	
	public 
	FacebookAPI (final String uri)
	{
		this (uri, null, null); 
		return; 
	}

	public 
	FacebookAPI (final String uri, final String accessToken)
	{
		this (uri, accessToken, null); 
		return;
	}
	
	public
	FacebookAPI (final String uri, final String accessToken, final Map<String, String> params)
	{
		setUri (uri); 
		setAccessToken (accessToken); 
		if (params != null) { 
			setParameters (params);
		}
		return ;
	}
	
	protected String uri; 
	public String getUri () { return this.uri; } 
	public void setUri (final String uri) { this.uri = uri; return; } 
	
	protected String accessToken; 
	public String getAccessToken () { return this.accessToken; } 
	public void setAccessToken (final String accessToken) { this.accessToken = accessToken; return; } 
	
	protected Map<String, String> params = new HashMap<String, String> (); 
	public Map<String, String> getParameters () { return this.params; } 
	public void setParameters (final Map<String, String> params) { this.params = params; return; } 
	public void setParameter (final String key, final String value) { this.params.put (key, value); return; } 
	public String getParameter (final String key) { return this.params.get (key); } 
	public boolean hasParameter (final String key) { return this.params.containsKey (key); } 
	public void removeParameter (final String key) { this.params.remove (key); return; } 
	public	void addParameters (final Map<String, String> newParams) 	{ params.putAll (newParams); return; }

	public boolean debug; 
	public boolean getDebug () { return this.debug; } 
	public void setDebug (final boolean debug) { this.debug = debug; return; } 
	
	public
	String invoke ()
		throws IOException
	{
		return invoke (null, null); 
	}
	
	public 
	JSONObject invokeToJson ()
		throws IOException, JSONException
	{
		final String body = invoke (); 
		final JSONObject obj = new JSONObject (body); 
		return obj; 
	}
	
	public 
	JSONObject invokeToJson (final String accessToken, final Map<String, String> addlParams)
		throws IOException, JSONException
	{
		final String body = invoke (accessToken, addlParams); 
		final JSONObject obj = new JSONObject (body); 
		return obj; 
	}
		
	public 
	String invoke (final String altAccessToken, final Map<String, String> addlParams)
		throws IOException
	{
		if (debug) { 
			logger.info ("invoke (" + uri + ", " + accessToken + ", " + params + ", " + addlParams + ")"); 
		}
		
		// Assemble the URL 
		
		StringBuffer buf = new StringBuffer();
		buf.append("https://");
		buf.append (uri); 
		buf.append ("?format=json");
		
		if (altAccessToken != null) { 
			buf.append ("&access_token=" );
			buf.append (URLEncoder.encode (altAccessToken)); 
		} else { 
			if (accessToken != null) { 
				buf.append ("&access_token=" );
				buf.append (URLEncoder.encode (accessToken)); 
			}
		}
	
		Map<String, String> use; 
		if (addlParams != null) { 
			use = new HashMap<String, String>(); 
			use.putAll (params); 
			use.putAll (addlParams) ;
		} else { 
			use = params; 
		}

		if (debug) {
		    logger.debug(use);
		}

		// And the parameters
		
		for (String key: use.keySet()) {
			buf.append("&"); 
			buf.append (key); 
			buf.append ("="); 
			final String value = use.get (key); 
			buf.append (URLEncoder.encode (value));
		}

		final String urlStr = buf.toString (); 
	
		if (debug) { 
			logger.info ("URL: " + urlStr); 
		}
	
		// Open the connection
		
		final URL url = new URL (urlStr);
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection ();

		// Read the body of the response 
		
		final int rc = conn.getResponseCode (); 
		final InputStream is = conn.getInputStream (); 
		final InputStreamReader isr = new InputStreamReader (is, "UTF-8"); 
		final BufferedReader rd = new BufferedReader (isr);
		final String body = IOUtil.readFully (rd); 
		rd.close();
		
		if (debug) { 
			logger.info ("Facebook response: " + body); 
		}

		if (rc == HttpServletResponse.SC_OK) { 
			return body;
		} 

		// Error handling goes here
		
		if (debug) { 
			logger.info ("Facebook response code: " + conn.getResponseCode ()); 
		}
		
		final FacebookIOException e = new FacebookIOException ("Facebook failure (" + conn.getResponseCode () + "): " + urlStr);
		e.setResponseCode (conn.getResponseCode ()); 
		e.setAPI (this);
		e.setResponse (body); 
		throw e; 
		// NOT REACHED
	}
	
	/**
	 * 
	 * @author crawford
	 *
	 */
	
	@SuppressWarnings("serial")
	public static class FacebookIOException 
		extends IOException
	{
		public FacebookIOException (final String msg) { super (msg); return; }
		public FacebookIOException (final String msg, final Throwable t) { super (msg, t); return; } 
		
		protected FacebookAPI api; 
		public FacebookAPI getApi () { return this.api; } 
		public void setAPI (final FacebookAPI api) { this.api = api; return; } 
		
		public int responseCode; 
		public int getResponseCode () { return this.responseCode; } 
		public void setResponseCode (final int responseCode) { this.responseCode = responseCode; return; } 
		
		protected String response; 
		public String getResponse () { return this.response; } 
		public void setResponse (final String response) { this.response = response; return; } 
	}
	
	
	@Override
	public
	String toString ()
	{
		StringBuffer buf = new StringBuffer (); 
		buf.append (getClass().getName ()); 
		buf.append ('['); 
		buf.append ("uri="); 
		buf.append (uri); 
		buf.append (",accessToken="); 
		buf.append (accessToken); 
		buf.append (",debug="); 
		buf.append (debug); 
		buf.append (",params="); 
		buf.append (params); 
		buf.append (']'); 
		return buf.toString (); 
	}
	
	public
	void configureMe ()
	{ 
		setUri (FBURL.getMe ()); 
		return ;
	}
	
	public
	void configureAppRequests (final String nodeId)
	{
		setUri (FBURL.getAppRequests (nodeId)); 
		return; 
	}

	public
	void configureAccessToken ()
	{
		setUri (FBURL.getAccessToken ()); 
		return; 
	}

	/**
	 * 
	 * @param secret
	 * @param payload
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */

	public static 
	String generateSignature (final String secret, final String payload) 
		throws NoSuchAlgorithmException, InvalidKeyException 
	{
		// Generate a key and create a digest of the payload
		
		final SecretKeySpec key = new SecretKeySpec (secret.getBytes (), "hmacSHA256"); 
		final Mac mac = Mac.getInstance("hmacSHA256");
		mac.init (key); 
		mac.update (payload.getBytes ()); 
		final byte [] digest = mac.doFinal ();
		
		// Base64 encode the digest and then tweak it as per URLencoding requirements
		
		String str = new String (org.apache.commons.codec.binary.Base64.encodeBase64 (digest)); 
		str = str.replace ('+', '-'); 
		str = str.replace ('/', '_');
		int index = str.indexOf ('='); 
		if (index != -1) { 
			str = str.substring (0, index); 
		}
		
		return str; 
	}

	/**
	 * 
	 * @param signature
	 * @param secret
	 * @param payload
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	
	public static
	boolean verifySignature (final String signature, final String secret, final String payload) 
		throws InvalidKeyException, NoSuchAlgorithmException
	{
		return signature.equals (generateSignature (secret, payload)); 
	}

//	public
//	String getMe (final String accessToken, final Map<String, String> params)
//		throws IOException
//	{
//	setUri (FBURL.getMe ()); 
//		final String json = invoke (accessToken, params); 
//		return json; 
//	}
//	
//	public 
//	String getAppRequests (final String nodeId, final String accessToken, final Map<String, String> params)
//		throws IOException
//	{
//	setUri (FBURL.getAppRequests (nodeId)); 
//		final String json = invoke (accessToken, params); 
//		return json; 
//	}
//	
//	public 
//	String getAccessToken (final String accessToken, final Map<String, String> params)
//		throws IOException
//	{
//		setUri (FBURL.getAccessToken ()); 
//		final String json = invoke (accessToken, params);
//		return json; 
//	}
}

// EOF
