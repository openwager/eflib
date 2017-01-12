package com.etherfirma.api.nashorn;

import java.io.*;
import java.util.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.Invocable;
import jdk.nashorn.api.scripting.ScriptObjectMirror;


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
        public String CONTEXT_SETTER = "context.setter";
    }


    interface DEFAULT
    {
    }

    protected String javascriptFunction;

    protected boolean silence = false;

    protected String contextSetter;

    public
    void init ()
    {
        javascriptFunction = getProperty (PROP.JAVASCRIPT_FUNCTION);

        if (hasProperty (PROP.SILENCE)) {
            silence = Boolean.parseBoolean (getProperty (PROP.SILENCE));
        }

        contextSetter = getProperty (PROP.CONTEXT_SETTER, null);

        logger.info ("javascript=" + javascriptFunction);
        logger.info ("silence=" +silence);
        logger.info ("contextSetter=" + contextSetter);
        return;
    }

    public static final String CONTEXT_SETTER = "CORE.contextSetter";

    @Override
    public Alteration intercept (HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
            throws Exception
{
        // Setup inheriting from the shared scope object (contains the global stuff)

//            final ScriptableObject sharedScope = RhinoUtil.getSharedScope (dc.getDispatcher ());
//            final Scriptable threadScope = c.newObject (sharedScope);
//            threadScope.setPrototype (sharedScope);
//            threadScope.setParentScope (null);
//
//            threadScope.put ("request", threadScope, req);
//            threadScope.put ("response", threadScope, res);
//            threadScope.put ("dc", threadScope, dc);
//
//            Logger logger = Logger.getLogger("javascript");
//            threadScope.put("log", threadScope, logger);

        final ScriptEngine engine = NashornUtil.getScriptEngine (dc.getDispatcher ());
        String errmsg = null;

        if (contextSetter != null) {
            logger.info ("Invoking " + contextSetter);
            final Invocable invocable = (Invocable) engine;
            final Object result = invocable.invokeFunction ("nashorn_invoker", contextSetter, req, res, dc);
            System.out.println(result);
        } else {
            logger.info ("No context setter.");
        }

//        final Object func = engine.get (javascriptFunction);
//
//        if (func == null) {
//            errmsg = "Javascript function not found: " + javascriptFunction;
//        } else {
//            if (func instanceof ScriptObjectMirror) {
//                final ScriptObjectMirror som = (ScriptObjectMirror) func;
//                if (! som.isFunction ()) {
//                   errmsg = "Javascript expression not a function: " + javascriptFunction;
//                } else {
                    final Invocable invocable = (Invocable) engine;
                    Object response = invocable.invokeFunction ("nashorn_invoker", javascriptFunction, req, res, dc);
//                }
//            }  else {
//                errmsg = "Javascript expression incompatible type: " + func.getClass ().getName ();
//            }
//        }

        if (null != errmsg) {
            response = "{ 'error': '" + errmsg + "' }";
        }

        if (! silence) {
            res.setContentType ("application/json");
            final PrintWriter writer = res.getWriter ();
            writer.print (response);
            writer.flush ();
        }

        return ABORT;
    }
}

// EOf