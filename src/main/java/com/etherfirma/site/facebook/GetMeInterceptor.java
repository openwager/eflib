package com.etherfirma.site.facebook;

import java.util.*;

import javax.servlet.http.*;

import org.apache.log4j.*;
import org.json.*;

import com.etherfirma.svc.facebook.*;
import com.tessera.dispatch.*;
import com.tessera.intercept.*;

/**
 * 
 * @author crawford
 *
 */

public class GetMeInterceptor
	extends InterceptorSupport
{
	private static final Logger logger = Logger.getLogger (GetMeInterceptor.class); 
	
	public 
	GetMeInterceptor (Map<String, String> props)
	{
		super (props);
		return; 
	}

	@Override
	public 
	Alteration intercept (HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
		throws Exception 
	{
		final JSONObject blob = (JSONObject) req.getAttribute ("blob"); 
		final String accessToken = blob.getString ("oauth_token"); 
		
		if (blob != null) { 
			try { 
//				final FacebookAPI  fb = new FacebookAPI ("graph.facebook.com/me"); 
//				final JSONObject obj = fb.invokeToJson (accessToken, null);
//				req.setAttribute ("fbid", obj.get ("id")); 
//				req.setAttribute ("fbuser", obj);
				final JSONObject fbuser = FacebookUtil.getMe (accessToken); 
				req.setAttribute ("fbuser", fbuser); 
			}
			catch (final Exception e) { 
				logger.warn (e.getMessage (), e); 
			}
		}
		
		return NO_ALTERATION;
	}
}

// EOF