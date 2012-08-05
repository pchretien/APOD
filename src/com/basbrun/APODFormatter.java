package com.basbrun;

import java.util.Calendar;

// Implementation of the WebDataConnectorFormatter that generate
// a filename from the date, a prefix and an extension.
public class APODFormatter implements IWebDataConnectorFormatter 
{
	private Calendar date;
	private String prefix;
	private String extension;

	public APODFormatter(
			Calendar date,
			String prefix,
			String extension)
	{
		this.date = date;
		this.prefix = prefix;
		this.extension = extension;
	}
	
	public String formatFilename() {
		
		// Check if the picture exist on disk ...
		String filename = String.format(
		prefix+"%02d%02d%02d."+extension, 
		(date.get(Calendar.YEAR)<2000)?date.get(Calendar.YEAR)-1900:date.get(Calendar.YEAR)-2000, 
		date.get(Calendar.MONTH)+1, 
		date.get(Calendar.DATE));
		
		return filename;
	}

}
