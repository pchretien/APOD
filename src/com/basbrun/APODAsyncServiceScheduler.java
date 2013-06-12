package com.basbrun;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class APODAsyncServiceScheduler extends AsyncTask<Void, Void, Void>
{
	private Context context = null;

	public APODAsyncServiceScheduler(Context context)
	{
		
		this.context = context;
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
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String time = preferences.getString("preferences_start_time", "08:00");
		
		int hour = Integer.parseInt(time.split(":")[0]);
		int minutes = Integer.parseInt(time.split(":")[1]);
				
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minutes);
		calendar.set(Calendar.SECOND, 0);
		
		if( Calendar.getInstance().compareTo(calendar) >= 0)
			calendar.add(Calendar.DATE, 1);
		
		Intent serviceIntent = new Intent(context, APODStartReceiver.class);
	    PendingIntent servicePendingIntent = PendingIntent.getBroadcast(context, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	    
	    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    alarmManager.cancel(servicePendingIntent);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, servicePendingIntent);
		
		return null;
	}

}
