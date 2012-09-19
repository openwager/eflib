package com.etherfirma.util.settings.servlet;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;

import com.etherfirma.util.settings.*; 

/**
 * 
 */

public class LoadSettingsServlet
	extends HttpServlet
{
	private static final Logger logger = Logger.getLogger (LoadSettingsServlet.class); 
	
	public
	LoadSettingsServlet ()
	{
		return; 
	}
		
	@Override
	public 
	void init ()
		throws ServletException
	{
		final ServletConfig sc = getServletConfig (); 
		final ServletContext sx = getServletContext ();
		
		final String path = sc.getInitParameter ("path");
		logger.info ("Loading settings from: " + path) ;

		// Get the settings object and clear it
		
		final Settings s = SettingsUtil.getSettings (sx);
		s.clear (); 
		
		// And then load the settings they specified
		
		final String realpath = sx.getRealPath (path); 
    	final File file = new File (realpath);

    	if (! file.exists ()) { 
    		logger.error ("File not found: " + path);  
    	} else { 
    		try {
    			s.merge (file);
    		}
    		catch (final Exception e) { 
    			logger.error (e.getMessage (), e); 
    		}
    	}
		return; 
	}
}

// EOF