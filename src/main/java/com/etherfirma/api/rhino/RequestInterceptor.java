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
	private static final Logger logger = Logger.getLogger (RequestInterceptor.class); 
	
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
	}
	
	
	interface DEFAULT
	{
		public String JAVASCRIPT_FUNCTION = "dispatch.dispatch"; 
	}
	
	protected String javascriptFunction; 
	
	protected boolean silence = false; 
	
	public
	void init ()
	{
		javascriptFunction = getProperty (PROP.JAVASCRIPT_FUNCTION, DEFAULT.JAVASCRIPT_FUNCTION);
		if (hasProperty (PROP.SILENCE)) { 
			silence = Boolean.parseBoolean (getProperty (PROP.SILENCE)); 
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

//			threadScope.put ("context", threadScope, Context.javaToJS (c, threadScope)); 
//			threadScope.put ("threadScope", threadScope, Context.javaToJS (threadScope, threadScope)); 
		
			// Invoke the handler
			
			String response = null; 
			
			final Object o1 = c.evaluateString (threadScope, javascriptFunction, "", 1, null) ;						
			if (! (o1 instanceof Function)) {
				final String errmsg = "Undefined or not a function: " + javascriptFunction; 
				logger.error (errmsg);
			    response = "{ 'error': '" + errmsg + "' }"; 
			} else {
			    final Object functionArgs[] = { req, res, dc };
			    final Function f = (Function) o1;
			    final Object result = f.call (c, threadScope, threadScope, functionArgs);
		    	response = Context.toString (result);
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