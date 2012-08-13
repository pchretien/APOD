package com.basbrun;

public class APODUtils 
{
	public static final int apiLevel = 3;
	
	public static int getRunningApiLevel()
	{
		return Integer.parseInt(android.os.Build.VERSION.SDK);
	}
}
