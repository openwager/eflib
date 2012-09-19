package com.etherfirma.api.rhino;

import javax.servlet.*;

import org.mozilla.javascript.*;

import com.tessera.dispatch.*;

/**
 * 
 * @author crawford
 *
 */

public class RhinoUtil
{
	private
	RhinoUtil ()
	{
		return; 
	}
	
	public interface ATTR { 
		public String NAME = "rhino"; 
	}
	
	public static
	ScriptableObject getSharedScope (final ServletContext sc)
	{
		final Object obj = sc.getAttribute (ATTR.NAME); 
		return (ScriptableObject) obj; 
	}
	
	public static
	ScriptableObject getSharedScope (final Dispatcher d)
	{
		final ServletContext sc = d.getServletContext ();
		return getSharedScope (sc); 
	}
	
	public static
	void setSharedScope (final Dispatcher d, final ScriptableObject sharedScope)
	{		
		final ServletContext sc = d.getServletContext ();
		sharedScope.put ("DISPATCHER", sharedScope, d); 
		sc.setAttribute (ATTR.NAME, sharedScope);
		return; 
	}
	
	public static
	void removeSharedScope (final Dispatcher d)
	{
		final ServletContext sc = d.getServletContext ();
		sc.removeAttribute (ATTR.NAME); 
		return; 
	}
}

// EOF