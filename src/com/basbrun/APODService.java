package com.basbrun;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class APODService extends Service
{
	private final IBinder binder = new APODServiceBinder();
	
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
		Log.d(APODUtils.APOD_TAG, "APODService.onStartCommand()");
		return Service.START_STICKY;		
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		Log.d(APODUtils.APOD_TAG, "APODService.onBind()");
		return binder;
	}

	public int getRandomInt()
	{
		Random rnd = new Random();
		int i = rnd.nextInt();
		Log.d(APODUtils.APOD_TAG, "APODService.getRandomInt() = " + Integer.toString(i));
		return i;
	}
}
