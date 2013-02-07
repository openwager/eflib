package com.etherfirma.api.rhino;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;
import org.mozilla.javascript.*;

import com.tessera.dispatch.*;
import com.tessera.dispatch.Dispatcher;
import com.tessera.intercept.*;
import com.weaselworks.util.*;

/**
 * 
 * @author crawford
 *
 */

public class UpdateRhinoInterceptor
	extends InterceptorSupport
{
	private static final Logger logger = Logger.getLogger (UpdateRhinoInterceptor.class);
	
	public UpdateRhinoInterceptor (Map<String, String> props)
	{
		super (props);
		return; 
	}

//	protected JexlStringExpression pathExpr; 
//
//	public
//	void init ()
//		throws Exception
//	{
//		pathExpr = new JexlStringExpression (require (PROP.PATH)); 
//		return; 
//	}
	
	interface DEFAULT
	{
		public String ATTR = "results"; 
	}
	
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
	
		final ScriptableObject sharedScope = RhinoUtil.getSharedScope (disp); 
		if (sharedScope == null) { 
			logger.error ("Rhino not initialized."); 
			output.add ("ERROR: Rhino not initialized."); 
			return NO_ALTERATION;  
		}

		// Embed a reference to the ServletContext just in case

		final ServletContext sc = dc.getDispatcher ().getServletContext (); 
		Object wrapped = Context.javaToJS (sc, sharedScope);
		ScriptableObject.putProperty(sharedScope, "servletContext", wrapped);
		wrapped = Context.javaToJS (dc, sharedScope); 
		ScriptableObject.putProperty (sharedScope, "_dc", wrapped); 
		
		if (hasProperty (PROP.PATH)) { 
			loadScript (getProperty (PROP.PATH), disp, sharedScope, output); 
		} else if (hasProperty (PROP.PATH + ".0")) { 
			int i = 0; 
			while (true) { 
				final String path = getProperty (PROP.PATH + "." + i); 
				if (StringUtil.isEmpty (path)) { 
					break; 
				}				
				loadScript (path, disp, sharedScope, output); 
				i ++; 
			}
		} else { 
			throw new Exception ("No paths specified."); 
		}

		ScriptableObject.deleteProperty (sharedScope, "servletContext"); 		
		ScriptableObject.deleteProperty (sharedScope, "_dc"); 		
		
		if (req != null && req.getParameter("plainTxt") != null) {
			for (String line: output) {
				res.getWriter().write(line + "\n");
			}
			return ABORT;
		}

		return NO_ALTERATION;
	}

	/**
	 * 
	 * @param path
	 * @param disp
	 * @param sharedScope
	 */

	public static
	void loadScript (final String path, final Dispatcher disp, final ScriptableObject sharedScope, final List<String> output)
		throws Exception
	{
		if (path.startsWith ("/WEB-INF/")) {
			final String realPath = disp.getServletContext ().getRealPath (path); 
			loadScripts (new File (realPath), sharedScope, output); 
		} else { 
	    	final InputStream is = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (path); 
	        if (is == null) {
	            logger.error ("Resource not found: " + path);
	            output.add ("ERROR: Resource not found: " + path); 
	        } else { 
	        	loadScript (is, path, sharedScope, output);
	        }
		}
		
       return; 
	}
	
	/**
	 * 
	 * @param path
	 * @param sharedScope
	 * @throws IOException
	 */
	
	public static
	void loadScripts (final File path, final ScriptableObject sharedScope, final List<String> output)
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
					if (file.isDirectory () || name.endsWith (".js")) {
						loadScripts (file, sharedScope, output);
					} else { 
						logger.info ("Ignoring file: " + file.getName ());
						output.add ("Ignoring file: " + file.getName ()); 
					}
				}
			}    		
		} else { 
			final InputStream is = new FileInputStream (path);
			loadScript (is, path.toString (), sharedScope, output); 
		}
		
		return; 
	}

	/**
	 * 
	 * @param is
	 * @param path
	 * @param sharedScope
	 * @throws IOException 
	 */
	
	public static
	void loadScript (final InputStream is, final String path, final ScriptableObject sharedScope, final List<String> output) 
		throws IOException
	{
		Context ctx = Context.enter (); 
		
		try {
			logger.info ("Loading [" + path + "] into shared context.");
			output.add ("Loading [" + path + " ] into shared context."); 
			final InputStreamReader reader = new InputStreamReader (is); 
			ctx.evaluateReader (sharedScope, reader, path, 1, null);
		} 
		catch (final Exception e) { 
			logger.error (e.getMessage (), e);
			output.add ("ERROR: " + e.getMessage ()); 
		}
		finally { 
			Context.exit (); 
		}
		
		return;
	}
}

// EOF
