package com.etherfirma.site.facebook;

import java.util.Map;
import javax.servlet.http.*;
import org.apache.log4j.Logger;
import com.tessera.intercept.*;
import com.tessera.intercept.InterceptorSupport.PROP;

/**
 * 
 * @author crawford
 *
 */

public class IfFacebookAuthorized2Interceptor 
	extends PredicateInterceptorSupport
{
	private static final Logger logger = Logger.getLogger(IfFacebookAuthorized2Interceptor.class);

	public 
	IfFacebookAuthorized2Interceptor(Map<String,String> props) 
	{
		super(props);
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
	boolean evaluate(HttpServletRequest req, HttpServletResponse res)
			throws Exception 
	{		
		final String authenticated = req.getParameter ("authenticated"); 
		final boolean result = "true".equals (authenticated);
		if (debug) { 
			logger.info ("authenticated = " + result); 
		}
		return result;  

		//		if (req.getParameter ("authenticated") == null) {
//			return false;
//		}
		
		
//		final Long facebookId = facebookService.getLoggedInFacebookId(req);
//		if (facebookId != null) {
//			logger.info("Facebook ID + " + facebookId + " is authorized.");
//			req.setAttribute("accessToken", facebookService.getAccessToken(req));
//			req.setAttribute("facebookId", facebookId);
//			return true;
//		}		
//		return false;
	}
}