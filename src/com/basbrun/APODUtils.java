package com.basbrun;

import java.util.Calendar;
import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

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
	
	public static Calendar getRandomApodDate()
	{
		Calendar newCalendar = Calendar.getInstance();
		
		Calendar today = Calendar.getInstance();
		int currentYear = today.get(Calendar.YEAR);
		int currentMonth = today.get(Calendar.MONTH);
		int currentDay = today.get(Calendar.DATE);
		
		int firstApodYear = 1995;
		int firstApodMonth = 5;
		int firstApodDay = 20;
		
		Random rnd = new Random();
		int newYear = rnd.nextInt(currentYear-firstApodYear+1)+firstApodYear;
		
		int minMonth = 0;
		int maxMonth = 11;
		if(newYear == firstApodYear)
			minMonth = firstApodMonth;
		if(newYear == currentYear)
			maxMonth = currentMonth;
		int newMonth = rnd.nextInt(maxMonth-minMonth+1)+minMonth;
		
		newCalendar.set(newYear, newMonth, 1);
		
		int minDay = 1;
		int maxDay = newCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		if(newYear == firstApodYear && newMonth == firstApodMonth)
			minDay = firstApodDay;
		if(newYear == currentYear && newMonth == currentMonth)
			maxDay = currentDay;
		int newDay = rnd.nextInt(maxDay-minDay+1)+minDay;
		
		newCalendar.set(newYear, newMonth, newDay);
		return newCalendar;
	}
	
	public static void SaveLastWallpaperDate(Context context, Calendar dateTime)
	{
		int year = dateTime.get(Calendar.YEAR);
		int month = dateTime.get(Calendar.MONTH);
		int date = dateTime.get(Calendar.DATE);
		
		String dateTimeToString = String.format("%d/%d/%d", year, month, date);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		preferences.edit().putString("wallpaper_date", dateTimeToString).commit();
	}
	
	public static Calendar getWallpaperDate(Context context)
	{
		Calendar wallpaperDate = Calendar.getInstance();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String dateTimeToString = preferences.getString("wallpaper_date", null);
		if(dateTimeToString == null)
			return wallpaperDate;
		
		wallpaperDate.set(Calendar.YEAR,  Integer.parseInt(dateTimeToString.split("/")[0]));
		wallpaperDate.set(Calendar.MONTH,  Integer.parseInt(dateTimeToString.split("/")[1]));
		wallpaperDate.set(Calendar.DATE,  Integer.parseInt(dateTimeToString.split("/")[2]));
		
		return wallpaperDate;
	}
}
