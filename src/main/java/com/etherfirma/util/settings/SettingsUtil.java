package com.etherfirma.util.settings;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * 
 * @author crawford
 *
 */

public class SettingsUtil
{
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
}

// EOF