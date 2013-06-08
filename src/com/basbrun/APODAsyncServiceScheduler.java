package com.basbrun;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class APODAsyncServiceScheduler extends AsyncTask<Void, Void, Void>
{
	private Context context = null;
	private int start= 10*1000;
	private long repeat = 30*1000;

	public APODAsyncServiceScheduler(Context context, int start, long repeat)
	{
		
		this.context = context;
		this.start = start;
		this.repeat = repeat;
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		Log.d(APODUtils.APOD_TAG, "APODAsyncServiceScheduler.onPostExecute");
	}

	@Override
	protected void onPreExecute()
	{
		Log.d(APODUtils.APOD_TAG, "APODAsyncServiceScheduler.onPreExecute");
	}

	@Override
	protected Void doInBackground(Void... params)
	{
		Log.d(APODUtils.APOD_TAG, "APODAsyncServiceScheduler.doInBackground");
		/*
		// Starting time ... 30 seconds after this line is executed...
	    Calendar calendarStartTime = Calendar.getInstance();
	    calendarStartTime.add(Calendar.MILLISECOND, start);
	    
	    // Get a reference to the AlarmManager system service
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    
		// The definition of the service to start ...
		Intent serviceIntent = new Intent(context, APODStartReceiver.class);
	    PendingIntent servicePendingIntend = PendingIntent.getBroadcast(context, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	    	    
	    // Clear previously registered events ...
	    alarmManager.cancel(servicePendingIntend);
	    
	    // Add the service to the AlarmManager 
	    alarmManager.setInexactRepeating(
	    		AlarmManager.RTC_WAKEUP, 
	    		calendarStartTime.getTimeInMillis(), 
	    		repeat, 
	    		servicePendingIntend);
	    */
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 6);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent serviceIntent = new Intent(context, APODStartReceiver.class);
	    PendingIntent servicePendingIntend = PendingIntent.getBroadcast(context, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, servicePendingIntend);
		return null;
	}

}
