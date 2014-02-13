package com.etherfirma.util.settings;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;

import com.etherfirma.util.settings.Settings;
import com.etherfirma.util.settings.SettingsUtil;

/**
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
		return; 
	}
	
    public
    void contextInitialized (ServletContextEvent sce) 
    { 
    	// Get the servlet context reference and save it 
    	
    	try { 
    		final ServletContext sc = sce.getServletContext ();
    		SettingsUtil.loadSettings("/WEB-INF/settings", sc);
    	}
    	catch (final Exception e) { 
    		logger.error (e); 
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
