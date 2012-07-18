package com.basbrun;

import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

//Load the requested APOD asynchronously 
class APODAsyncLoader extends AsyncTask<Calendar, Void, Void>
{
	// The calling Activity is required start the new 
	// APODActivity and stop the current one.
	Activity activity;
	
	// Reference to the APODApplication to get access to the APODDataProvider
	private APODApplication app = null;
	
	// The ProgressDialog created by the calling Activity
	ProgressDialog progressDialog;
	
	// Minimum time to wait to load the picture
	long milliseconds = 0;
		
	// Ctor
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
	
	// This is where the asyn loading is made. This code runs in
	// a separate thread. The onPostExecute method is called when the
	// task is done
	@Override
	protected Void doInBackground(Calendar... params)
	{
		// Start a timer for the splash screen
		Calendar timer = Calendar.getInstance();
		
		// Load the APOD for the date received in parameter
		Calendar newDate = params[0];		
		app.getDataProvider().getAPODByDate(newDate);
		
		// Wait until the splash screen delay expires
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

	// Called when getAPODByDate() is done
	protected void onPostExecute(Void result)
	{
		// Turn off the spinner if any
		if(progressDialog != null)
			progressDialog.dismiss();

		// Start a new APODActivity for the new APODData loaded
		activity.startActivity(new Intent(activity, APODActivity.class));
		
		// Terminate the current APODActivity
        activity.finish();
	}
}