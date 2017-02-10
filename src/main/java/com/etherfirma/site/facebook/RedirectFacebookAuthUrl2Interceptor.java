package com.etherfirma.site.facebook;

import java.io.IOException;
import java.net.*;
import java.util.*;

import javax.servlet.http.*;

import org.apache.log4j.*;
import io.vertx.core.json.JsonObject;

import com.etherfirma.util.settings.*;
import com.tessera.dispatch.*;
import com.tessera.intercept.*;
import com.weaselworks.util.*;

/**
 * 
 * @author crawford
 *
 */

public class RedirectFacebookAuthUrl2Interceptor 
	extends InterceptorSupport
{
	private static final Logger logger = Logger.getLogger(RedirectFacebookAuthUrl2Interceptor.class);
	
	public
	RedirectFacebookAuthUrl2Interceptor(Map<String,String> props) 
	{
		super(props);
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

	@Override
	public 
	Alteration intercept (HttpServletRequest req, HttpServletResponse res, DispatchContext dc) 
		throws Exception
	{
		if (debug) {
			OAuthFacebookRedirect2Interceptor.logRequest (logger, req);
		}
		
		// Read the settings and get the base redirect url 
		
		final JsonObject settings = SettingsUtil.getSettings (req);
		final String permissions = JsonObjectUtil.get (settings, "facebook.permissions"); 
		final String appId = JsonObjectUtil.get (settings, "facebook.appId"); 
		String oauthUrl = req.getScheme () + "://" + JsonObjectUtil.get (settings, "facebook.oauthRedirect");
			
//		final Settings settings = SettingsUtil.getSettings (req);
//		final String permissions = settings.lookup ("facebook.permissions", String.class, true); 
//		final String appId = settings.lookup ("facebook.appId", String.class, true); 
//		String oauthUrl = req.getScheme () + "://" + settings.lookup ("facebook.oauthRedirect", String.class, true);

		// Extract the GET parameters from the request
		
		final String queryString = req.getQueryString (); 
		boolean first = true; 
		
		if (! StringUtil.isEmpty (queryString)) { 
			final List<String> keys = new ArrayList<String> (); 
			for (final String pair : queryString.split ("&")) { 
				keys.add (pair.split ("=")[0]); 
			}
			Collections.sort (keys); 
			for (final String key : keys) {
				if (first) { 
					oauthUrl += '?'; 
					first = false ;
				} else { 
					oauthUrl += '&'; 
				}
				oauthUrl += key + '=' + URLEncoder.encode (req.getParameter (key), "UTF-8"); 
			}
		}
		
		// Put together the URL to redirect the end-user to 
        String url = "https://www.facebook.com/dialog/oauth?" + 
		    "client_id=" + appId + "&" + 
		    "redirect_uri=" + URLEncoder.encode (oauthUrl, "UTF-8") + "&" +
		    "scope=" + permissions;
				
        // Output some debugging information if we need to 
        
        if (debug) { 
        	logger.info ("REDIRECTING TO: \n  " + url); 
        }
        
		// Output a page that will redirect them to the specified page. 
		
		sendRedirectPage (res, url, debug); 
		return ABORT;
	}
	
	/**
	 * 
	 * @param res
	 * @param url
	 * @param debug
	 * @throws IOException
	 */
	
	public static
	void sendRedirectPage (final HttpServletResponse res, final String url, final boolean debug) 
			throws IOException
	{
		final StringBuffer html = getRedirectPage (url, debug); 
		res.setContentType ("text/html");
		res.setContentLength (html.length());
		res.getOutputStream().write(html.toString().getBytes());
		return; 
	}
	
	/**
	 * 
	 * @param url
	 * @param debug
	 * @return
	 */
	
	public static
	StringBuffer getRedirectPage (final String url, final boolean debug)
	{
		final StringBuffer html = new StringBuffer();
//		if (debug) { 
//			html.append ("<h1>Debug Mode</h1>"); 
//			html.append ("Redirecting to: <pre>" + url + "</pre>");
//			html.append ("Click <a href='" + url + "'>here</a> to continue."); 
//		} else { 
			html.append("<script type=\"text/javascript\">");
			html.append("var frame = self;");
			html.append("while (frame != parent)");
			html.append("  frame = parent;");
			html.append("frame.location = \"" + url + "\";");
			html.append("</script>");			
//		}
		return html; 
	}
}

// EOF
