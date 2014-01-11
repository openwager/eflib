package com.etherfirma.util.settings;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;

import com.tessera.dispatch.*;
import com.tessera.intercept.*;
import com.weaselworks.util.*;

/**
 * 
 * @author crawford
 *
 */

public class LoadSettingsInterceptor
	extends InterceptorSupport
{
	private static final Logger logger = Logger.getLogger (LoadSettingsInterceptor.class); 
	
	public
	LoadSettingsInterceptor (Map<String, String> props)
	{
		super (props);
		return; 
	}

	@Override
	public 
	Alteration intercept (HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
		throws Exception 
	{
	    int cnt = 0;
		
	    while (true) {
	        final String path = getProperty ("path." + cnt);
	        if (StringUtil.isEmpty (path)) {
	            break;
	        }
	        final ServletContext sc = dc.getDispatcher ().getServletContext (); 
	        SettingsUtil.loadSettings(path, sc);
	        cnt ++;
	    }
	
	    logger.info ("Loaded " + cnt + " settings resources."); 
		return NO_ALTERATION; 
	}

}

// EOF