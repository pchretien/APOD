package com.basbrun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

// This receiver is called when the devices boot up sequence complete
public class APODScheduledReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Log.d(APODUtils.APOD_TAG,"APODScheduledReceiver.onReceive");
		APODService.ScheduleService(context);
	}

}
