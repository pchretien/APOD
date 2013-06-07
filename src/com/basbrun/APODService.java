package com.basbrun;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class APODService extends Service
{
	private final IBinder binder = new APODServiceBinder();
	private static APODDataProvider dataProvider = null;
	
	public class APODServiceBinder extends Binder 
	{
		APODService getService() 
	    {
	    	return APODService.this;
	    }
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		Log.d(APODUtils.APOD_TAG, "APODService.onStart()");
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		int returnedFlag = Service.START_STICKY;
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean runAutoWallpaper = preferences.getBoolean("auto_wallpaper", false);
		if(!runAutoWallpaper)
		{
			Log.d(APODUtils.APOD_TAG, "APODService.onStartCommand(): auto_wallpaper=off");
			return returnedFlag;
		}
		
		Log.d(APODUtils.APOD_TAG, "APODService.onStartCommand(): auto_wallpaper=on");
		
		boolean wifiOnly = preferences.getBoolean("auto_wallpaper_wifi_only", true);
		if(wifiOnly && !APODUtils.isConnectedWifi(getApplicationContext()))
		{
			Log.d(APODUtils.APOD_TAG, "APODService.onStartCommand(): WiFi not available");
				return returnedFlag;
		}
		
		try
		{
			if(dataProvider == null)
				dataProvider = new APODDataProvider(preferences);
		
			Calendar today = Calendar.getInstance();
			
			long lastUpdate = preferences.getLong("last_wallpaper_update", 0);
			if(today.getTimeInMillis()/APODUtils.MILLIS_PER_DAY == lastUpdate)
			{
				Log.d(APODUtils.APOD_TAG, "APODService.onStartCommand(): Current wallpaper is up to date");
				return returnedFlag;
			}
			
			APODData apodData = dataProvider.getAPODByDate(today);
			if(apodData == null)
			{
				Log.e(APODUtils.APOD_TAG, "APODService.onStartCommand(): Failed to load APOD");
				return returnedFlag;
			}
		
			// Update the wallpaper
			android.app.WallpaperManager wallpaperManager = android.app.WallpaperManager.getInstance(APODService.this);
			Bitmap apodBitmap = apodData.getBitmap();
			if(apodBitmap != null)
			{
				wallpaperManager.setBitmap(apodData.getBitmap());
			}
			else
			{
				Log.i(APODUtils.APOD_TAG, "The APOD for today is not a picture");
			}
		}
		catch(Exception ex)
		{
			Log.e(APODUtils.APOD_TAG, "APODService.onStartCommand()", ex);
		}
		
		return returnedFlag;		
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		Log.d(APODUtils.APOD_TAG, "APODService.onBind()");
		return binder;
	}
	
	public static void ScheduleService(Context context)
	{
		new APODAsyncServiceScheduler(context, 30*1000, 30*1000).execute();
	}

	public int getRandomInt()
	{
		Random rnd = new Random();
		int i = rnd.nextInt();
		Log.d(APODUtils.APOD_TAG, "APODService.getRandomInt() = " + Integer.toString(i));
		return i;
	}
}
