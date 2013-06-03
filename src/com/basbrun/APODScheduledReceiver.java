package com.basbrun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class APODScheduledReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		APODService.ScheduleService(context, 60 * 1000, 60 * 60 * 1000);
	}

}
