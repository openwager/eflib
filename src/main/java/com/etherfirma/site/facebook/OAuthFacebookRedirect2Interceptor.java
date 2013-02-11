package com.etherfirma.site.facebook;

import java.net.*;
import java.util.*;

import javax.servlet.http.*;

import org.apache.log4j.Logger;

import com.etherfirma.svc.facebook.*;
import com.etherfirma.util.settings.*;
import com.tessera.dispatch.DispatchContext;
import com.tessera.intercept.*;
import com.weaselworks.util.*;

/**
 * 
 * @author crawford
 *
 */

public class OAuthFacebookRedirect2Interceptor 
	extends InterceptorSupport 
{
	private static final Logger logger = Logger.getLogger(OAuthFacebookRedirect2Interceptor.class);
	
	public 
	OAuthFacebookRedirect2Interceptor (final Map<String,String> props)
	{
		super(props);
	}
	
	interface PROP
		extends InterceptorSupport.PROP
	{
		public String DENIED = "denied";
		public String ERROR = "error"; 
	}
	
	protected String deniedUri; 
	protected String errorUri; 
	protected boolean debug; 
	
	public
	void init ()
		throws Exception
	{
		debug = "true".equals (getProperty (PROP.DEBUG)); 
		deniedUri = require (PROP.DENIED);
		errorUri = require (PROP.ERROR); 
		return; 
	}
	
	/**
	 * 
	 * @param logger
	 * @param req
	 */
	
	public static
	void logRequest (final Logger logger, final HttpServletRequest req)
	{
		logger.info ("INVOKED " + req.getRequestURI() + " with:"); 
		for (final Object param : req.getParameterMap ().keySet ()) { 
			final String val = req.getParameter ((String) param); 
			logger.info ("  parameter '" + param + "' = '" + val + "'"); 
		}
		return; 
	}
	
	@Override
	public 
	Alteration intercept(HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
		throws Exception 
	{
		if (debug) {
			logRequest (logger, req); 
		}
		
		// If the request contains an 'error_reason' parameter then the authentication failed. 
		// Most likely this was because the user denied the application. 
		
		final String errorReason = req.getParameter ("error_reason");
		
		if (! StringUtil.isEmpty (errorReason)) { 
			if (debug) { 
				logger.info("error_reason=" + errorReason); 
			}
			if ("user_denied".equals (errorReason)) { 
				res.sendRedirect (req.getContextPath () + "/" + deniedUri); 
				return ABORT;  
			}
		}
	
		// Load the settings (contains the appId and appSecret)
		
		final Settings settings = SettingsUtil.getSettings (req);		
		final String appId = settings.lookup ("facebook.appId", String.class, true); 
		final String appSecret = settings.lookup ("facebook.appSecret", String.class, true);
		final String facebookHandle = settings.lookup ("facebook.facebookHandle", String.class, true); 
	
		// Otherwise, we need to redirect to facebook to get their approval
		
		String paramsString = ""; 
		String fbresponse; 
		
		try { 
			final FacebookAPI fb = new FacebookAPI ("graph.facebook.com/oauth/access_token"); 
			fb.setParameter ("client_id", appId); 
			fb.setParameter ("client_secret", appSecret); 
			final String code = req.getParameter ("code"); 
			fb.setParameter ("code", code); 

			final List<String> params= new ArrayList<String> (req.getParameterMap().keySet());
			Collections.sort (params); 
			for (final String param : params) { 
                if (param.equals("code")) {
                     continue; // don't include the "code" arg they've passed us
                }
                if (param.equals("base_domain")) {
                     continue; // Facebook has been throwing this in.
                               // It's not in the redirectUrl created by
                               // RedirectFacebookAuthUrl2Interceptor.
                               // The redirect_uri's have to match for
                               // Facebook to confirm authentication.
                }

				final String value = req.getParameter (param); 
				fb.setParameter (param, value);
				if (StringUtil.isEmpty (paramsString)) { 
					paramsString = "?"; 
				} else {  
					paramsString += "&"; 
				}
				paramsString += param + "=" + URLEncoder.encode (value, "UTF-8"); 
			}

			final String redirectUrl = req.getRequestURL ().toString () + paramsString;
			fb.setParameter ("redirect_uri", redirectUrl); 

//			if (debug) { 
//				fb.setDebug (true); 				
//			}
			
			fbresponse = fb.invoke ();
			if (debug) { 
				logger.info ("response: " + fbresponse); 
			}
	
//			logger.info("accessToken response from facebook = " + response.toString());
//			accessToken =  response.toString().split("=")[1]; //second half of access_token=11087123412341234132434
//		
//			System.err.println ("ACCESS_TOKEN = " + accessToken); 
//			req.setAttribute ("accessToken", accessToken); 
		} 
		catch (final Exception e) {
			logger.error("Error obtaining authToken", e);
			res.sendRedirect (req.getContextPath () + "/" + errorUri); 
			return ABORT;  
		}
		
		// Now that we're all properly authenticated we can redirect back to the application page 
		
		final String redir = "http://apps.facebook.com/" + facebookHandle + "/"; 
		if (StringUtil.isEmpty (paramsString)) {
			paramsString = "?authenticated=true";
		} else {
			paramsString += "&authenticated=true";
		}
//		paramsString += "&code=" + req.getParameter("code");
		
//		paramsString += "&accessToken=" + URLEncoder.encode (accessToken, "UTF-8");
		
		fbresponse = fbresponse.replace ("access_token", "accessToken"); 
		paramsString += "&" + fbresponse; 
		
		final String url = redir + paramsString; 
		if (debug) { 
			logger.info ("redirecting to: " + url); 
		}
		//res.sendRedirect (url);
		RedirectFacebookAuthUrl2Interceptor.sendRedirectPage (res, url, debug); 
		return ABORT;
	}	
}

// EOF