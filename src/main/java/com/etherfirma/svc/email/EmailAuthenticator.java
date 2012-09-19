package com.etherfirma.svc.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 
 * @author crawford
 *
 */

public class EmailAuthenticator
	extends Authenticator
{
	public
	EmailAuthenticator (final String username, final String password)
	{
		auth = new PasswordAuthentication (username, password);
		return; 
	}
	
	private PasswordAuthentication auth;

	@Override
    protected 
    PasswordAuthentication getPasswordAuthentication ()
    {
		return auth;
    }
}

// EOF