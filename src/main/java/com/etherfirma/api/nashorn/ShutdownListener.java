package com.etherfirma.api.nashorn;

import jdk.nashorn.api.scripting.JSObject;
import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 
 * @author doru paraschiv
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
    	logger.info ("Invoking Javascript(Nashorn) shutdown listeners.");
    	invokeShutdownListeners (sc);
    	return; 
    }

    public static
    String invokeShutdownListeners (final ServletContext sc)
    {

		final String jsCORE = "CORE";
		final String jsFn = "shutdown";
		String response = null;

		try {
			// Setup inheriting from the shared scope object (contains the global stuff)

			final ScriptEngine engine = NashornUtil.getScriptEngine (sc);
			ScriptContext context = engine.getContext();

			JSObject core = (JSObject) context.getAttribute(jsCORE, ScriptContext.ENGINE_SCOPE);
			JSObject function = (JSObject) core.getMember(jsFn);
			Object result = function.call(null);

			response = Context.toString(result);

		} catch (Exception e) {
			logger.error(e);
		}


		return response;
    }
}

// EOF