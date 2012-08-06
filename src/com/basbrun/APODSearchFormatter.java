package com.basbrun;

// Implementation of the WebDataConnectorFormatter that generate
// a filename from the date, a prefix and an extension.
public class APODSearchFormatter implements IWebDataConnectorFormatter 
{
	private String query;

	public APODSearchFormatter(String query)
	{
		this.query = query;
	}
	
	public String formatFilename() 
	{		
		// Check if the picture exist on disk ...
		String filename = "apod_search?tquery="+query;		
		return filename;
	}
}
