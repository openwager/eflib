package com.etherfirma.api.rhino;

import org.apache.log4j.*;
import javax.servlet.*;
import org.mozilla.javascript.*;

/**
 * 
 * @author crawford
 *
 */

public class ShutdownListener
	implements ServletContextListener 
{
	private static final Logger logger = Logger.getLogger (ShutdownListener.class); 
	
    public
    void contextInitialized (ServletContextEvent event) 
    {
    	return; 
    }

    public 
    void contextDestroyed (ServletContextEvent event)
    {
    	final ServletContext sc = event.getServletContext ();
    	logger.info ("Invoking Javascript shutdown listeners.");
    	// TODO: Hardcoding the name of the shutdown function isn't very elegant. This really
    	// should go as a servlet init parameter or something like that... [crawford] 
    	invokeShutdownListeners ("MFP.shutdown", sc);
    	return; 
    }

    public static
    String invokeShutdownListeners (final String shutdown, final ServletContext sc)
    {
        final ScriptableObject so = RhinoUtil.getSharedScope (sc);
        
        if (so != null) {
    		Context c = Context.enter (); 
    		
    		try {
    			final Object o1 = c.evaluateString (so, shutdown, "", 1, null) ;						
    			if (! (o1 instanceof Function)) {
    				return "Shutdown function undefined or not a function: " + shutdown; 
    			} else {
    			    final Object functionArgs[] = { };
    			    final Function f = (Function) o1;
    			    final Object result = f.call (c, so, so, functionArgs);
    		    	final String response = Context.toString (result);
    		    	return response; 
    			}
    		} 
    		finally { 
    			Context.exit (); 
    		}
        }

        return null; 
    }
}

// EOF