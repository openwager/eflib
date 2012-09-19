package com.etherfirma.api.intercept;

import java.text.*;
import java.util.*;

import javax.servlet.http.*;

import com.tessera.dispatch.*;
import com.tessera.intercept.*;

/**
 * 
 * @author crawford
 *
 */

public class NoCachingInterceptor
	extends InterceptorSupport
{
	public
	NoCachingInterceptor (Map<String, String> props)
	{
		super (props);
		return; 
	}

	private final static DateFormat expiresFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
	
	@Override
	public Alteration intercept (HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
		throws Exception 
	{
		res.setHeader ("Cache-Control", "no-cache"); 
		res.setHeader ("Pragma", "no-cache, must-revalidate"); 

		final long now = System.currentTimeMillis (); 
		final String time = expiresFormat.format (now);  
		res.setHeader ("Expires", time); 
		
		return NO_ALTERATION;
	}
}

// EOF