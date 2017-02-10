package com.etherfirma.api.nashorn;

import com.tessera.dispatch.DispatchContext;
import com.tessera.intercept.Alteration;
import com.tessera.intercept.InterceptorSupport;
import org.apache.log4j.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author crawford
 */

public class RequestInterceptor
        extends InterceptorSupport {
    private static final Logger logger = Logger.getLogger(RequestInterceptor.class);

    public RequestInterceptor(Map<String, String> props) {
        super(props);
        return;
    }

    interface PROP {
        public String JAVASCRIPT_FUNCTION = "javascript.function";
        public String SILENCE = "silence";
        public String CONTEXT_SETTER = "context.setter";
    }


    interface DEFAULT {
    }

    protected String javascriptFunction;

    protected boolean silence = false;

    protected String contextSetter;

    public void init() {
        javascriptFunction = getProperty(PROP.JAVASCRIPT_FUNCTION);

        if (hasProperty(PROP.SILENCE)) {
            silence = Boolean.parseBoolean(getProperty(PROP.SILENCE));
        }

        contextSetter = getProperty(PROP.CONTEXT_SETTER, null);

        logger.info("javascript=" + javascriptFunction);
        logger.info("silence=" + silence);
        logger.info("contextSetter=" + contextSetter);
        return;
    }

    public static final String CONTEXT_SETTER = "CORE.contextSetter";

    @Override
    public Alteration intercept(HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
            throws Exception {
        final ScriptEngine engine = NashornUtil.getScriptEngine(dc.getDispatcher());
        String errmsg = null;

        if (contextSetter != null) {
            final Invocable invocable = (Invocable) engine;
            final Object result = invocable.invokeFunction("nashorn_invoker", contextSetter, req, res, dc);
            System.out.println(result);
        } else {
            logger.error("No context setter.");
        }

        final Invocable invocable = (Invocable) engine;
        Object response = invocable.invokeFunction("nashorn_invoker", javascriptFunction, req, res, dc);

        if (null != errmsg) {
            response = "{ 'error': '" + errmsg + "' }";
        }

        if (!silence) {
            res.setContentType("application/json");
            final PrintWriter writer = res.getWriter();
            writer.print(response);
            writer.flush();
        }

        return ABORT;
    }
}

// EOf