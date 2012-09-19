package com.etherfirma.svc.email;

import java.util.concurrent.*;

import org.apache.log4j.*;

public class Test
{
	private static final Logger logger = Logger.getLogger (Test.class); 
	
	public static
	EmailAgent getAgent ()
	{
		final EmailAgent a = new EmailAgent (); 
		a.setFromName ("Godzilla <g0dzill3rz@gmail.com>"); 
		a.setName ("Test Mailer"); 
		a.setPassword ("a1secret"); 
		a.setSmtpHost ("smtp.gmail.com"); 
		a.setSmtpPort (25); 
		a.setUsername ("g0dzill3rz"); 
		
//		a.setProperty ("mail.debug", ""  + true); 	
		a.setProperty ("mail.smtp.auth", "" + true); 
		a.setProperty ("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
		a.setProperty ("mail.smtp.socketFactory.fallback", "" + false);
		a.setProperty ("mail.smtp.socketFactory.port", "" + 465);
		a.setProperty ("mail.smtp.starttls.enable", "" + true); 
	
		return a; 
	}
	
	public static 
	EmailTemplate getTemplate ()
	{
		final EmailTemplate  t = new EmailTemplate (); 
		t.setHtml ("<h1>Test</h1> <p>This is a test of rufus email sending.</p>");
		t.setTxt ("This is a test"); 
		t.setSubject ("This is the subject"); 
		t.setName ("Sample Template"); 
		return t; 
	}
	
	public static
	EmailRequest getRequest ()
		throws Exception
	{
		final EmailRequest r = new EmailRequest (); 
		r.setAddressee ("lee.crawford@gmail.com"); 
		r.setState (EmailRequestState.UNSENT); 		
		return r; 
	}
	
	public static class PrintRunnable
	implements Runnable 
	{
		protected static int i = 0; 
		public void run () { System.err.println (i ++); } 
	}
	
	public static
	void main (final String [] args)
		throws Exception
	{
		BasicConfigurator.configure();
		logger.info ("SENDING AN EMAIL"); 
		
		
		final ExecutorService executor = Executors.newFixedThreadPool (10);
		for (int i = 0; i < 10; i ++) { 
			executor.execute (new PrintRunnable ()); 
		}
		
		
		final EmailAgent a = getAgent (); 
		final EmailTemplate t = getTemplate (); 
		final EmailRequest r = getRequest (); 
		
		final EmailSender es = new EmailSender ();
		es.setDebug (true); 
		final EmailRequestState state = es.send (a, t, r); 
		
		logger.info ("SENT: " + state); 
		 
		return; 
	}
}

// EOF