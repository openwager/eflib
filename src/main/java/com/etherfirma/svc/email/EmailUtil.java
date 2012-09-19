package com.etherfirma.svc.email;

import java.util.*;

import javax.mail.internet.*;

import com.weaselworks.util.*;

//import org.apache.velocity.*;
//import org.apache.velocity.app.*;

//import com.weaselworks.io.*;

/**
 * 
 * @author crawford
 *
 */

public class EmailUtil

{
	private
	EmailUtil ()
	{
		return;
	}

	/**
	 * 
	 * @param body
	 * @param props
	 * @return
	 * @throws TemplateMergingException
	 */
	
	public static
	String merge (final String body, final Map<String, String> props)
//	throws TemplateMergeException
	{
		if (props == null) { 
			return body; 
		}

		final String merged = StringUtil.replaceAll (body, props);
		
//		String merged = null;
//		final StringWriter writer = new StringWriter ();
//	
//		try { 
//			final VelocityContext ctx = new VelocityContext ();
//			for (final String key : props.keySet ()) { 
//				ctx.put (key, props.get (key)); 
//			}
//			
//			final Properties velocityProps = new Properties ();
//			velocityProps.put ("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");		
//
//			Velocity.init (velocityProps);
//			Velocity.evaluate (ctx, writer, "LOG", body);
//			merged = writer.getBuffer ().toString ();
//		}
//		catch (Exception e) {
//			throw new TemplateMergeException ("Error merging template part.", e);
//		}
//		finally {
//			IOUtil.safeClose (writer); 				
//		}

		return merged;		
	}
	
	/**
	 * 
	 * @param address
	 * @throws AddressException
	 */
	
	public static
	void validateAddress (final String address)
		throws AddressException
	{
		final InternetAddress addy = new InternetAddress (address);
		addy.validate ();
		return; 
	}

	/**
	 * 
	 * @param address
	 * @return
	 */
	
	public static
	boolean isValidAddress (final String address)
	{
		boolean isValid; 
		try {
			validateAddress (address);
			isValid = true; 
		}
		catch (AddressException e) {
			isValid = false;
        }
		return isValid;
	}
}

// EOF
