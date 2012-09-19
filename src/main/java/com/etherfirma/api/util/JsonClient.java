package com.etherfirma.api.util;

import java.io.*;
import java.net.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

import org.json.*;

/**
 * 
 * @author crawford
 *
 */

public class JsonClient
{
	public
	JsonClient (final String url)
	{
		setUrl (url);
		return; 
	}

	protected String url; 
	public String getUrl () { return this.url; } 
	public void setUrl (final String url) { this.url = url; return; } 
	
	public static final boolean DEFAULT_DEBUG = false; 
	protected boolean debug = DEFAULT_DEBUG; 
	public boolean getDebug () { return this.debug; } 
	public void setDebug (final boolean debug) { this.debug = debug; return; } 

	/**
	 * 
	 * @param b
	 * @return
	 * @throws JSONException
	 */
	
	public
	JSONObject getRequest (final JSONObject b)
		throws JSONException
	{
		final JSONObject o = new JSONObject (); 
		o.put ("_t", "mfmessage"); 
		final JSONObject h = new JSONObject ();
		h.put ("_t", "mfheader");
		o.put ("header", h); 
		o.put ("body", b); 
		return o; 
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 * @throws JSONException
	 */
	
	public 
	JSONObject getRequest (final String type)
		throws JSONException
	{
		final JSONObject b = new JSONObject (); 
		b.put ("_t", type); 
		return getRequest (b);
	}
	
	/**
	 * 
	 * @param req
	 * @return
	 * @throws Exception
	 */
	
	public
	JSONObject send (final JSONObject req)
		throws Exception
	{
		// Setup an HttpClient for sending the request
		
		final PostMethod post = new PostMethod (url);
        
        // Execute request
        
        try {
        	final StringBuffer buf = new StringBuffer (); 
        	buf.append ("json="); 
        	buf.append (URLEncoder.encode (req.toString (), "UTF-8"));
        	byte [] bytes = buf.toString ().getBytes (); 
        	final RequestEntity entity = new ByteArrayRequestEntity(bytes, "application/x-www-form-urlencoded");  
            post.setRequestEntity(entity);
            final HttpClient httpclient = new HttpClient();

            if (getDebug ()) { 
            	System.err.println ("========== SENDING ============"); 
            	System.err.println (req.toString (2)); 
        	}
        	
            int result = httpclient.executeMethod(post);
            if (result != 200) { 
            	throw new IOException ("Invalid response code: " + result); 
            }
            bytes = post.getResponseBody ();
            final String str = new String (bytes); 
//            if (getDebug ()) { 
//            	System.err.println (str); 
//            }
            final JSONObject res = new JSONObject (new String (bytes));  
            if (getDebug ()) { 
            	System.err.println ("========== RECEIVE ============"); 
            	System.err.println (res.toString (2)); 
            }
            return res;
        } finally {
            post.releaseConnection();
        }

        // NOT REACHED 
		
	}
}

// EOF