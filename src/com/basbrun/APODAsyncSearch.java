package com.basbrun;

import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;

//Load the requested APOD asynchronously 
class APODAsyncSearch extends AsyncTask<Void, Void, Void>
{
	private String query;
	
	// The calling Activity is required start the new 
	// APODActivity and stop the current one.
	private APODSearchActivity activity;
	
	// Reference to the APODApplication to get access to the APODDataProvider
	private APODApplication app = null;
	
	List<APODSearchItem> results;
	
	ProgressDialog progressDialog;
	
	// Ctor
	public APODAsyncSearch(
			String query, 
			APODSearchActivity activity,
			APODApplication app)
	{
		this.query = query;
		this.activity = activity;
		this.app = app;
	}
	
	// This is where the asyn loading is made. This code runs in
	// a separate thread. The onPostExecute method is called when the
	// task is done
	@Override
	protected Void doInBackground(Void ... params)
	{
		// Do search here ...
	    results = app.getDataProvider().searchAPOD(query);
		
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
		
		activity.displayResults(results);
	}

	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();
		this.progressDialog = ProgressDialog.show(
				activity, 
				activity.getResources().getString(R.string.searching), 
				"Searching "+query);
	}
}