//// APOD ////
//
// This project is an Android client to visualize the Astronomy Picture Of The
// Day (APOD) published by NASA at http://apod.nasa.gov/apod/. This project is
// free of charges and have been developed with the objective to learn Android
// programmation.
//
// Copyright (C) 2012  Philippe Chretien
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License Version 2
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
//
// You will find the latest version of this code at the following address:
// http://github.com/pchretien
//
// You can contact me at the following email address:
// philippe.chretien at gmail.com

package com.basbrun;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.graphics.Bitmap;

// The APODDataProvider is the link between the application and the APOD website 
// where all the data is. This class is instantiated in the APODApplication class 
// as a singleton.
public class APODDataProvider
{
	private APODData apodData = null;
	
	public String getAPODRoot()
	{
		return APODDataConnector.getDomainRoot();
	}

	// Return the current APOD data
	public APODData getAPOD()
	{
		// If no APOD has been loaded a call to the web server is made to get the data
		if(apodData == null)
			getAPODByDate(GregorianCalendar.getInstance());

		return apodData;
	}

	// Return the APOD data for a specific day
	public APODData getAPODByDate(Calendar date)
	{
		// If the data currently loaded is the same as the one requested nothing is done
		if(apodData != null && apodData.getDate().equals(date))
			return apodData;
		
		// Initialization
		String src = null;
		String error = "";
		String description = "";
		APODData.ApodContentType apodContentType = APODData.ApodContentType.NONE;

		// Get the APOD web page HTML content 
		String page = APODDataConnector.GetHtml(date);
		if(page != null)
		{
			// Parse the page to find an <IMG/> element
			src = APODHtmlParser.FindFirstElementSrc(page, "IMG");

			if(src != null)
			{
				// Load the HTML text and set the APOD type
				src = APODDataConnector.getDomainRoot() + src;
				description = APODHtmlParser.getDescription(page, "IMG");
				apodContentType = APODData.ApodContentType.IMG;
			}
			else
			{
				// If no <IMG/> element is found, parse the page to 
				// find an <IFRAME> element
				src = APODHtmlParser.FindFirstElementSrc(page, "IFRAME");

				if(src != null)
				{
					// Load the HTML text and set the APOD type
					description = APODHtmlParser.getDescription(page, "IFRAME");
					apodContentType = APODData.ApodContentType.IFRAME;
				}
			}

			if(src == null)
				apodContentType = APODData.ApodContentType.NONE;
		}

		// Instantiate the right APODData object.
		switch(apodContentType)
		{
		case IMG:
			Bitmap bmp = APODDataConnector.getBitmap(date, src);				
			apodData = new APODData(
					apodContentType,
					date,
					bmp,
					src,
					page,
					APODDataConnector.getDomainRoot() + APODDataConnector.formatFileName(date, "html", "ap"),
					description,
					error);			
			
			break;

		case IFRAME:
			// Instantiate an APODData for a video type APOD
			apodData = new APODData(
					apodContentType,
					date,
					null,
					src,
					page,
					APODDataConnector.getDomainRoot() + APODDataConnector.formatFileName(date, "html", "ap"),
					description,
					error);
			break;

		case ERROR:
			// Return an error
			apodData = new APODData(
					apodContentType,
					date,
					null,
					null,
					null,
					APODDataConnector.getDomainRoot() + APODDataConnector.formatFileName(date, "html", "ap"),
					description,
					error);
			break;

		case NONE:
			apodData = null;
			break;
		}		

		return apodData;
	}
	
	public String getBitmapPathFromCache(Calendar date)
	{
		return APODDataConnector.getBitmapPathFromCache(date);		
	}
}
