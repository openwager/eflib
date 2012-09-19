package com.etherfirma.svc.email;

import com.weaselworks.util.*;

/**
 * 
 * @author crawford
 *
 */

@SuppressWarnings ("serial")
public class EmailAgent
	extends PropertiedIdThing
{
	public
	EmailAgent ()
	{
		return; 
	}
	
	public
	EmailAgent (final EmailAgent ea)
	{
		this.smtpHost = ea.smtpHost; 
		this.smtpPort = ea.smtpPort; 
		this.username = ea.username; 
		this.password =ea.password; 
		this.fromName = ea.fromName; 
		this.name = ea.name;
		for (final String key : ea.getProperties ().keySet ()) { 
			final String value = ea.getProperty (key); 
			this.setProperty (key, value); 
		}
		return; 
	}

	protected String smtpHost; 
	public String getSmtpHost () { return this.smtpHost; } 
	public void setSmtpHost (final String smtpHost) { this.smtpHost = smtpHost; return; } 
	
	protected int smtpPort;
	public int getSmtpPort () { return this.smtpPort; } 
	public void setSmtpPort (final int smtpPort) { this.smtpPort = smtpPort; return; }
		
	protected String username; 
	public String getUsername () { return this.username; } 
	public void setUsername (final String username) { this.username = username; return; } 
	
	protected String password; 
	public String getPassword () { return this.password; } 
	public void setPassword (final String password) { this.password = password; return; } 
	
	protected String fromName; 
	public String getFromName () { return this.fromName; } 
	public void setFromName (final String fromName) { this.fromName = fromName; return; }
	
	protected String name; 
	public String getName () { return this.name; } 
	public void setName (final String name) { this.name = name; return; } 
		
	@Override
	protected
	void paramString (final StringBuffer buf)
	{
		super.paramString (buf); 
		buf.append (",name=");
		buf.append (name);
		buf.append (",password=");
		buf.append (password);
		buf.append (",fromName=");
		buf.append (fromName);
		buf.append (",smtpHost=");
		buf.append (smtpHost);
		buf.append (",smtpPort=");
		buf.append (smtpPort);
		buf.append (",username=");
		buf.append (username);
		return; 
	}
	
	@Override
    protected 
    void buildHash (HashBuilder hash)
    {
		super.buildHash (hash); 
		hash.add (name); 
		hash.add (password); 
		hash.add (fromName);
		hash.add (smtpHost); 
		hash.add (smtpPort); 
		hash.add (username);
		return; 
	}
	
    @Override
    public boolean equals (final Object obj)
    {
	    if (this == obj) {
		    return true;
	    }
	    if (! (obj instanceof EmailAgent)) {
		    return false;
	    }
	    final EmailAgent other = (EmailAgent) obj;
	    return 
	    	super.equals (obj)
	        && Util.isEqual (name, other.name)
	    	&& Util.isEqual (password, other.password) 
	    	&& Util.isEqual (fromName, other.fromName) 
	    	&& Util.isEqual (smtpHost, other.smtpHost) 
	    	&& Util.isEqual (smtpPort, other.smtpPort) 
	        && Util.isEqual (username, other.username)
       ;
    }
}

// EOF