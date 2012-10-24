package com.etherfirma.svc.email;

import java.util.*;
import javax.mail.internet.*;

import com.weaselworks.util.*;

/**
 * 
 * @author crawford
 *
 */

@SuppressWarnings ("serial")
public class EmailRequest
	extends PropertiedIdThing
{
	public
	EmailRequest ()
	{
		return; 
	}
		
	protected List<String> addressees = new ArrayList<String> (); 
	public List<String> getAddressees () { return addressees; } 
	
	public 
	void addAddressee (final String addressee) 
		throws AddressException
	{ 
		EmailUtil.validateAddress (addressee); 
		addressees.add (addressee); 
		return;
	}
	
	protected String template; 
	public String getTemplate () { return this.template; } 
	public void setTemplate (final String template) { this.template = template; return; } 
	
	protected String agent; 
	public String getAgent () { return this.agent; } 
	public void setAgent (final String agent) { this.agent = agent; return; } 
	
	protected EmailRequestState state = EmailRequestState.UNSENT; 
	public EmailRequestState getState () { return this.state; } 
	public void setState (final EmailRequestState state) { this.state = state; return; }

	protected Map<String, String> mappings = new HashMap<String, String> (); 
	public Map<String, String> getMappings () { return this.mappings; } 
	public void setMappings (final Map<String, String> mappings) { this.mappings = mappings; return; }

	@Override
	protected void buildHash (HashBuilder hash)
	{
		super.buildHash (hash);
		hash.add (addressees); 
		hash.add (template); 
		hash.add (agent); 
		hash.add (state); 
		hash.add (mappings); 
		return; 
	}

	@Override
	protected void paramString (StringBuffer buf) 
	{
		super.paramString (buf);
		buf.append (",addressees=");
		buf.append (addressees);
		buf.append (",template=");
		buf.append (template);
		buf.append (",agent=");
		buf.append (agent);
		buf.append (",state=");
		buf.append (state);
		buf.append (",mappings=");
		buf.append (mappings);
		return; 
	}
}

// EOF