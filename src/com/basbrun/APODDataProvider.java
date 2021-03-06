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
import java.util.List;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

// The APODDataProvider is the link between the application and the APOD website 
// where all the data is. This class is instantiated in the APODApplication class 
// as a singleton.
public class APODDataProvider
{
	private Calendar lastAPOD;
	
	private APODData apodData = null;	
	private SharedPreferences preferences = null;
	private WebDataConnector dataConnector= null;
	
	private String searchQuery;
	private List<APODSearchItem> searchResults;
	
	public APODDataProvider(SharedPreferences preferences)
	{
		lastAPOD = Calendar.getInstance();
		
		this.preferences = preferences;
		dataConnector = new WebDataConnector(APODData.getDomainRoot(), APODData.getCachingDirectory());
	}
	
	// Return the APOD URL root
	public String getAPODRoot()
	{
		return dataConnector.getDomainRoot();
	}

	// Return the current APOD data
	public APODData getAPOD()
	{
		// If no APOD has been loaded a call to the web server is made to get the data
		if(apodData == null)
			getAPODByDate(GregorianCalendar.getInstance());

		return apodData;
	}
	
	public APODData getAPODByFilename(String filename)
	{
		if(filename == null || filename.length() == 0)
			return getAPODByDate(null);
		
		// Extract the date form the filename
		// Format is apYYMMDD.html		
		int year = Integer.parseInt(filename.substring(2, 4));
		
		// This rule is good until 2094 ... :)
		if(year > 94)
			year += 1900;
		else
			year += 2000;
		
		int month = Integer.parseInt(filename.substring(4, 6));
		int day = Integer.parseInt(filename.substring(6, 8));
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.DATE, day);
		
		return getAPODByDate(calendar);
	}
	
	public boolean isAPODUpToDate(Calendar date)
	{
		if( apodData != null && 
			apodData.getDate().get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
			apodData.getDate().get(Calendar.MONTH) == date.get(Calendar.MONTH) &&
			apodData.getDate().get(Calendar.DATE) == date.get(Calendar.DATE))
		{
			return true;
		}
		
		return false;
	}

	// Return the APOD data for a specific day
	public APODData getAPODByDate(Calendar date)
	{
		if(isAPODUpToDate(date))
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
		page = dataConnector.GetHtml(
				preferences.getBoolean("caching", true), 
				new APODFormatter(date, "ap", "html"));
		
		if(page == null)
		{
			apodContentType = APODData.ApodContentType.ERROR;
			error = APODApplication.getAppContext().getString(R.string.connection_error);
		}
		else
		{
			// Make sure we have the current date ...
			if(date == null)
			{
				date = APODHtmlParser.getCurrentDate(page);
				lastAPOD = date;
				
				if(preferences.getBoolean("caching", true))
				{
					// Call it again so the file is cached ..
					page = dataConnector.GetHtml(
						preferences.getBoolean("caching", true), 
						new APODFormatter(date, "ap", "html"));
				}
			}
			
			// Parse the page to find an <IMG/> element
			src = APODHtmlParser.FindFirstElementSrc(page, "IMG");

			if(src != null)
			{
				// Load the HTML text and set the APOD type
				src = dataConnector.getDomainRoot() + src;
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
			dataConnector.getDomainRoot() + new APODFormatter(date, "ap", "html").formatFilename(),
			description,
			error);		
		
		switch(apodContentType)
		{
		case IMG:
			// This is an image type APOD
			Bitmap bmp = dataConnector.getBitmap(
					src, 
					preferences.getBoolean("caching", true),
					new APODFormatter(date, "", "jpg"));				
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
		if(date == null)
			return true;
		
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
				dataConnector.getDomainRoot() + new APODFormatter(date, "ap", "html").formatFilename(),
				description,
				"There are no APOD before June 16th 1995 and for June 17th, 18th and 19th 1995");	
			
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
				dataConnector.getDomainRoot() + new APODFormatter(date, "ap", "html").formatFilename(),
				description,
				"Travelling to the future isn't possible yet!");	
			
			return false;
		}
		
		return true;
	}
	
	// Return the full ath of the bitmap in cache to load into the WebView
	public String getBitmapPathFromCache(Calendar date)
	{
		return dataConnector.getBitmapPathFromCache(date, new APODFormatter(date, "", "jpg"));		
	}
	
	public List<APODSearchItem> searchAPOD(String query)
	{
		WebDataConnector webDataConnector = new WebDataConnector("http://apod.nasa.gov/cgi-bin/apod/", null);
		String page = webDataConnector.GetHtml(false, new APODSearchFormatter(query));
		
		// Most probably a connection error ...
		if(page == null)
			return null;
		
		List<APODSearchItem> list = APODHtmlParser.parseSearchResults(page);		
		return list;
	}

	public List<APODSearchItem> getSearchResults() 
	{
		return searchResults;
	}

	public void setSearchResults(List<APODSearchItem> searchResults) 
	{
		this.searchResults = searchResults;
	}

	public String getSearchQuery() 
	{
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) 
	{
		this.searchQuery = searchQuery;
	}
	
	public boolean isTodayAPOD()
	{
		int currentYear = lastAPOD.get(Calendar.YEAR);
		int currentMonth = lastAPOD.get(Calendar.MONTH);
		int currentDay = lastAPOD.get(Calendar.DATE);
		
		int apodYear = apodData.getDate().get(Calendar.YEAR);
		int apodMonth = apodData.getDate().get(Calendar.MONTH);
		int apodDay = apodData.getDate().get(Calendar.DATE);
		
		if(currentYear == apodYear && currentMonth == apodMonth && currentDay == apodDay)
			return true;
		
		return false;		
	}
}
