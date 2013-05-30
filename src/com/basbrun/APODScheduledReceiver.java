package com.basbrun;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class APODScheduledReceiver extends BroadcastReceiver
{
	// Restart service every 30 seconds
	  private static final long REPEAT_TIME = 1000 * 30;

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		// Starting time ... 30 seconds after this line is executed...
	    Calendar calendarStartTime = Calendar.getInstance();
	    calendarStartTime.add(Calendar.SECOND, 30);
	    
	    // Get a reference to the AlarmManager system service
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    
		// The definition of the service to start ...
		Intent serviceIntent = new Intent(context, APODStartReceiver.class);
	    PendingIntent servicePendingIntend = PendingIntent.getBroadcast(context, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	    	    
	    // Add the service to the AlarmManager 
	    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendarStartTime.getTimeInMillis(), REPEAT_TIME, servicePendingIntend);
	}

}
