package com.etherfirma.api.nashorn;

import com.tessera.dispatch.DispatchContext;
import com.tessera.dispatch.Dispatcher;
import com.tessera.intercept.Alteration;
import com.tessera.intercept.InterceptorSupport;
import com.weaselworks.util.StringUtil;
import org.apache.log4j.Logger;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author crawford
 *
 */

public class UpdateNashornInterceptor
	extends InterceptorSupport
{
	private static final Logger logger = Logger.getLogger (UpdateNashornInterceptor.class);
	
	public UpdateNashornInterceptor(Map<String, String> props)
	{
		super (props);
		return; 
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
	
		final ScriptEngine engine = NashornUtil.getScriptEngine (disp);
		if (engine == null) {
			logger.error ("Nashorn ScriptEngine not initialized.");
			output.add ("ERROR: Nashorn ScriptEngine not initialized.");
			return NO_ALTERATION;  
		}

		// Embed a reference to the ServletContext just in case

		final ServletContext sc = dc.getDispatcher ().getServletContext();
        final Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put ("servletContext", sc);
        bindings.put ("_dc", dc);
        bindings.put ("dc", dc);

		if (hasProperty (PROP.PATH)) {
			loadScript (getProperty (PROP.PATH), disp, engine, bindings, output);
		} else if (hasProperty (PROP.PATH + ".0")) { 
			int i = 0; 
			while (true) { 
				final String path = getProperty (PROP.PATH + "." + i); 
				if (StringUtil.isEmpty (path)) { 
					break; 
				}				
				loadScript (path, disp, engine, bindings, output);
				i ++; 
			}
		} else { 
			throw new Exception ("No paths specified."); 
		}

		if (req != null && req.getParameter("plainTxt") != null) {
			for (String line: output) {
				res.getWriter().write(line + "\n");
			}
			return ABORT;
		}

        bindings.remove ("servletContext");
        bindings.remove ("_dc");
        bindings.remove ("dc");

		return NO_ALTERATION;
	}

	/**
	 * 
	 * @param path
	 * @param disp
	 * @param engine
	 */

	public static
	void loadScript (final String path, final Dispatcher disp, final ScriptEngine engine, final Bindings bindings, final List<String> output)
		throws Exception
	{
		if (path.startsWith ("/WEB-INF/")) {
			final String realPath = disp.getServletContext ().getRealPath (path); 
			loadScripts (new File (realPath), engine, bindings, output);
		} else { 
	    	final InputStream is = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (path); 
	        if (is == null) {
	            logger.error ("Resource not found: " + path);
	            output.add ("ERROR: Resource not found: " + path); 
	        } else { 
	        	loadScript (is, path, engine, bindings, output);
	        }
		}
		
       return; 
	}
	
	/**
	 * 
	 * @param path
	 * @param engine
	 * @throws java.io.IOException
	 */

	public static
	void loadScripts (final File path, final ScriptEngine engine, final Bindings bindings ,final List<String> output)
		throws IOException
	{
		if (! path.exists ()) {
			logger.error ("Missing resource: " + path);
			output.add ("ERROR: Missing resource: " + path);
			return;
		}

		// If the path is a directory, recursively

		if (path.isDirectory ()) {
			final File [] files = path.listFiles ();
			Arrays.sort (files);
			for (final File file : files) {
				final String name = file.getName ().toLowerCase ();
				if (! name.startsWith (".")) {
					if ((file.isDirectory () && ! name.endsWith ("-ignored")) || name.endsWith (".js") ) {
						loadScripts (file, engine, bindings, output);
					} else {
						logger.info ("Ignoring file: " + file.getName ());
						output.add ("Ignoring file: " + file.getName ());
					}
				}
			}
		} else {
			final InputStream is = new FileInputStream (path);
			loadScript (is, path.toString (), engine, bindings, output);
		}

		return;
	}

	/**
	 *
	 * @param is
	 * @param path
	 * @param engine
	 * @throws java.io.IOException
	 */
	
	public static
	void loadScript (final InputStream is, final String path, final ScriptEngine engine, final Bindings bindings, final List<String> output)
		throws IOException
	{
		try {
			logger.info ("Loading [" + path + "] into Nashorn ScriptEngine.");
			output.add ("Loading [" + path + " ] into Nashorn ScriptEngine.");
			final InputStreamReader reader = new InputStreamReader (is);
            if (bindings == null) {
                engine.eval (reader);
            } else {
                engine.eval (reader, bindings);
            }
		}
		catch (final Exception e) { 
			logger.error (e.getMessage (), e);
			output.add ("ERROR: " + e.getMessage ()); 
		}

		return;
	}
}

// EOF
