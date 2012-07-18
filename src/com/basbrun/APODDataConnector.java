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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

// Connect to the internet to download web pages and pictures. 
// The data connector also provide simple caching to save on bandwidth
public class APODDataConnector
{
	private static String domainRoot = "http://apod.nasa.gov/apod/";
	public static String getDomainRoot()
	{
		return domainRoot;
	}
	
	// Get the HTML page content for the date received in parameter...
	public static String GetHtml(Calendar date, boolean caching)
	{
		String pageSource;	
		
		// Build the filename from the date
		String filename = APODDataConnector.formatFileName(date, "html", "ap");
			
		// Check if the object is in the cache
		if(caching)
		{
			pageSource = getHtmlFromCache(filename);
			
			if(pageSource != null)
				return pageSource;
		}

		// If not in cache, download the page
		try
        {
        	URL url = new URL(domainRoot + filename);
        	InputStream is = url.openStream();
        	InputStreamReader isr = new InputStreamReader(is);
        	BufferedReader br = new BufferedReader(isr);

        	String line = "";
        	pageSource = "";
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

		// If caching is activated, save the downloaded page to the cache
		if(caching)
			saveHtmlToCache(filename, pageSource);
		
		return pageSource;
	}
	
	// Retreive an html file from the cache
	private static String getHtmlFromCache(String filename)
	{	
		try
		{
			FileInputStream fstream = new FileInputStream(checkApodDirectory() + File.separator + filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));			
			
			//Read File Line By Line
			String line;
			String html = "";
			while ((line = br.readLine()) != null)   
				html += line;
			
			if(in != null)
				in.close();
			
			return html;

		}
		catch(Exception ex)
		{	
		}
		
		return null;
	}
	
	// Save an html file to the cache
	private static void saveHtmlToCache(String filename, String html)
	{
		try 
		{
			String fullPath = checkApodDirectory() + File.separator + filename;
			FileWriter fr = new FileWriter(new File(fullPath));
			BufferedWriter out = new BufferedWriter(fr);
			out.write(html);
			out.close();
		} 
		catch (Exception ex) 
		{ 
			return;
		}
	}

	// Download a bitmap from the web
	public static Bitmap getBitmap(Calendar date, String src, boolean caching)
	{
		Bitmap bmp = null;
		
		// Check for the bitmap in the cache
		if(caching)
		{
			bmp = getBitmapFromCache(date);
			if(bmp != null)
				return bmp;
		}
		
		// If not in the cache, download form the web
		try
		{
			URL url = new URL(src);
			InputStream is = url.openStream();
			bmp = BitmapFactory.decodeStream(is);
			
			// If caching is activated, save the bitmap to the cache
			if(caching)
				saveBitmapToCache(bmp, date);
			
			return bmp;
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
	// Read a bitmap from the cache
	private static Bitmap getBitmapFromCache(Calendar date)
	{
		String dir = checkApodDirectory();  
		String filename = formatFileName(date, "jpg", "");		

		return BitmapFactory.decodeFile(dir + File.separator + filename);
	}
	
	// Check if a bitmap is in the cache
	private static boolean isBitmapCached(Calendar date)
	{
		String dir = checkApodDirectory();  
		String filename = formatFileName(date, "jpg", "");	
		File file = new File(dir + File.separator + filename);
		return file.exists();
	}
	
	// Get the full path of the image in the cache to load the WebView
	public static String getBitmapPathFromCache(Calendar date)
	{
		if(!isBitmapCached(date))
			return null;
		
		return checkApodDirectory() + File.separator + formatFileName(date, "jpg", "");
	}

	// Add a bitmap to the cache
	private static void saveBitmapToCache(Bitmap bmp, Calendar date)
	{
		if(bmp == null)
			return;
		
		try
		{
			String filename = formatFileName(date, "jpg", "");			
			File imageFile = new File( checkApodDirectory() + File.separator + filename);
			FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
		}
		catch(Exception ex)
		{			
		}
	}
	
	// Validate tht the APOD cache directory exist. If it does not exist, it
	// is created and the path returned to the caller
	private static String checkApodDirectory() 
	{
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "APOD";
		File dirFile = new File(dir);
		if(!dirFile.exists())
			dirFile.mkdir();
		
		return dir;
	}
	
	// Build the filename of the html page to download using the date of the requested APOD
	public static String formatFileName(Calendar date, String extension, String prefix)
	{
		// Check if the picture exist on disk ...
		String filename = String.format(
		prefix+"%02d%02d%02d."+extension, 
		(date.get(Calendar.YEAR)<2000)?date.get(Calendar.YEAR)-1900:date.get(Calendar.YEAR)-2000, 
		date.get(Calendar.MONTH)+1, 
		date.get(Calendar.DATE));
		
		return filename;
	}
	
	// Erase all files in the cache
	public static void clearCache()
	{
		File directory = new File(checkApodDirectory());
		File[] files = directory.listFiles();	
		
		if(files == null)
			return;
		
		for(File file : files)
		{
			file.delete();
		}
	}
}
