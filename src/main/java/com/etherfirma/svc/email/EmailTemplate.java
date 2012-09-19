package com.etherfirma.svc.email;

import java.util.*;

import com.weaselworks.util.*;

/**
 * 
 * @author crawford
 *
 */

@SuppressWarnings ("serial")
public class EmailTemplate
	extends PropertiedIdThing
{
	public
	EmailTemplate ()
	{
		return; 
	}
	
	protected String name; 
	public String getName () { return this.name; } 
	public void setName (final String name) { this.name = name; return; } 
	
	protected String subject; 
	public String getSubject (){ return this.subject; } 
	public void setSubject (final String subject) { this.subject = subject; return; } 

	protected Set<String> required = new HashSet<String> (); 
	public void setRequired (final Set<String> required) { this.required = required; } 
	public Set<String> getRequired () { return this.required; } 
	
	protected Set<String> optional = new HashSet<String> (); 
	public void setOptional (final Set<String> optional) { this.optional = optional; return; } 
	public Set<String> getOptional () { return this.optional; } 
	
	protected String txt; 
	public String getTxt () { return this.txt; } 
	public void setTxt (final String txt) { this.txt = txt; return; } 
	
	protected String html; 
	public String getHtml () { return this.html; } 
	public void setHtml (final String html) { this.html = html; return; }
	
	@Override
	protected void buildHash (HashBuilder hash) 
	{
		super.buildHash (hash);
		hash.add (name); 
		hash.add (subject); 
		hash.add (required); 
		hash.add (optional); 
		hash.add (txt); 
		hash.add (html); 
		return; 
	}
	
	@Override
	protected void paramString (StringBuffer buf) 
	{
		super.paramString (buf);
		buf.append (",name=");
		buf.append (name);
		buf.append (",subject=");
		buf.append (subject);
		buf.append (",required=");
		buf.append (required);
		buf.append (",optional=");
		buf.append (optional);
		buf.append (",txt=");
		buf.append (txt);
		buf.append (",html=");
		buf.append (html);
		return; 
	} 
}

// EOF