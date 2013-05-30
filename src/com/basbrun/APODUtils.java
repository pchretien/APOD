package com.basbrun;

public class APODUtils 
{
	public static final int apiLevel = 3;	
	public static final String APOD_TAG = "apod";
	
	public static int getRunningApiLevel()
	{
		return Integer.parseInt(android.os.Build.VERSION.SDK);
	}
}
