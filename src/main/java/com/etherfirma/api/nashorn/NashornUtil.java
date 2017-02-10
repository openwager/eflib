package com.etherfirma.api.nashorn;

import com.tessera.dispatch.Dispatcher;

import javax.script.ScriptEngine;
import javax.servlet.ServletContext;

/**
 * Created by leecrawford on 11/14/14.
 */
public class NashornUtil
{
    private
    NashornUtil ()
    {
        return;
    }

    public interface ATTR {
        public String NAME = "nashorn";
    }

    public static
    ScriptEngine getScriptEngine(final ServletContext sc)
    {
        final Object obj = sc.getAttribute (ATTR.NAME);
        return (ScriptEngine) obj;
    }

    public static
    ScriptEngine getScriptEngine (final Dispatcher d)
    {
        final ServletContext sc = d.getServletContext ();
        return getScriptEngine (sc);
    }

    public static
    void setScriptEngine (final Dispatcher d, final ScriptEngine engine)
    {
        final ServletContext sc = d.getServletContext ();
//        engine.put ("DISPATCHER", engine, d);
        sc.setAttribute (ATTR.NAME, engine);
        return;
    }

    public static
    void removeScriptEngine (final Dispatcher d)
    {
        final ServletContext sc = d.getServletContext ();
        sc.removeAttribute (ATTR.NAME);
        return;
    }
}

// EOF
