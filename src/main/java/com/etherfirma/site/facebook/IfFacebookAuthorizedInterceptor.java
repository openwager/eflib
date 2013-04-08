package com.etherfirma.site.facebook;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.etherfirma.svc.facebook.FacebookUtil;
import com.etherfirma.util.settings.Settings;
import com.etherfirma.util.settings.SettingsUtil;
import com.tessera.intercept.PredicateInterceptorSupport;

/**
 * 
 * @author leecrawford
 *
 */

public class IfFacebookAuthorizedInterceptor 
	extends PredicateInterceptorSupport
{
	private static final Logger logger = Logger.getLogger(IfFacebookAuthorizedInterceptor.class);

	public 
	IfFacebookAuthorizedInterceptor(Map<String,String> props) 
	{
		super (props);
		return; 
	}
	
	protected boolean debug = false; 
	
	public
	void init ()
		throws Exception
	{
		debug = "true".equals (getProperty (PROP.DEBUG)); 
		return; 
	}
	
	/**
	 * 
	 */

	@Override
	public 
	boolean evaluate(HttpServletRequest req, HttpServletResponse res)
			throws Exception 
	{		
		if (debug) {
			OAuthFacebookRedirect2Interceptor.logRequest (logger, req);
		}

		final Settings settings = SettingsUtil.getSettings (req);		
		final String fbAppId = settings.lookup ("facebook.appId", String.class, true); 			
		final String fbAppSecret = settings.lookup ("facebook.appSecret", String.class, true);

		// Handle the case where we're injecting an access token manually. This is 
		// usually because we're trying to allow a developer or customer service to 
		// 'become' that user. 
		//
		// If they specify an access_token then we'll verify that it's valid and use
		// it to fetch enough information to fake the standard login mechanism. 
		
		final String accessToken = req.getParameter ("access_token"); 
		
		if (accessToken != null) { 
			final JSONObject json = FacebookUtil.debugToken(req, accessToken);
			final JSONObject data = (JSONObject) json.get ("data"); 
			
			final boolean isValid = data.getBoolean ("is_valid"); 
			if (! isValid) { 
				throw new IOException ("Access token not valid."); 
			}
			
			final long appId = data.getLong ("app_id");
			if (! fbAppId.equals ("" + appId)) { 
				throw new IOException ("AppId mismatch: " + appId); 
			}

			// Everything looks good so we'll put together a fake blob for the 
			// game that will look like they authenticated the standard way
			
			final JSONObject blob = new JSONObject (); 
			blob.put ("oauth_token", accessToken); 
			blob.put ("user_id", "" + data.get ("user_id"));
			
			req.setAttribute ("blob", blob); 
			return true; 			
		}
		
		// If this is coming from Facebook then they should have included a 'signed_request' 
		// parameter that contains all the information we need.  
		
		final String sr = req.getParameter ("signed_request");
		if (sr == null) { 
			return false;  
		}
		
		final JSONObject json = FacebookUtil.parseSignedRequest (sr, fbAppSecret, debug);
		if (! json.has ("oauth_token")) { 
			return false; 
		}
		
		req.setAttribute ("blob", json); 
		return true; 		
	}
}

// EOF