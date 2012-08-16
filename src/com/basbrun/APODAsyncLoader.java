package com.basbrun;

import java.text.SimpleDateFormat;
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
	
	// Minimum time to wait to load the picture
	private long milliseconds = 0;
	
	ProgressDialog progressDialog;
	
	private Calendar timer;
		
	// Ctor
	public APODAsyncLoader(
			Calendar date,
			String filename,
			Activity activity, 
			APODApplication app,
			long milliseconds)
	{
		this.date = date;
		this.filename = filename;
		this.activity = activity;
		this.app = app;
		this.milliseconds = milliseconds;
	}
	
	// This is where the asyn loading is made. This code runs in
	// a separate thread. The onPostExecute method is called when the
	// task is done
	@Override
	protected Void doInBackground(Void ... params)
	{
		// Start a timer for the splash screen
		timer = Calendar.getInstance();
		
		// Load the APOD for the date received in parameter
		if(filename != null)
			app.getDataProvider().getAPODByFilename(filename);
		else			
			app.getDataProvider().getAPODByDate(date);
		
		
		return null;
	}

	// Called when getAPODByDate() is done
	@Override
	protected void onPostExecute(Void result)
	{
		if(progressDialog != null && progressDialog.isShowing())
		{
			try
			{
				progressDialog.dismiss();
			}
			catch(Exception ex)
			{
			}
		}
		
		// Wait until the splash screen delay expires
		try
		{
			while(Calendar.getInstance().getTimeInMillis() - timer.getTimeInMillis() < milliseconds)
				Thread.sleep(100);
		}
		catch(Exception ex)
		{			
		}

		if(activity != null )
		{
			if(activity instanceof APODActivity)
			{
				((APODActivity)activity).RefreshContent();
			}
			else if(activity instanceof APODSplashScreenActivity)
			{
				// Start a new APODActivity for the new APODData loaded
				activity.startActivity(new Intent(activity, APODActivity.class));
				
				// Terminate the current APODActivity
		        activity.finish();
			}
			else
			{
				// Terminate the calling Activity
		        activity.finish();
			}
		}
	}

	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();
		
		if(milliseconds == 0)
		{
			String dateDisplay = "APOD";
			
			if(filename != null)
			{
				if(filename.length() > 0)
				{
					// Format the date to display from the filename
					int year = Integer.parseInt(filename.substring(2, 4)) + 2000;
					if(year > 2094)
						year -= 100;
					
					int month = Integer.parseInt(filename.substring(4, 6));			
					int day = Integer.parseInt(filename.substring(6, 8));
					
					dateDisplay = year + "/" + month + "/" + day;
				}
			}
			else if(date != null)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				dateDisplay = sdf.format(date.getTime());
			}
			
			this.progressDialog = ProgressDialog.show(
					activity, 
					activity.getResources().getString(R.string.loading), 
					"Loading "+ dateDisplay);
		}
	}
}