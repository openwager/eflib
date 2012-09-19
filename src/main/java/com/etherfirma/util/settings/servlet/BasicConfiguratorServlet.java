package com.etherfirma.util.settings.servlet;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;

/**
 * 
 * @author crawford
 *
 */

public class BasicConfiguratorServlet
	extends HttpServlet
{
	private static final Logger logger = Logger.getLogger (BasicConfiguratorServlet.class); 
	
	@Override
	public 
	void init ()
		throws ServletException
	{
		BasicConfigurator.configure ();
		logger.info ("Configured."); 
		return; 
	}
}


// EOF