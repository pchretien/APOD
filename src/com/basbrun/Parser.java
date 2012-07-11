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

public class Parser
{
	// Find the first occurence of a specific element and return it's SRC attribute value
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
}
