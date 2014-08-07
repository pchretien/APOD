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
public class WebDataConnector
{	
	private String domainRoot = "";
	private String cachingDirectory = "";
	
	public WebDataConnector(String domainRoot, String cachingDirectory)
	{
		this.domainRoot = domainRoot;
		this.cachingDirectory = cachingDirectory;
	}
	
	public String getDomainRoot()
	{
		return domainRoot;
	}
	
	// Get the HTML page content for the date received in parameter...
	public String GetHtml(boolean caching, IWebDataConnectorFormatter formatter)
	{
		String pageSource;	
		
		// Build the filename from the date
		String filename = formatter.formatFilename();
			
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
        	InputStreamReader isr = new InputStreamReader(is, "ISO-8859-1");
        	BufferedReader br = new BufferedReader(isr);

        	String line = "";
        	pageSource = "";
        	StringBuffer stringBuffer = new StringBuffer();
			while ((line = br.readLine()) != null)
				stringBuffer.append(line);

			pageSource = stringBuffer.toString();
			
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
	private String getHtmlFromCache(String filename)
	{	
		if(filename == null || filename.length() == 0)
			return null;
		
		try
		{
			FileInputStream fstream = new FileInputStream(checkCachingDirectory() + File.separator + filename);
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
	private void saveHtmlToCache(String filename, String html)
	{
		if(filename == null || filename.length() == 0)
			return;
		
		try 
		{
			String fullPath = checkCachingDirectory() + File.separator + filename;
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
	public Bitmap getBitmap(String src, boolean caching, IWebDataConnectorFormatter formatter)
	{
		Bitmap bmp = null;
		
		// Check for the bitmap in the cache
		if(caching)
		{
			bmp = getBitmapFromCache(formatter);
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
				saveBitmapToCache(bmp, formatter);
			
			return bmp;
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
	// Read a bitmap from the cache
	private Bitmap getBitmapFromCache(IWebDataConnectorFormatter formatter)
	{
		String dir = checkCachingDirectory();  
		String filename = formatter.formatFilename();		

		return BitmapFactory.decodeFile(dir + File.separator + filename);
	}
	
	// Check if a bitmap is in the cache
	private boolean isBitmapCached(Calendar date, IWebDataConnectorFormatter formatter)
	{
		String dir = checkCachingDirectory();  
		String filename = formatter.formatFilename();	
		File file = new File(dir + File.separator + filename);
		return file.exists();
	}
	
	// Get the full path of the image in the cache to load the WebView
	public String getBitmapPathFromCache(Calendar date, IWebDataConnectorFormatter formatter)
	{
		if(!isBitmapCached(date, formatter))
			return null;
		
		return checkCachingDirectory() + File.separator + formatter.formatFilename();
	}

	// Add a bitmap to the cache
	private void saveBitmapToCache(Bitmap bmp, IWebDataConnectorFormatter formatter)
	{
		if(bmp == null)
			return;
		
		try
		{
			String filename = formatter.formatFilename();			
			File imageFile = new File( checkCachingDirectory() + File.separator + filename);
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
	private String checkCachingDirectory() 
	{
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + cachingDirectory;
		File dirFile = new File(dir);
		if(!dirFile.exists())
			dirFile.mkdir();
		
		return dir;
	}
	
	// Erase all files in the cache
	public void clearCache()
	{
		File directory = new File(checkCachingDirectory());
		File[] files = directory.listFiles();	
		
		if(files == null)
			return;
		
		for(File file : files)
		{
			file.delete();
		}
	}
}
