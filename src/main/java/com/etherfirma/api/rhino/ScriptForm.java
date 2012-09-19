package com.etherfirma.api.rhino;

import com.lattice.data.*;
import com.tessera.intercept.form.*;

/**
 * 
 * @author crawford
 *
 */

public class ScriptForm
	extends Form
{
	@StringValidations (validations={
		@StringValidation (regex="(?s).+", errmsg="Script must be specified.") 
	})
	protected String script; 
	public String getScript () { return this.script; } 
	public void setScript (final String script) { this.script = script; return; } 

//	protected boolean reuseContext; 
//	public boolean getReuseContext () { return this.reuseContext; } 
//	public void setReuseContext (final boolean reuseContext) { this.reuseContext = reuseContext; return; } 
}

// EOF
