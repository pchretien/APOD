package com.basbrun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class APODStartReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Log.d(APODUtils.APOD_TAG, "APODStartReceiver.onReceive");
		
		Intent service = new Intent(context, APODService.class);
	    context.startService(service);
	}

}
