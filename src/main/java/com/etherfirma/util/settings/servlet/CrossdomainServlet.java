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
import org.w3c.dom.*;

/**
 * 
 * @author crawford
 *
 */

public class CrossdomainServlet
	extends HttpServlet
{
	private static final Logger logger = Logger.getLogger (CrossdomainServlet.class); 
	
	public
	CrossdomainServlet ()
	{
		return; 
	}
	
	protected static
	Document createDocument (final Settings s) 
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
			
			final Settings attrs = s.extract ("attributes");
			if (attrs != null) { 
				final Map<String, Object> props = attrs.getProperties (); 
				for (final String key : props.keySet ()) { 
					final Object val = props.get (key); 
					e1.setAttribute (key, val.toString ()); 
				}
			}

			// Process any allow directives
			
			int i = 0; 
			while (true) { 
				final String key = "allow" + i;
				final Object path = s.lookup (key); 
				if (path == null) { 
					break; 
				}
				final Element e2 = doc.createElement ("allow-access-from"); 
				e2.setAttribute ("domain", path.toString ()); 
				e1.appendChild (e2);  
					
				i ++; 
			}
			
			// Process any allow http request headers from directives
			
			i = 0; 
			while (true) { 
				final String key = "allowHttpRequestHeadersFrom" + i; 
				final Map<String, Object> obj = (Map<String, Object>) s.lookup (key); 
				if (obj == null) { 
					break; 
				}
				final Element e3 = doc.createElement ("allow-http-request-headers-from");
				final String domain = (String) obj.get ("domain"); 
				if (domain == null) { 
					throw new IOException ("Missing header: " + domain); 
				}
				e3.setAttribute ("domain",  domain); 
				final String headers = (String) obj.get ("headers");
				if (headers == null) { 
					throw new IOException ("Missing header: " + headers); 
				}
				e3.setAttribute ("headers", headers);
				e1.appendChild (e3); 
				
				i ++; 
			}
			
			// Process <site-control permitted-cross-domain-policies="all" />
			
			final Map<String, Object> obj = (Map<String, Object>) s.lookup ("siteControl"); 
			if (obj != null) { 
				final String permitted = (String) obj.get ("permittedCrossDomainPolicies"); 
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
	void main (final String [] args)
		throws Exception
	{
		final Settings settings = new Settings (); 
		settings.merge (new File ("/Users/crawford/Workspace/hot-ticket/root/src/main/settings/hot-ticket-dev/crossdomain.json")); 
		final Settings crossdomain = settings.extract ("crossdomain"); 
		crossdomain.dump ();
		final Document doc = createDocument (crossdomain); 
		final Element elem = doc.getDocumentElement ();
		
		dump (elem, System.out);
		
		final byte [] bytes = dump (elem); 
		System.err.println ("" + bytes.length + " bytes."); 
		System.err.println (new String (bytes)); 
		return; 
		
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
		final Settings s = SettingsUtil.getSettings (req); 
	
		final Settings cd = s.extract ("crossdomain");
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