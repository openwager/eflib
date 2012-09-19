package com.etherfirma.api.rhino;

import com.lattice.data.*;

/**
 * 
 * @author crawford
 *
 */

public class RemoteUploadScriptForm
	extends UploadForm
{
	@StringValidations (validations={
		@StringValidation (regex=".+", errmsg="Endpoint must be specified.") 
	})
	protected String endpointUrl = "/fun/api/json/v2"; 
	public String getEndpointUrl () { return this.endpointUrl; } 
	public void setEndpointUrl (final String endpointUrl) { this.endpointUrl = endpointUrl; return; } 
}

// EOF