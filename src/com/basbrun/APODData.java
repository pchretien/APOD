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

import android.graphics.Bitmap;

public class APODData
{
	// Type of APOD contend. IFRAME refer to YouTybe videos most of the time
	public enum ApodContentType
	{
		IMG,
		IFRAME,
		NONE,
		ERROR
	}

	// The type of the current APOD
	private ApodContentType apodDataType;
	
	// The date of the currently displayed APOD
	private Calendar date;

	// The image, if any, of the current APOD picture
	private Bitmap bitmap;
	
	// The source url of the current APOD
	private String src;

	// The html content of the current APOD page
	private String page;
	
	// The full url of the current APOD web page
	private String pagePath;
	
	// A modified version of the HTML content of the current APOD
	private String description;

	// Error message if any
	private String error;

	// Ctor
	public APODData(
			ApodContentType apodDataType,
			Calendar date,
			Bitmap bitmap,
			String src,
			String page,
			String pagePath,
			String description,
			String error)
	{
		this.apodDataType = apodDataType;
		this.date = date;
		this.bitmap = bitmap;
		this.src = src;
		this.page = page;
		this.pagePath = pagePath;
		this.description = description;
		this.error = error;
	}

	public Calendar getDate() {
		return date;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public String getPage() {
		return page;
	}
	public String getPagePath() {
		return pagePath;
	}
	public String getSrc() {
		return src;
	}
	public ApodContentType getApodDataType() {
		return apodDataType;
	}
	public String getError() {
		return error;
	}
	public String getDescription() {
		return description;
	}
}
