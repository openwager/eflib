package com.etherfirma.api.rhino;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import org.apache.log4j.Logger;

import org.mozilla.javascript.*;

import com.tessera.dispatch.*;
import com.tessera.intercept.*;

/**
 * 
 * @author crawford
 *
 */

public class RequestInterceptor
	extends InterceptorSupport
{
//	private static final Logger logger = Logger.getLogger (RequestInterceptor.class); 
	
	public
	RequestInterceptor (Map<String, String> props)
	{
		super (props);
		return; 
	}

	interface PROP
	{
		public String JAVASCRIPT_FUNCTION = "javascript.function";
		public String SILENCE = "silence";
		public String THREADLOCAL_DC = "threadLocal.dc";
	}
	
	
	interface DEFAULT
	{
		public String JAVASCRIPT_FUNCTION = "dispatch.dispatch"; 
	}
	
	protected String javascriptFunction; 
	
	protected boolean silence = false; 
	
	protected boolean addThreadLocal = false;
	
	public
	void init ()
	{
		javascriptFunction = getProperty (PROP.JAVASCRIPT_FUNCTION, DEFAULT.JAVASCRIPT_FUNCTION);
		
		if (hasProperty (PROP.SILENCE)) { 
			silence = Boolean.parseBoolean (getProperty (PROP.SILENCE)); 
		}
		
		if (hasProperty (PROP.THREADLOCAL_DC)) { 
			addThreadLocal = Boolean.parseBoolean (getProperty (PROP.THREADLOCAL_DC));
		}
		
		return; 
	}
	
	@Override
	public Alteration intercept (HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
		throws Exception 
	{
		Context c = Context.enter (); 
		
		try {
			// Setup inheriting from the shared scope object (contains the global stuff) 
			
			final ScriptableObject sharedScope = RhinoUtil.getSharedScope (dc.getDispatcher ());
			final Scriptable threadScope = c.newObject (sharedScope); 
			threadScope.setPrototype (sharedScope); 
			threadScope.setParentScope (null);
			
			threadScope.put ("request", threadScope, req); 
			threadScope.put ("response", threadScope, res); 
			threadScope.put ("dc", threadScope, dc);
			
			Logger logger = Logger.getLogger("javascript");
			threadScope.put("log", threadScope, logger);

			String errmsg = null;
			if (addThreadLocal) {
				String javascriptContextSetterFunction = "CORE.contextSetter";
				final Object possibleContextSetter = c.evaluateString (threadScope, javascriptContextSetterFunction, "", 1, null);
				if ( possibleContextSetter instanceof Function) {
					final Function contextSetter = (Function) possibleContextSetter;
				    final Object fArgs[] = { req, res, dc };
				    contextSetter.call (c, threadScope, threadScope, fArgs);
				} else {
					String error = "Undefined or not a function: " + javascriptContextSetterFunction; 
					logger.error (error);
					errmsg += error + " - ";
				}
			}
			
			String response = null; 

			final Object possibleFunction = c.evaluateString (threadScope, javascriptFunction, "", 1, null) ;						
			if (possibleFunction instanceof Function) {
			    final Object functionArgs[] = { req, res, dc };
			    final Function f = (Function) possibleFunction;
			    final Object result = f.call (c, threadScope, threadScope, functionArgs);
		    	response = Context.toString (result);
			} else {
				String error = "Undefined or not a function: " + javascriptFunction; 
				logger.error (error);
				errmsg += error;
			}
			
			if(null != errmsg) {
			    response = "{ 'error': '" + errmsg + "' }"; 
			}
			
			if (! silence) { 
			    res.setContentType ("application/json"); 
				final PrintWriter writer = res.getWriter (); 
				writer.print (response); 
				writer.flush ();
			}
		} 
		finally { 
			Context.exit (); 
		}

		return ABORT; 
	}
}

// EOf