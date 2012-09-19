package com.etherfirma.api.rhino;

import com.tessera.intercept.form.*;

/**
 * 
 * @author crawford
 *
 */

public class UploadForm
	extends Form
{
	protected byte [] data; 
	public byte [] getData () { return this.data; } 
	public void setData (final byte [] data) { this.data = data; return; } 
	
	public String getDataAsString () 
	{
		if (data != null) { 
			return new String (data); 
		} else { 
			return ""; 
		}
	} 
	
	protected String dataFilename; 
	public String getDataFilename () { return this.dataFilename; } 
	public void setDataFilename (final String dataFilename) { this.dataFilename = dataFilename; return; }  
}

// EOF
