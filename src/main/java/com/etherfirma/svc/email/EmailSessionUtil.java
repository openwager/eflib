package com.etherfirma.svc.email;

import java.util.*;

import javax.mail.*;

/**
 * 
 * @author crawford
 *
 */

public class EmailSessionUtil
{
	private
	EmailSessionUtil ()
	{
		return; 
	}
	
	public interface PROP
	{
		public String SOCKETFACTORY_CLASS = "mail.smtp.socketFactory.class";
		public String SOCKETFACTORY_PORT = "mail.smtp.socketFactory.port";
		public String SOCKETFACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";
		public String START_TTLS_ENABLE = "mail.smtp.starttls.enable";
		public String FROM = "mail.from";
		public String SMTP_HOST = "mail.smtp.host";
		public String SMTP_PORT = "mail.smtp.port";
		public String TRANSPORT_PROTOCOL = "mail.transport.protocol";
	}
	
	
	/**
	 * A utility method for creating a new mail session from the 
	 * specified {@linkplain EmailAgent}. 
	 * 
	 * @param agent
	 * @return
	 */
	
	public static
	Session getSession (final EmailAgent agent)
	{
		final Properties props = new Properties ();

		props.put (PROP.FROM, agent.getFromName ());
		props.put (PROP.SMTP_HOST, agent.getSmtpHost ());
		props.put (PROP.SMTP_PORT, agent.getSmtpPort ());
		props.put (PROP.TRANSPORT_PROTOCOL, "smtp");
		props.putAll (agent.getProperties ());

		final EmailAuthenticator auth = new EmailAuthenticator (agent.getUsername (), agent.getPassword ()); 
		final Session session = Session.getInstance (props, auth); 
		return session;
	}
}

// EOF