package com.etherfirma.site.facebook;

import java.util.*;

import javax.servlet.http.*;

import io.vertx.core.json.JsonObject;

import com.etherfirma.util.settings.*;
import com.tessera.dispatch.*;
import com.tessera.intercept.*;
import com.weaselworks.util.JsonObjectUtil;

/**
 * 
 * @author crawford
 *
 */

public class GetFacebookAppIdInterceptor
	extends InterceptorSupport
{
	public
	GetFacebookAppIdInterceptor (Map<String, String> props)
	{
		super (props);
		return; 
	}

	@Override
	public 
	Alteration intercept (HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
		throws Exception 
	{
		final JsonObject settings = SettingsUtil.getSettings (req); 
		final String appId = JsonObjectUtil.get (settings, "facebook.appId");
//		final Settings settings = SettingsUtil.getSettings (req);
//		final String appId = settings.lookup ("facebook.appId", String.class, true); 
		req.setAttribute ("appId", appId); 
		return NO_ALTERATION;
	}

}

// EOF