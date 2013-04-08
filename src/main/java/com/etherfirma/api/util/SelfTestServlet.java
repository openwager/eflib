package com.etherfirma.api.util;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.json.*;

import com.weaselworks.util.*;

/**
 * 
 * @author crawford
 *
 */

@SuppressWarnings("serial")
public class SelfTestServlet
	extends HttpServlet
{	
	public interface PARAM
	{
		public String REQUEST_TYPE = "t"; 
	}
	
	protected
	JSONObject getRequest (final JSONObject b)
		throws JSONException
	{
		final JSONObject o = new JSONObject (); 
		o.put ("_t", "mfmessage"); 
		final JSONObject h = new JSONObject ();
		h.put ("_t", "mfheader");
		o.put ("header", h); 
		o.put ("body", b); 
		return o; 
	}
	
	
	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{
		// Figure out what we're hitting
		
		String rtype = req.getParameter (PARAM.REQUEST_TYPE); 
		if (StringUtil.isEmpty (rtype)) { 
			rtype = "pingReq"; 
		}
		
		// Send the request 

		try {
			//final ServletContext sc = getServletContext ();
			//final String url = "/" + sc.getServletContextName () + "/api/json/v2"; 
			String url = "" + req.getRequestURL (); 
			url = url.substring (0, url.indexOf ("/test")); 
			url += "/api/json/v2"; 
			
			final JsonClient client = new JsonClient (url);
			client.setDebug (false); 
			
			final JSONObject json = new JSONObject ();
			json.put ("_t", rtype); 
			final JSONObject in = getRequest (json);  
			
			final JSONObject out = client.send (in); 
			
			res.setContentType ("text/json");
//			res.setContentType ("text/txt");
			final Writer writer = res.getWriter (); 
			out.write (writer);
		} 
		catch (final Exception e) { 
			throw new IOException (e.getMessage (), e); 
		}

		return; 		
	}

	@Override
	protected void doPost (HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException 
	{
		doGet (req, res); 
		return; 
	}
}

// EOF