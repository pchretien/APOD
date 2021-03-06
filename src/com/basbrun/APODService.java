package com.basbrun;

import java.util.Calendar;
import java.util.Random;

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
		Log.d(APODUtils.APOD_TAG, "APODService.onStartCommand()");
		
		Calendar now = Calendar.getInstance();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String time = preferences.getString("preferences_start_time", "08:00");
		
		int hour = Integer.parseInt(time.split(":")[0]);
		int minutes = Integer.parseInt(time.split(":")[1]);
				
		Calendar wallPaperTime = Calendar.getInstance();
		wallPaperTime.set(Calendar.HOUR_OF_DAY, hour);
		wallPaperTime.set(Calendar.MINUTE, minutes);
		wallPaperTime.set(Calendar.SECOND, 0);
		wallPaperTime.set(Calendar.MILLISECOND, 0);
		
		// If not the right time ... return
		if(	now.getTimeInMillis() - wallPaperTime.getTimeInMillis() > 1000*60 ||
			now.getTimeInMillis() - wallPaperTime.getTimeInMillis() < 0)
			return returnedFlag;
		
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
		
			Calendar dateTime = Calendar.getInstance();	
			boolean randomWallpaper = preferences.getBoolean("random_wallpaper", false);
			if(randomWallpaper)
				dateTime = APODUtils.getRandomApodDate();
			
			APODData apodData = dataProvider.getAPODByDate(dateTime);
			if(apodData == null)
			{
				Log.e(APODUtils.APOD_TAG, "APODService.onStartCommand(): Failed to load APOD");
				return returnedFlag;
			}
			
			int safetyCounter = 0;
			while(randomWallpaper && apodData.getBitmap() == null)
			{
				dateTime = APODUtils.getRandomApodDate();
				apodData = dataProvider.getAPODByDate(dateTime);
				
				if(apodData == null)
				{
					Log.e(APODUtils.APOD_TAG, "APODService.onStartCommand(): Failed to load APOD");
					return returnedFlag;
				}
				
				if(safetyCounter++ > 5)
					break;
			}
		
			// Update the wallpaper
			android.app.WallpaperManager wallpaperManager = android.app.WallpaperManager.getInstance(APODService.this);
			Bitmap apodBitmap = apodData.getBitmap();
			if(apodBitmap != null)
			{
				wallpaperManager.setBitmap(apodData.getBitmap());
				APODUtils.SaveLastWallpaperDate(APODApplication.getAppContext(), apodData.getDate());
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
		new APODAsyncServiceScheduler(context).execute();
	}

	public int getRandomInt()
	{
		Random rnd = new Random();
		int i = rnd.nextInt();
		Log.d(APODUtils.APOD_TAG, "APODService.getRandomInt() = " + Integer.toString(i));
		return i;
	}
}
