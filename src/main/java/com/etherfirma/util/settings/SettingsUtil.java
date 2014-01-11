package com.etherfirma.util.settings;

import java.io.File;
import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;
import org.json.*;

/**
 * 
 * @author crawford
 *
 */

public class SettingsUtil
{
	private static final Logger logger = Logger.getLogger (SettingsUtil.class); 
	
	private 
	SettingsUtil ()
	{
		return; 
	}
	
	public static
	Settings getSettings (final HttpServletRequest req)
	{
		final ServletContext sc = req.getSession ().getServletContext ();
		return getSettings (sc); 
	}
	
	public static final String SERVLET_CONTEXT_ATTR = "_settings"; 
	
	/**
	 * 
	 * @param sc
	 * @return
	 */
	
	public static
	Settings getSettings (final ServletContext sc) 
	{
		Settings s = (Settings) sc.getAttribute (SERVLET_CONTEXT_ATTR); 
		if (s == null) {
			s = new Settings (); 
			sc.setAttribute (SERVLET_CONTEXT_ATTR, s); 
		}
		return s; 
	}
	
	/**
	 * 
	 * @param req
	 */
	
	public static
	void resetSettings (final HttpServletRequest req)
	{
		final ServletContext sc = req.getSession ().getServletContext ();
		resetSettings (sc); 
		return; 
	}
	
	/**
	 * 
	 * @param sc
	 */
	
	public static
	void resetSettings (final ServletContext sc)
	{
		sc.removeAttribute (SERVLET_CONTEXT_ATTR);
		return; 
	}
	
	/**
	 * Loads new settings from the specified path. 
	 * 
	 * @param path
	 * @param sc
	 */
	
	public static
	Settings loadSettings (final String path, final ServletContext sc)
		throws IOException, JSONException
	{
        final Settings s = SettingsUtil.getSettings (sc); 

        if (path.startsWith ("/WEB-INF")) { 
			final String realpath = sc.getRealPath (path); 
        	final File file = new File (realpath); 
        	if (! file.exists ()) { 
        		logger.error ("File not found: " + path); 
        	} else { 
        		try { 
        			s.merge (file);
        		}
        		catch (final Exception e) { 
        			logger.error ("Error merging settings from " + realpath, e); 
        		}
        	}
        } else { 
        	s.mergeResource (path); 
        }

        return s; 
	}
}

// EOF