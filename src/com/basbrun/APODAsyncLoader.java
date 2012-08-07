package com.basbrun;

import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

//Load the requested APOD asynchronously 
class APODAsyncLoader extends AsyncTask<Void, Void, Void>
{
	// Date of the APOD to load
	private Calendar date;
	
	// Filename of the APOD to load
	private String filename;
	
	// The calling Activity is required start the new 
	// APODActivity and stop the current one.
	private Activity activity;
	
	// Reference to the APODApplication to get access to the APODDataProvider
	private APODApplication app = null;
	
	// The ProgressDialog created by the calling Activity
	private ProgressDialog progressDialog;
	
	// Minimum time to wait to load the picture
	private long milliseconds = 0;
		
	// Ctor
	public APODAsyncLoader(
			Calendar date,
			Activity activity, 
			ProgressDialog progressDialog,
			long milliseconds)
	{
		this.date = date;
		this.activity = activity;
		this.progressDialog = progressDialog;
		this.milliseconds = milliseconds;
		
		// Get a reference to the Application main class
        app = (APODApplication)activity.getApplication();
	}
	
	// Ctor
		public APODAsyncLoader(
				String filename,
				Activity activity, 
				ProgressDialog progressDialog,
				long milliseconds)
		{
			this.filename = filename;
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
	protected Void doInBackground(Void ... params)
	{
		// Start a timer for the splash screen
		Calendar timer = Calendar.getInstance();
		
		// Load the APOD for the date received in parameter
		if(date != null)
			app.getDataProvider().getAPODByDate(date);
		else if(filename != null)
			app.getDataProvider().getAPODByFilename(filename);
		else
			app.getDataProvider().getAPODByFilename(""); // Should load the default page
		
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
	@Override
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