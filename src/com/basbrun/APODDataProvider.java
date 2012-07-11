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
	private String domainRoot = "http://apod.nasa.gov/apod/";
	
	// Return the base url of the APOD website
	public String getDomainRoot()
	{
		return domainRoot;
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
		// Extract the Calendar date
		int iYear = (date.get(Calendar.YEAR)<2000)?date.get(Calendar.YEAR)-1900:date.get(Calendar.YEAR)-2000;
		int iMonth = date.get(Calendar.MONTH)+1; // Month is zero based ...
		int iDay = date.get(Calendar.DATE);

		// Build the html file name to load
		String path = String.format("ap%02d%02d%02d.html", iYear, iMonth, iDay);

		// If the data currently loaded is the same as the one requested nothing is done
		if(apodData != null && apodData.getDate().equals(date))
			return apodData;

		// Initialization
		String src = null;
		String error = "";
		String description = "";
		APODData.ApodContentType apodContentType = APODData.ApodContentType.NONE;

		// Get the APOD web page HTML content 
		String page = HttpFetch.GetHtml(domainRoot + path);
		if(page == null)
		{
			apodContentType = APODData.ApodContentType.ERROR;
			error = "Unable to load page " + domainRoot + path;
		}

		if(page != null)
		{
			// Parse the page to find an <IMG/> element
			src = Parser.FindFirstElementSrc(page, "IMG");

			if(src != null)
			{
				// Load the HTML text and set the APOD type
				src = domainRoot + src;
				description = this.getDescription(page, "IMG");
				apodContentType = APODData.ApodContentType.IMG;
			}
			else
			{
				// If no <IMG/> element is found, parse the page to 
				// find an <IFRAME> element
				src = Parser.FindFirstElementSrc(page, "IFRAME");

				if(src != null)
				{
					// Load the HTML text and set the APOD type
					description = this.getDescription(page, "IFRAME");
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
			// Instantiate an APODData for a picture type APOD
			Bitmap bmp = HttpFetch.getBitmap(src);
			apodData = new APODData(
					apodContentType,
					date,
					bmp,
					src,
					page,
					domainRoot + path,
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
					domainRoot + path,
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
					domainRoot + path,
					description,
					error);
			break;

		case NONE:
			apodData = null;
			break;
		}

		return apodData;
	}

	// Parse the HTML file to extract the APOD description and remove unnecessary text
	private String getDescription(String pageIn, String element)
	{
		String page = pageIn;
		
		// Remove the IMG element
		int start = page.toUpperCase().indexOf("<"+element.toUpperCase());
		if(start == -1)
			return page;

    	start = page.substring(start).toUpperCase().indexOf(">")+start+1;
    	int stop = page.substring(start).toUpperCase().indexOf("<HR>")+start;
    	page = "<html>" + page.substring(start, stop) + "</html>";

    	return page;
	}
}
