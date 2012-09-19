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

public class UploadInterceptor
	extends FormInterceptor<UploadForm>
{
	public
	UploadInterceptor (final Map<String, String> props)
	{
		super (props, UploadForm.class); 
		return; 
	}
	
	@Override
	protected <Type extends UploadForm> Alteration intercept (Type form, HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
		throws Exception 
	{
		if (form.getData () == null || form.getData ().length == 0)  { 
			form.setError ("data", "File must be uploaded.");
		}
		
		if (form.getHasErrors ()) { 
			return failure (req); 
		}

		Context c = Context.enter (); 
		Object result = null; 
		String formatted = null; 
		
		try { 
			final ScriptableObject scope = RhinoUtil.getSharedScope (dc.getDispatcher ());
			final String script = new String (form.getData ()); 
			result = c.evaluateString (scope, script, "<upload>", 1, null);						
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

// EOF