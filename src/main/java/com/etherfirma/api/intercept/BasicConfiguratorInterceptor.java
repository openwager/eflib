package com.etherfirma.api.intercept;

import java.util.*;

import javax.servlet.http.*;

import org.apache.log4j.*;

import com.tessera.dispatch.*;
import com.tessera.intercept.*;

/**
 * A really quick-and-dirty interceptor that can be used to configure the default
 * behavior for log4j. 
 * 
 * For use when you just don't feeel like figuring out how to configure log4j for your
 * web-app and/or servlet container. 
 * 
 * @author crawford
 *
 */

public class BasicConfiguratorInterceptor
	extends InterceptorSupport
{
	public 
	BasicConfiguratorInterceptor (Map<String, String> props)
	{
		super (props);
		return; 
	}

	@Override
	public 
	Alteration intercept (HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
		throws Exception 
	{
		BasicConfigurator.configure (); 
		return NO_ALTERATION;
	}
}

// EOF