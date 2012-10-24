package com.etherfirma.svc.email;

import java.io.*;
import java.util.*;

import javax.mail.*;
import javax.mail.event.*;
import javax.mail.internet.*;
import javax.mail.internet.MimeMessage.*;

import org.apache.log4j.*;

import com.weaselworks.util.*;

/**
 * 
 * @author crawford
 *
 */

public class EmailSender
	implements TransportListener
{
	private static final Logger logger = Logger.getLogger (EmailSender.class); 
	
	public
	EmailSender ()
	{
		return; 
	}
	
	protected boolean debug = false; 
	public boolean getDebug () { return this.debug; } 
	public void setDebug (final boolean debug) { this.debug = debug; return; } 
	
	protected FutureValue <EmailRequestState> future = new FutureValue <EmailRequestState> (); 
	
	/**
	 * 
	 * @param reqId
	 * @param es
	 * @throws FunServiceException
	 */
	
	public
	EmailRequestState send (final EmailAgent a, final EmailTemplate t, final EmailRequest r)
		throws Exception
	{
		// Get the email request and make sure it's UNSENT
		
		if (r.getState () != EmailRequestState.UNSENT) { 
			throw new Exception ("EmailRequest not UNSENT."); 
		}
		
		// Get the agent we'll use to send the email and create a new session
		
		final Session sess = EmailSessionUtil.getSession (a);
		
		// Verify the mappings on the request
		
		final Map<String, String> mappings = r.getMappings (); 
		final Set<String> required = t.getRequired ();  

		for (final String param : required) { 
			if (! mappings.containsKey (param)) { 
				throw new Exception ("EmailRequest is missing '" + param + "' mapping."); 
			}
		}
		
		// Merge the mail message
		
		final Message email = createEmail (a, t, r, sess);
		
		// And send it along 
	
		Transport transport = null;
		logger.info ("Sending email: " + email); 
		
		try {
			transport = sess.getTransport ();
			transport.addTransportListener (this);
			if (! transport.isConnected ()) {
				transport.connect ();
				if (debug) { 
					logger.info ("transport.connect ()");
				} 
			}
			transport.sendMessage (email, email.getAllRecipients ());
			if (debug) { 
				logger.info ("transport.sendMessage ()");
			} 
			return future.getValue (); 
		}
		finally {
			if (transport != null) {
				if (transport.isConnected ()) {
					try {
						transport.close ();
					}
					catch (MessagingException e) {
						logger.warn ("unable to close mail transport!", e);
					}
				}
			}
		}  
		
	}
	
	
	/**
	 * 
	 */
	
	private static final String ALTERNATIVE_CONTENT_TYPE = "alternative";

	/**
	 * Creates an email message from the contents of the email request and the associated
	 * email template. 
	 * 
	 * @param es
	 * @param ea
	 * @param req
	 * @param sess
	 * @return
	 * @throws MessagingException
	 * @throws  
	 */
	
	protected
	Message createEmail (final EmailAgent ea, final EmailTemplate et, final EmailRequest er, final Session sess)
	    throws MessagingException, IOException, ClassNotFoundException
	{
		final Message message = new MimeMessage (sess);
//		if (! StringUtil.isEmpty (ea.getFromName ())) {
//			message.setFrom (new InternetAddress (ea.getFromName ()));
//		}
		message.setHeader ("From", ea.getFromName ()); 

		// Set the addressee on the message
		
		for (final String addressee : er.getAddressees()) { 
			message.addRecipient (RecipientType.TO, new InternetAddress (addressee));
		}
		
		// Merge the template body/bodies with the properties from the request
		
		String plainBody = et.getTxt (), htmlBody = et.getHtml (); 
		final Map<String, String> props = er.getMappings ();
		props.put ("_agentId", "" + ea.getId ()); 
		props.put ("_templateId", ""  + et.getId ()); 
		props.put ("_requestId", "" + er.getId ()); 
		props.put ("_props", "" + props); 
		
		if (plainBody != null) { 
			plainBody = EmailUtil.merge (plainBody, props); 
		}

		if (htmlBody != null) { 
			htmlBody = EmailUtil.merge (htmlBody, props); 
		}
		
		// Do property replacement in the subject, too
		
		final String subject = StringUtil.replaceAll (et.getSubject (), props); 
		message.setSubject (subject);

		// Finally, we can create the email message content and return it
		
		final Multipart msgContent = createEmailMessageContent (plainBody, htmlBody);
		message.setContent (msgContent);
//		message.addHeader (FUN_REQUEST_ID_HEADER, "" + er.getId ());

		return message;
	}
	
	/**
	 * 
	 * @param plainBody
	 * @param htmlBody
	 * @return
	 * @throws MessagingException
	 */

	protected
	Multipart createEmailMessageContent (final String plainBody, final String htmlBody)
	    throws MessagingException
	{
		final Multipart msgPart = new MimeMultipart (ALTERNATIVE_CONTENT_TYPE);
		int i = 0;

		if (plainBody != null) {
			final BodyPart part = createBodyPart (plainBody, MimeType.TEXT_PLAIN);
			msgPart.addBodyPart (part, i);
			i++;
		}

		if (htmlBody != null) {
			final BodyPart part = createBodyPart (htmlBody, MimeType.TEXT_HTML); 
			msgPart.addBodyPart (part, i);
		}
		
		return msgPart;
	}

	/**
	 * 
	 * @param templateBody
	 * @param mimeType
	 * @return
	 * @throws MessagingException
	 */
	
	protected
	BodyPart createBodyPart (String body, MimeType mimeType)
	    throws MessagingException
	{
		if (body != null) {
			final BodyPart part = new MimeBodyPart ();
			part.setContent (body, mimeType.getType ());
			return part;
		}
		return null;
	}
	
//	private static final String FUN_REQUEST_ID_HEADER = "fun-request-id";

//	/**
//	 * 
//	 * @param msg
//	 * @return
//	 * @throws MessagingException
//	 */
//	
//	protected
//	long getRequestIdFromMessage (final javax.mail.Message msg)
//	    throws MessagingException
//	{
//		String [] val = msg.getHeader (FUN_REQUEST_ID_HEADER);
//		if (val.length != 0) {
//			return Long.parseLong (val [0]);
//		}
//		throw new MessagingException ("Unable to get the request id header");
//	}
	
	/**
	 * 
	 * @param te
	 * @param targetState
	 * @throws MessagingException
	 */

	protected
	void updateMessage (final TransportEvent te, final EmailRequestState targetState)
	{
//		javax.mail.Message msg = te.getMessage ();
//		long reqId;
		
		try {
//			reqId = getRequestIdFromMessage (msg);
//			updateState (reqId, targetState);
			logger.info ("Message updated -> " + targetState);
			future.setValue (targetState);
		}
		catch (final Throwable e) {
			logger.warn ("Unable log state of email callback (" + targetState + ").", e);
			future.setValue (null); 
		}
		
		return; 
	}
	
	/**
	 * @see TransportListener#messageDelivered(TransportEvent)
	 */

	public
	void messageDelivered (TransportEvent te) 
	{
		updateMessage (te, EmailRequestState.DELIVERED);
		return; 
	}

	/**
	 * @see TransportListener#messageNotDelivered(TransportEvent)
	 */
	
	public 
	void messageNotDelivered (TransportEvent te) 
	{
		updateMessage (te, EmailRequestState.FAILED_DELIVERY);
		return; 
	}

	/**
	 * @see TransportListener#messagePartiallyDelivered(TransportEvent)
	 */
	
	public
	void messagePartiallyDelivered (TransportEvent te)
	{
		updateMessage (te, EmailRequestState.PARTIAL_DELIVERY); 
		return; 
	}
}

// EOF