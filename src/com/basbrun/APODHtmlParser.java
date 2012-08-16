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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// Custom very very basic HTML parser
// Any suggestion for a nice html parser on Android?
public class APODHtmlParser
{
	// Find the first occurrence of a specific element and return it's SRC attribute value
	public static String FindFirstElementSrc(String html, String elementType)
	{
		int elementStart = html.toUpperCase().indexOf("<"+elementType.toUpperCase());
		if(elementStart < 0)
			return null;

		String src = html.substring(elementStart);
		src = src.substring(src.toUpperCase().indexOf("SRC"));
		src = src.substring(src.toUpperCase().indexOf("\"")+1);
		src = src.substring(0, src.toUpperCase().indexOf("\""));

		return src;
	}
	
	// Parse the HTML file to extract the APOD description and remove unnecessary text
	public static String getDescription(String pageIn, String element)
	{
		String page = pageIn;
		
		// Remove the IMG element
		int start = page.toUpperCase().indexOf("<"+element.toUpperCase());
		if(start == -1)
			return page;

    	start = page.substring(start).toUpperCase().indexOf(">")+start+1;
    	int stop = page.substring(start).toUpperCase().indexOf("<HR>")+start;
    	page = "<html>" + page.substring(start, stop) + "</html>";
    	
    	page = page.replace("<a href", " <a href");
    	page = page.replace("</a>", "</a> ");

    	return page;
	}
	
	public static List<APODSearchItem> parseSearchResults(String page)
	{
		List<APODSearchItem> list = new ArrayList<APODSearchItem>();
		
		try
		{
			if(page.indexOf("Nothing found") > -1)
				return list;
			
			String pageUp = page.toUpperCase();
			int index = pageUp.indexOf("<P>");
			while(index > -1)
			{
				if(pageUp.substring(index+3).startsWith("<P>"))
					break;
				
				// Get the item full content
				int endIndex = pageUp.substring(index).indexOf("<P>", 1);
				String itemContent = page.substring(index, index+endIndex);
				
				// Get the title
				int titleIndex = 0;
				for(int i=0; i<5; i++)
					titleIndex += itemContent.substring(titleIndex).indexOf(">", 1);				
				int endTitleIndex = itemContent.substring(titleIndex).indexOf("<");
				String title = itemContent.substring(titleIndex+1, titleIndex+endTitleIndex).trim();
				
				// This is a patch to work around an encoding problem with the
				// APOD "Kepler 22b: An Almost Earth Orbiting an Almost Sun"
				if(title.indexOf("-") != -1)
				{
					// Get the date
					int dateIndex = title.indexOf(":")+1;
					int endDateIndex = title.indexOf("-");
					String date = title.substring(dateIndex, endDateIndex).trim();
					
					// Remove the date from the title
					title = title.substring(endDateIndex+1).trim();
							
					// Get the page path
					int pageIndex = itemContent.indexOf("apod.nasa.gov/apod/")+19;
					int endPageIndex = itemContent.indexOf(">", pageIndex);
					String pagePath = itemContent.substring(pageIndex, endPageIndex);
					
					// Add to the list
					list.add(new APODSearchItem(title, date, pagePath));
				}
				
				// Get the next item in the page ...
				index += pageUp.substring(index).indexOf("<P>", 1);
			}
		}
		catch(Exception ex)
		{
		}
		
		return list;
	}
	
	public static Calendar getCurrentDate(String page)
	{
		Calendar calendar = Calendar.getInstance();
		
		String token = "discuss_apod.php?date=";
		int datePos = page.indexOf(token);
		if(datePos > -1)
		{
			datePos += token.length();
			String dateString = page.substring(datePos, datePos+6);
			
			// Extract the date form the filename
			// Format is apYYMMDD.html		
			int year = Integer.parseInt(dateString.substring(0, 2));
			
			// This rule is good until 2094 ... :)
			if(year > 94)
				year += 1900;
			else
				year += 2000;
			
			int month = Integer.parseInt(dateString.substring(2, 4));
			int day = Integer.parseInt(dateString.substring(4, 6));
			
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month-1);
			calendar.set(Calendar.DATE, day);
		}
		
		return calendar;
	}
}
