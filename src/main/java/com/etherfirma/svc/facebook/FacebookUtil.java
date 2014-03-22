package com.etherfirma.svc.facebook;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.vertx.java.core.json.JsonObject;

import com.etherfirma.util.settings.Settings;
import com.etherfirma.util.settings.SettingsUtil;
import com.weaselworks.util.JsonObjectUtil;

/**
 * Various utility methods for working with the {@link FacebookAPI} class. 
 * 
 * @author leecrawford
 *
 */

public class FacebookUtil 
{
	private static final Logger logger = Logger.getLogger (FacebookUtil.class); 
	
	private
	FacebookUtil ()
	{
		return; 
	}

	/**
	 * 
	 * @param accessToken
	 * @return
	 * @throws Exception
	 */
	
	public static
	JSONObject getMe (final String accessToken) 
		throws Exception
	{
		final FacebookAPI  fb = new FacebookAPI ("graph.facebook.com/me"); 
		final JSONObject obj = fb.invokeToJson (accessToken, null);
		return obj; 
	}

	/**
	 * 
	 * @param req
	 * @return
	 * @throws Exception 
	 */
	
	public static
	String getAppAccessToken (final HttpServletRequest req)
		throws Exception
	{
		// Get the appId and appSecret from the settings
		
//		final Settings settings = SettingsUtil.getSettings (req);		
//		final String appId = settings.lookup ("facebook.appId", String.class, true); 
//		final String appSecret = settings.lookup ("facebook.appSecret", String.class, true);

		final JsonObject settings = SettingsUtil.getSettings (req);	
		final String appId = JsonObjectUtil.get (settings, "facebook.appId"); 			
		final String appSecret = JsonObjectUtil.get (settings, "facebook.appSecret");
		// TODO: error checking

		// And fetch the app accessToken from the graph API
		
		final FacebookAPI fb = new FacebookAPI ("graph.facebook.com/oauth/access_token"); 
		fb.setParameter ("client_id", appId); 
		fb.setParameter ("client_secret", appSecret); 
		fb.setParameter ("grant_type", "client_credentials"); 
		
		fb.setDebug (true); 
		
		// The accessToken is the part following the first '=' sign
		
		final String response = fb.invoke ();
		final int index = response.indexOf ('='); 
		final String accessToken =response.substring (index + 1);  
		return accessToken;
	}
	
	/**
	 * 
	 * @param req
	 * @param inputToken
	 * @return
	 * @throws Exception
	 */
	
	public static 
	JSONObject debugToken (final HttpServletRequest req, final String inputToken)
		throws Exception
	{
		final String accessToken = getAppAccessToken (req); 
		return debugToken (accessToken, inputToken); 
	}
	
	/**
	 * 
	 * @param accessToken
	 * @param inputToken
	 * @return
	 * @throws IOException
	 * @throws JSONException 
	 */
	
	public static
	JSONObject debugToken (final String accessToken, final String inputToken)
			throws IOException, JSONException
	{
		final FacebookAPI fb = new FacebookAPI ("graph.facebook.com/debug_token"); 
		fb.setParameter ("input_token", inputToken); 
		fb.setParameter ("access_token", accessToken); 
		
		final JSONObject result = fb.invokeToJson (); 
		return result; 
	}
	
	/**
	 * 
	 * @param sr
	 * @param debug
	 * @return
	 * @throws JSONException
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	
	public static
	JSONObject parseSignedRequest (final String sr, final String appSecret, final boolean debug)
		throws JSONException, IOException, InvalidKeyException, NoSuchAlgorithmException
	{
		if (debug) { 
			logger.info ("Parsing signed request: " + sr); 
		}
		
		// Break apart the signature and the encoding 
		
		final int index = sr.indexOf ('.'); 
		final String sig = sr.substring (0, index);
		final String encoded = sr.substring (index + 1); 
		if (debug) { 
			logger.info (" SIG = '" + sig + "'"); 
			logger.info (" ENC = '" + encoded + "'"); 
		}
		
		// Decode the encoded portion of the body
		
		final byte [] bytes = org.apache.commons.codec.binary.Base64.decodeBase64 (encoded); 
		final String decoded = new String (bytes); 
		if (debug) { 
			logger.info (" DEC = '" + decoded + "'"); 
		}
		
		final JSONObject json = new JSONObject (decoded); 
		if (debug) { 
			logger.info (" JSON = " + json.toString (2)); 
		}
		
		// Verify the signature on the request
		
		final String algorithm = (String) json.get ("algorithm"); 
		if (! "HMAC-SHA256".equals (algorithm)) { 
			throw new IOException ("Unexpected algorithm: " + algorithm); 
		} 
		
		if (! FacebookAPI.verifySignature (sig, appSecret, encoded)) { 
			throw new IOException ("Signature on signed_request does not verify."); 
		}
		
		return json; 
	}
}

// EOF