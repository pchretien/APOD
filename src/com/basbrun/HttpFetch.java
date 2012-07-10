
package com.basbrun;

import java.io.*;
import java.net.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class HttpFetch 
{	
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
