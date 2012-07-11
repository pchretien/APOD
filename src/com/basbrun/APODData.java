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
	public enum ApodContentType
	{
		IMG,
		IFRAME,
		NONE,
		ERROR
	}

	private ApodContentType apodDataType;

	private Calendar date;

	private Bitmap bitmap;
	private String src;

	private String page;
	private String pagePath;
	private String description;

	private String error;

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
