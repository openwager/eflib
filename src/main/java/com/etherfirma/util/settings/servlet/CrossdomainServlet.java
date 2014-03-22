package com.etherfirma.util.settings.servlet;

import java.io.*;
import java.util.*; 

import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import com.etherfirma.util.settings.*; 
import com.weaselworks.codec.xml.*;

import org.apache.log4j.*;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.w3c.dom.*;

/**
 * 
 * @author crawford
 *
 */

@SuppressWarnings("serial")
public class CrossdomainServlet
	extends HttpServlet
{
	private static final Logger logger = Logger.getLogger (CrossdomainServlet.class); 
	
	public
	CrossdomainServlet ()
	{
		return; 
	}
	
	@SuppressWarnings("unchecked")
	protected static
	Document createDocument (final JsonObject s) 
		throws ServletException, IOException
	{
		try { 
			final DocumentBuilder builder = XMLUtil.getBuilder (); 
			final Document doc = builder.newDocument (); 
			
			DOMImplementation domImpl = doc.getImplementation();
			DocumentType doctype = domImpl.createDocumentType("cross-domain-xml",
			    "SYSTEM",
			    "http://www.macromedia.com/xml/dtds/cross-domain-policy.dtd");
			doc.appendChild (doctype); 
			
			final Element e1 = doc.createElement ("cross-domain-policy");
			doc.appendChild (e1); 
	
			// Copy over any attributes
			
			final JsonObject attrs = s.getObject ("attributes");
			for (final String field : attrs.getFieldNames ()) { 
				final String value = attrs.getString (field); 
				e1.setAttribute (field, value); 
			}

			// Process any allow directives

			final JsonArray allow = s.getArray ("allow"); 
			for (int i = 0; i < allow.size (); i ++) { 
				final String domain = allow.get (i); 
				final Element e2 = doc.createElement ("allow-access-from"); 
				e2.setAttribute ("domain", domain); 
				e1.appendChild (e2);  				
			}
						
			// Process any allow http request headers from directives
			
			final JsonArray ahrh = s.getArray ("allowHttpRequestHeadersFrom");
			for (int i = 0; i < ahrh.size (); i ++) { 
				final JsonObject o = ahrh.get (i); 
				final String domain = o.getString ("domain"); 
				if (domain == null) { 
					throw new IOException ("Missing field: domain");  
				}
				final String headers = o.getString ("header"); 
				if (headers == null) { 
					throw new IOException ("Missing field: header"); 
				}
				
				final Element e3 = doc.createElement ("allow-http-request-headers-from");
				e3.setAttribute ("domain",  domain); 
				e3.setAttribute ("headers", headers);
				e1.appendChild (e3); 
			}			
			
			// Process <site-control permitted-cross-domain-policies="all" />
			
			final JsonObject siteControl = s.getObject ("siteControl"); 			
			if (siteControl != null) { 
				final String permitted = siteControl.getString ("permittedCrossDomainPolicies"); 
				if (permitted == null) { 
					throw new IOException ("Missing field: permittedCrossDomainPolicies"); 
				}
				final Element e4 = doc.createElement ("site-control"); 
				e4.setAttribute ("permitted-cross-domain-policies", permitted); 
				e1.appendChild (e4); 
			}
			
			return doc; 
		} catch (final Exception e) {
			throw new ServletException (e.getMessage (), e); 
		}
		
		// NOT REACHED
	}	
	
	public static 
	void dump (final Element el, final OutputStream os)
		throws Exception
	{
        // Now dump it to the specified output stream

        final TransformerFactory xfactory = TransformerFactory.newInstance ();
        final Transformer transformer = xfactory.newTransformer ();
        transformer.setOutputProperty("omit-xml-declaration","yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty (OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");        
        transformer.setOutputProperty (OutputKeys.OMIT_XML_DECLARATION, "no"); 
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.macromedia.com/xml/dtds/cross-domain-policy.dtd");
        
        final Source source = new DOMSource (el);
        final Result result = new StreamResult (os);
        transformer.transform (source, result);
        return; 
	}
	
	public static
	byte [] dump (final Element el)
		throws Exception
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream (); 
		dump (el, baos); 
		final byte [] bytes = baos.toByteArray ();
		return bytes; 
	}

	public 
	void doGet (final HttpServletRequest req, final HttpServletResponse res)
		throws ServletException, IOException 
	{		
		final JsonObject s = SettingsUtil.getSettings (req); 
	
		final JsonObject cd = s.getObject ("crossdomain");
		if (cd == null) { 
			logger.warn ("No crossdomain settings loaded."); 
			return; 
		}

		final Document doc = createDocument (cd);		
		res.setContentType ("text/x-cross-domain-policy"); 

		try { 
			final byte [] bytes = dump (doc.getDocumentElement ()); 
			res.setContentLength (bytes.length); 
			final OutputStream out = res.getOutputStream ();
			out.write (bytes); 			
		}
		catch (final Exception e) { 
			logger.error (e.getMessage (), e); 
		}
		
//		res.setContentType ("application/xml");
		return; 	
	}
}

// EOF