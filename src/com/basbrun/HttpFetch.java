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

import java.io.*;
import java.net.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class HttpFetch
{
	// Get an HTML page content ...
	public static String GetHtml(String path)
	{
		String pageSource = "";

		try
        {
        	URL url = new URL(path);
        	InputStream is = url.openStream();
        	InputStreamReader isr = new InputStreamReader(is);
        	BufferedReader br = new BufferedReader(isr);

        	String line = "";
			while ((line = br.readLine()) != null)
				pageSource += line;

			br.close();
			isr.close();
			is.close();
        }
        catch(Exception ex)
        {
        	return null;
        }

		return pageSource;
	}

	// Download a bitmap ...
	public static Bitmap getBitmap(String src)
	{
		try
		{
			URL url = new URL(src);
			InputStream is = url.openStream();
			Bitmap bmp = BitmapFactory.decodeStream(is);
			return bmp;
		}
		catch(Exception ex)
		{
			return null;
		}
	}
}
