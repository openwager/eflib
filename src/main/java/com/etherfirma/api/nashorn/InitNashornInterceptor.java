package com.etherfirma.api.nashorn;

import java.util.*;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import javax.servlet.*;
import javax.servlet.http.*;

import com.tessera.dispatch.*;
import com.tessera.dispatch.Dispatcher;
import com.tessera.intercept.*;

/**
 *
 * @author crawford
 *
 */

public class InitNashornInterceptor
        extends InterceptorSupport
{
//	private static final Logger logger = Logger.getLogger (InitNashornInterceptor.class); 

    public
    InitNashornInterceptor (Map<String, String> props)
    {
        super (props);
        return;
    }

    interface PROP
            extends InterceptorSupport.PROP
    {
        public String SHUTDOWN = "shutdown";
    }

    interface DEFAULT
    {
        public String ATTR = "results";
    }

    @SuppressWarnings("unchecked")
    @Override
    public Alteration intercept (HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
            throws Exception
    {
        // Get the output object

        List<String> output = new ArrayList<String> ();

        if (req != null) {
            final String attr = getProperty (PROP.ATTR, DEFAULT.ATTR);
            output = (List<String>) req.getAttribute (attr);
            if (output == null) {
                output = new ArrayList<String> ();
                req.setAttribute (attr, output);
            }
        }

        final Dispatcher disp = dc.getDispatcher ();
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        NashornUtil.setScriptEngine (disp, engine);
        output.add ("Created new Nashorn engine.");
        return NO_ALTERATION;
    }
}

// EOF


