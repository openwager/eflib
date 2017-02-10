package com.etherfirma.util.settings;

import java.util.*;

import javax.servlet.http.*;

import io.vertx.core.json.JsonObject;

import com.tessera.dispatch.*;
import com.tessera.intercept.*;

/**
 * 
 * @author crawford
 *
 */

public class GetSettingsInterceptor
	extends InterceptorSupport
{
	public
	GetSettingsInterceptor (Map<String, String> props)
	{
		super (props);
		return; 
	}

	interface DEFAULT
	{
		public String ATTR = "settings"; 
	}
	
	@Override
	public Alteration intercept (HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
		throws Exception 
	{
		final JsonObject settings = SettingsUtil.getSettings (req); 
		req.setAttribute (getProperty (PROP.ATTR, DEFAULT.ATTR), settings); 
		return NO_ALTERATION; 
	}
}

// EOF