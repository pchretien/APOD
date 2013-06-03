package com.basbrun;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class APODUtils 
{
	public static final int apiLevel = 3;	
	public static final String APOD_TAG = "apod";
	public static final long MILLIS_PER_DAY = 1000 * 60 * 60 * 24;
	
	public static int getRunningApiLevel()
	{
		return Integer.parseInt(android.os.Build.VERSION.SDK);
	}
	
	public static boolean isConnected(Context context) 
	{
	    ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    
	    if (connectivityManager.getActiveNetworkInfo() != null && 
	    		connectivityManager.getActiveNetworkInfo().isAvailable() && 
	    		connectivityManager.getActiveNetworkInfo().isConnected()) 
	    {
            return true;

        } else 
        {
            return false;
        }
	}
	
	public static boolean isConnectedWifi(Context context) 
	{
	    ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    
	    NetworkInfo networkInfo = null;
	    if (connectivityManager != null) 
	    {
	        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    }
	    
	    return networkInfo == null ? false : networkInfo.isConnected();
	}
}
