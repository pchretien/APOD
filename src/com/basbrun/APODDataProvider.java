## APOD ##
#
# This project is an Android client to visualize the Astronomy Picture Of The
# Day (APOD) published by NASA at http://apod.nasa.gov/apod/. This project is
# free of charges and have been developed with the objective to learn Android
# programmation.
#
# Copyright (C) 2012  Philippe Chretien
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License Version 2
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
#
# You will find the latest version of this code at the following address:
# http://github.com/pchretien
#
# You can contact me at the following email address:
# philippe.chretien at gmail.com

package com.basbrun;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.graphics.Bitmap;


public class APODDataProvider
{
	private String domainRoot = "http://apod.nasa.gov/apod/";
	public String getDomainRoot()
	{
		return domainRoot;
	}

	private APODData apodData = null;

	public APODData getAPOD()
	{
		if(apodData == null)
			getAPODByDate(GregorianCalendar.getInstance());

		return apodData;
	}

	public APODData getAPODByDate(Calendar date)
	{
		int iYear = (date.get(Calendar.YEAR)<2000)?date.get(Calendar.YEAR)-1900:date.get(Calendar.YEAR)-2000;
		int iMonth = date.get(Calendar.MONTH)+1; // Month is zero based ...
		int iDay = date.get(Calendar.DATE);

		String path = String.format("ap%02d%02d%02d.html", iYear, iMonth, iDay);

		if(apodData != null && apodData.getDate().equals(date))
			return apodData;

		String src = null;
		String error = "";
		String description = "";
		APODData.ApodContentType apodContentType = APODData.ApodContentType.NONE;

		String page = HttpFetch.GetHtml(domainRoot + path);
		if(page == null)
		{
			apodContentType = APODData.ApodContentType.ERROR;
			error = "Unable to load page " + domainRoot + path;
		}

		if(page != null)
		{
			src = Parser.FindFirstElementSrc(page, "IMG");

			if(src != null)
			{
				src = domainRoot + src;
				description = this.getDescription(domainRoot + path, "IMG");
				apodContentType = APODData.ApodContentType.IMG;
			}
			else
			{
				src = Parser.FindFirstElementSrc(page, "IFRAME");

				if(src != null)
				{
					description = this.getDescription(domainRoot + path, "IFRAME");
					apodContentType = APODData.ApodContentType.IFRAME;
				}
			}

			if(src == null)
				apodContentType = APODData.ApodContentType.NONE;
		}

		switch(apodContentType)
		{
		case IMG:
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

	private String getDescription(String path, String element)
	{
		String page = HttpFetch.GetHtml(path);

		// Remove the IMG element
		int start = page.toUpperCase().indexOf("<"+element.toUpperCase());
		if(start == -1)
			return page;

    	int stop = page.substring(start).toUpperCase().indexOf(">")+start+1;
    	page = page.substring(0, start) + page.substring(stop);

    	// Remove the Site Title
    	start = page.toUpperCase().indexOf("<CENTER>");
		if(start == -1)
			return page;

    	stop = page.substring(start).toUpperCase().indexOf("</CENTER>")+start+9;
    	page = page.substring(0, start) + page.substring(stop);

    	return page;
	}
}
