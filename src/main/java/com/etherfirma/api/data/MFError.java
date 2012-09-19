package com.etherfirma.api.data;

import java.util.*;

/**
 * 
 * @author crawford
 *
 */

public class MFError
{
	public
	MFError ()
	{
		 return; 
	}

	public
	MFError (final int errcode)
	{
//		setProperty ("_type", "error");
		setProperty ("errcode", Integer.toString(errcode));
		return; 
	}

	public
	MFError (final int errcode, final String errmsg)
	{
//		setProperty ("_type", "error");
		setProperty ("errcode", Integer.toString(errcode));
		setProperty ("emsg", errmsg); 
		return; 
	}

	public
	MFError (final Throwable thrown)
	{
//		setProperty ("_type", "error");
		setProperty ("errcode", Integer.toString(MFErrorCode.INTERNAL));
		setProperty ("etyp", thrown.getClass ().getSimpleName ());
		if (thrown.getMessage () != null) { 
			setProperty ("emsg", thrown.getMessage ());
		}
		// TODO: Consider adding the stack trace
		return; 
	}
	
	protected int errcode; 
	public int getErrorCode () { return this.errcode; } 
	public void setErrorCode (final int errcode) { this.errcode = errcode; return; } 
	
	protected Map<String, String> props; 
	public Map<String, String> getProperties () { return this.props; } 
	public void setProperties (final Map<String, String> props) { this.props = props; return; } 
	public String getProperty (final String key) { return this.props.get (key); } 
	
	public
	void setProperty (final String key, final String val) 
	{
		if (props == null) { 
			props = new HashMap<String, String> (); 
		}
		props.put (key, val); 
		return; 
	}
}

// EOF
