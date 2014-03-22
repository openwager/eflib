package com.etherfirma.util.settings;

import com.weaselworks.io.*;
import com.weaselworks.util.JsonObjectUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;
import org.json.*;
import org.vertx.java.core.json.JsonObject;

/**
 * 
 * @author crawford
 *
 */

public class SettingsUtil
{
	private static final Logger logger = Logger.getLogger (SettingsUtil.class); 
	
	private 
	SettingsUtil ()
	{
		return; 
	}
	
	public static
	JsonObject getSettings (final HttpServletRequest req)
	{
		final ServletContext sc = req.getSession ().getServletContext ();
		return getSettings (sc); 
	}
	
	public static final String SERVLET_CONTEXT_ATTR = "_settings"; 
	
	/**
	 * 
	 * @param sc
	 * @return
	 */
	
	public static
	JsonObject getSettings (final ServletContext sc) 
	{
		JsonObject s = (JsonObject) sc.getAttribute (SERVLET_CONTEXT_ATTR); 
		if (s == null) {
			s = new JsonObject (); 
			sc.setAttribute (SERVLET_CONTEXT_ATTR, s); 
		}
		return s; 
	}
	
	/**
	 * 
	 * @param req
	 */
	
	public static
	void resetSettings (final HttpServletRequest req)
	{
		final ServletContext sc = req.getSession ().getServletContext ();
		resetSettings (sc); 
		return; 
	}
	
	/**
	 * 
	 * @param sc
	 */
	
	public static
	void resetSettings (final ServletContext sc)
	{
		sc.removeAttribute (SERVLET_CONTEXT_ATTR);
		return; 
	}
	
	
	/**	
	 * 
	 * @param is
	 * @param src
	 * @param sc
	 * @return
	 * @throws IOException
	 */

	public static
	void loadSettings (final InputStream is, final String src, final ServletContext sc) 
		throws IOException 
	{
		final JsonObject settings = SettingsUtil.getSettings (sc); 
		final String json = IOUtil.readFully (is); 
		try { 
			final JsonObject merge = new JsonObject (json); 
			JsonObjectUtil.merge (settings, merge); 
		}
		catch (final Exception e) { 
			throw new IOException ("Error loading settings from " + src, e); 
		}
		return;  
	}
	
	/**
	 * 
	 * @param file
	 * @param src
	 * @param sc
	 * @return
	 * @throws IOException
	 */
	
	public static
	void loadSettings (final File path, final String src, final ServletContext sc)
		throws IOException
	{
		if (! path.exists ()) { 
			throw new IOException ("File not found: " + src);  
		} 			 
		
		if (path.isDirectory ()) { 
			for (final File file : path.listFiles ()) {
				final String name = file.getName ().toLowerCase (); 
				if (! name.startsWith (".")) { 
					if (file.isDirectory () || name.endsWith (".json")) {
						loadSettings (file, file.getAbsolutePath (), sc); 
					} else { 
						logger.info ("Ignoring file: " + file.getName ()); 
					}
				}
			}
		} else { 
			final InputStream is = new FileInputStream (path); 
			loadSettings (is, src, sc);
		}
		
		return; 
	}
	
	/**
	 * Loads new settings from the specified path. 
	 * 
	 * @param path
	 * @param sc
	 */
	
	public static
	JsonObject loadSettings (final String path, final ServletContext sc)
		throws IOException
	{
		if (path.startsWith ("/WEB-INF")) { 
			final String realpath = sc.getRealPath (path); 
			final File file = new File (realpath); 
			loadSettings (file, realpath, sc); 
		} else { 
			final ClassLoader cl = Thread.currentThread ().getContextClassLoader (); 
			final InputStream is = cl.getResourceAsStream (path);
			if (is == null) { 
				throw new IOException ("Resource not found: " + path); 
			}
			loadSettings (is, path, sc); 
		}

		final JsonObject settings = SettingsUtil.getSettings (sc); 
		return settings; 
	}
}

// EOF