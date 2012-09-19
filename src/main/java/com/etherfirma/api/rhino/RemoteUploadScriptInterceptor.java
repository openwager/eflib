package com.etherfirma.api.rhino;

import java.util.*;

import javax.servlet.http.*;

import com.tessera.dispatch.*;
import com.tessera.intercept.*;
import com.tessera.intercept.form.*;

/**
 * 
 * @author crawford
 *
 */

public class RemoteUploadScriptInterceptor
	extends FormInterceptor<RemoteUploadScriptForm>
{
	public RemoteUploadScriptInterceptor (Map<String, String> props)
	{
		super (props, RemoteUploadScriptForm.class);
		return; 
	}

	@Override
	protected <Type extends RemoteUploadScriptForm> Alteration intercept (Type form, HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
		throws Exception 
	{		
		if (form.getData () == null || form.getData ().length == 0)  { 
			form.setError ("data", "File must be uploaded.");
		}
		
		if (form.getHasErrors ()) { 
			return failure (req); 
		}

		
		
		//req.setAttribute ("result", "foo"); 
		//req.setAttribute ("formatted", "asads"); 
		return NO_ALTERATION; 
	}
}

// EOF