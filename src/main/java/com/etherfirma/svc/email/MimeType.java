package com.etherfirma.svc.email;

/**
 * 
 * @author crawford
 *
 */

public enum MimeType
{
	TEXT_PLAIN ("text/plain"), TEXT_HTML ("text/html"); 
	
	MimeType (final String type)
	{
		this.type = type; 
		return; 
	}
	
	private final String type; 
	
	public
	String getType ()
	{
		return type; 
	}
	
	public
	String getName ()
	{
		return name (); 
	} 

	public 
	int getOrdinal ()
	{
		return ordinal (); 
	}
}

// EOF