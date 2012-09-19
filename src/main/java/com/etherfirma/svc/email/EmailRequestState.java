package com.etherfirma.svc.email;

/**
 * 
 * @author crawford
 *
 */

public enum EmailRequestState
{
	UNSENT,
	OUTBOX, 
	SENT, 
	DELIVERED, 
	PARTIAL_DELIVERY, 
	FAILED_DELIVERY, 
	ERROR; 
	
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
