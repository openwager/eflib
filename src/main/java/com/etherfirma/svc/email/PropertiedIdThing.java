package com.etherfirma.svc.email;

import java.util.*;

import com.weaselworks.svc.data.*;
import com.weaselworks.util.*;

/**
 * 
 * @author crawford
 *
 */

@SuppressWarnings ("serial")
public class PropertiedIdThing
	extends Thing
{
	protected
	PropertiedIdThing ()
	{
		return; 
	}
	
	protected String id; 
	public String getId () { return this.id; } 
	public void setId (final String id) { this.id = id; return; } 

	protected Map<String, String> properties = new HashMap<String, String> (); 
	public Map<String, String> getProperties () { return this.properties; } 
	public void setProperties (final Map<String, String> properties) { this.properties = properties; return; } 
	public void setProperty (final String name, final String value) { this.properties.put (name, value); return; }
	public String getProperty (final String name) { return this.properties.get (name); } 
	public boolean hasProperty (final String name) { return this.properties.containsKey (name); }
	
	@Override
	protected void buildHash (HashBuilder hash) 
	{
		hash.add (properties);
		hash.add (id); 
		return; 
	}
	
	@Override
	protected void paramString (StringBuffer buf) 
	{
		buf.append (",id=");
		buf.append (id);
		buf.append (",properties=");
		buf.append (properties);
		return; 
	} 
	
}

// EOF