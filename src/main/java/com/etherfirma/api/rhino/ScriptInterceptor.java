package com.etherfirma.api.rhino;

import java.util.*;

import javax.servlet.http.*;

import org.mozilla.javascript.*;

import com.tessera.dispatch.*;
import com.tessera.intercept.*;
import com.tessera.intercept.form.*;

/**
 * 
 * @author crawford
 *
 */

public class ScriptInterceptor
	extends FormInterceptor<ScriptForm>
{
	public ScriptInterceptor (Map<String, String> props)
	{
		super (props, ScriptForm.class);
		return;
	}
	
	protected 
	Scriptable getScope (final DispatchContext dc, final Context c)
	{
		final ScriptableObject sharedScope = RhinoUtil.getSharedScope (dc.getDispatcher ());
		final Scriptable threadScope = c.newObject (sharedScope); 
		threadScope.setPrototype (sharedScope); 
		threadScope.setParentScope (null); 
		return threadScope; 
	}

	@Override
	protected <Type extends ScriptForm> Alteration intercept (Type form, HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
		throws Exception 
	{
		Object result = null; 
		Context c = Context.enter (); 
		String formatted = null; 
		
		try {
			Scriptable threadScope = getScope (dc, c); ;
			
			// Save the local variables. 
			
			threadScope.put ("request", threadScope, req); 
			threadScope.put ("response", threadScope, res); 
			threadScope.put ("dc", threadScope, dc); 
			
			// Invoke the handler
			
			result = c.evaluateString (threadScope, form.getScript (), "", 1, null);						
			formatted = Context.toString (result); 
		} 
		catch (final Exception e) { 
			result = e; 
		}
		finally { 
			Context.exit (); 
		}
		
		req.setAttribute ("result", result) ;
		req.setAttribute ("formatted", formatted); 
		return NO_ALTERATION; 
	}
}

// EOf