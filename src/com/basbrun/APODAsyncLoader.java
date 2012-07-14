package com.basbrun;

import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

//Load the requested APOD asynchronously 
class APODAsyncLoader extends AsyncTask<Calendar, Void, Void>
{
	// The calling Activity
	Activity activity;
	
	// Reference to the APODApplication to get access to the APODDataProvider
	private APODApplication app = null;
	
	// The ProgressDialog created by the calling Activity
	ProgressDialog progressDialog;
	
	// Minimum time to wait to load the picture
	long milliseconds = 0;
		
	public APODAsyncLoader(
			Activity activity, 
			ProgressDialog progressDialog,
			long milliseconds)
	{
		this.activity = activity;
		this.progressDialog = progressDialog;
		this.milliseconds = milliseconds;
		
		// Get a reference to the Application main class
        app = (APODApplication)activity.getApplication();
	}
	
	@Override
	protected Void doInBackground(Calendar... params)
	{
		Calendar timer = Calendar.getInstance();
		
		// Never load an APOD for a date after today ...
		Calendar newDate = params[0];
		int y = newDate.get(Calendar.YEAR);
		int m = newDate.get(Calendar.MONTH);
		int d = newDate.get(Calendar.DATE);
			
		if(y<1995)
			return null;
		
		if(y == 1995 && m < 5)
			return null;
		
		if(y == 1995 && m == 5 && d < 20 && d != 16)
			return null;
		
		app.getDataProvider().getAPODByDate(newDate);
		
		try
		{
			while(Calendar.getInstance().getTimeInMillis() - timer.getTimeInMillis() < milliseconds)
				Thread.sleep(100);
		}
		catch(Exception ex)
		{			
		}
		
		return null;
	}

	protected void onPostExecute(Void result)
	{
		if(progressDialog != null)
			progressDialog.dismiss();

		activity.startActivity(new Intent(activity, APODActivity.class));
        activity.finish();
	}
}