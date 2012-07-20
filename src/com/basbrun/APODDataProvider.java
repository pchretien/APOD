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

import android.content.SharedPreferences;
import android.graphics.Bitmap;

// The APODDataProvider is the link between the application and the APOD website 
// where all the data is. This class is instantiated in the APODApplication class 
// as a singleton.
public class APODDataProvider
{
	private APODData apodData = null;
	private SharedPreferences preferences = null;
	
	public APODDataProvider(SharedPreferences preferences)
	{
		this.preferences = preferences;
	}
	
	// Return the APOD URL root
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
		String page = "";
		APODData.ApodContentType apodContentType = APODData.ApodContentType.ERROR;
		
		if(!dateValidation(date, src, description, page))
			return null;

		// Get the APOD web page HTML content 
		page = APODDataConnector.GetHtml(date, preferences.getBoolean("caching", true));
		if(page == null)
		{
			apodContentType = APODData.ApodContentType.ERROR;
			error = "Connection error! Make sure you are connected to the internet!";
		}
		else
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
			{
				error = "Unable to read the APOD web page content!";
				apodContentType = APODData.ApodContentType.ERROR;
			}
		}
		

		// Instantiate the right APODData object.
		apodDataBuilder(date, src, error, description, page, apodContentType);		

		return apodData;
	}

	// Build the right APODData object in regard to the information found in the APOD html page
	private void apodDataBuilder(
			Calendar date, 
			String src, 
			String error,
			String description, 
			String page,
			APODData.ApodContentType apodContentType) 
	{
		apodData = new APODData(
			apodContentType,
			date,
			null,
			src,
			page,
			APODDataConnector.getDomainRoot() + APODDataConnector.formatFileName(date, "html", "ap"),
			description,
			error);		
		
		switch(apodContentType)
		{
		case IMG:
			// This is an image type APOD
			Bitmap bmp = APODDataConnector.getBitmap(date, src, preferences.getBoolean("caching", true));				
			apodData.setBitmap(bmp);			
			break;

		case IFRAME:
			break;

		case ERROR:			
			break;
		}
	}

	// Check if the requested date is in the range of the APOD publications
	private boolean dateValidation(
			Calendar date, 
			String src, 
			String description,
			String page) 
	{
		// Validate date range
		if(	date.get(Calendar.YEAR)<1995 || 
			(date.get(Calendar.YEAR) == 1995 && date.get(Calendar.MONTH) < 5) || 
			(date.get(Calendar.YEAR) == 1995 && date.get(Calendar.MONTH) == 5 && date.get(Calendar.DATE) < 20 && date.get(Calendar.DATE) != 16))
		{
			apodData = new APODData(
				APODData.ApodContentType.ERROR,
				date,
				null,
				src,
				page,
				APODDataConnector.getDomainRoot() + APODDataConnector.formatFileName(date, "html", "ap"),
				description,
				"There are not APOD before June 16th 1995 and for June 17th, 18th and 19th 1995");	
			
			return false;
		}
		
		// Validate date range
		Calendar today = Calendar.getInstance();		
		if(	date.get(Calendar.YEAR) > today.get(Calendar.YEAR) ||
			(date.get(Calendar.YEAR) == today.get(Calendar.YEAR) && date.get(Calendar.MONTH) > today.get(Calendar.MONTH)) ||
			(date.get(Calendar.YEAR) == today.get(Calendar.YEAR) && date.get(Calendar.MONTH) == today.get(Calendar.MONTH) && date.get(Calendar.DATE) > today.get(Calendar.DATE)) )
		{
			apodData = new APODData(
				APODData.ApodContentType.ERROR,
				date,
				null,
				src,
				page,
				APODDataConnector.getDomainRoot() + APODDataConnector.formatFileName(date, "html", "ap"),
				description,
				"There are not APOD for dates after today");	
			
			return false;
		}
		
		return true;
	}
	
	// Return the full ath of the bitmap in cache to load into the WebView
	public String getBitmapPathFromCache(Calendar date)
	{
		return APODDataConnector.getBitmapPathFromCache(date);		
	}
}
