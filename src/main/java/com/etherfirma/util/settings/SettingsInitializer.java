package com.etherfirma.util.settings;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;

import com.etherfirma.util.settings.SettingsUtil;

/**
 * Can be used in the web.xml to cause the settings to be dynamically 
 * loaded when the container starts up. 
 *
 * Add the following stanza to the web.xml: 
 * 
 * <listener>
 * 	<listener-class>com.etherfirma.util.settings.SettingsInitializer</listener-class>
 * </listener>
 * 
 * @author crawford
 *
 */

public class SettingsInitializer
	implements javax.servlet.ServletContextListener 
{ 
	public static final Logger logger = Logger.getLogger (SettingsInitializer.class); 
	
	public
	SettingsInitializer ()
	{
		logger.info ("HIYA");
		return; 
	}
	
    public
    void contextInitialized (ServletContextEvent sce) 
    { 
    	logger.info ("HELLO");
    	
    	// Get the servlet context reference and save it 
    	
    	try { 
    		final ServletContext sc = sce.getServletContext ();
    		SettingsUtil.loadSettings("/WEB-INF/settings", sc);
    	}
    	catch (final Exception e) { 
    		logger.error (e);
    		e.printStackTrace (); 
    	}
    	return; 
    } 

    public 
    void contextDestroyed (final ServletContextEvent sce) 
    { 
        return; 
    } 
}

// EOF
