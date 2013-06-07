package com.basbrun;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class APODWallpaperChangedReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		preferences.edit().putLong(
				"last_wallpaper_update", 
				Calendar.getInstance().getTimeInMillis()/APODUtils.MILLIS_PER_DAY).commit();
		
	}

}
