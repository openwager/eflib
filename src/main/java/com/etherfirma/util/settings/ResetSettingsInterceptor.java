package com.etherfirma.util.settings;

import java.util.*;

import javax.servlet.http.*;

import com.tessera.dispatch.*;
import com.tessera.intercept.*;

/**
 * 
 * @author crawford
 *
 */

public class ResetSettingsInterceptor
	extends InterceptorSupport
{

	public
	ResetSettingsInterceptor (Map<String, String> props)
	{
		super (props);
		return; 
	}

	@Override
	public 
	Alteration intercept (HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
		throws Exception 
	{
		SettingsUtil.resetSettings (req); 
		return NO_ALTERATION; 
	}
}

// EOF
