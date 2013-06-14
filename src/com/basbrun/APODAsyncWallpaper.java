package com.basbrun;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

//Load the requested APOD asynchronously 
class APODAsyncWallpaper extends AsyncTask<Void, Void, Void>
{
	private Activity activity;
	private APODApplication app = null;
	
	ProgressDialog progressDialog;
	
	// Ctor
	public APODAsyncWallpaper(
			Activity activity,
			APODApplication app)
	{
		this.activity = activity;
		this.app = app;
	}
	
	// This is where the asyn loading is made. This code runs in
	// a separate thread. The onPostExecute method is called when the
	// task is done
	@Override
	protected Void doInBackground(Void ... params)
	{
		APODData apodData = app.getDataProvider().getAPOD();
		if(apodData != null)
		{
			try
			{
				Bitmap bmp = apodData.getBitmap();
				if(bmp != null)
				{
					setWallpaperWithAPOD(bmp);
					APODUtils.SaveLastWallpaperDate(APODApplication.getAppContext(), apodData.getDate());
				}
			}
			catch(Exception ex)
			{        	
				System.out.println(ex);
			}
		}
	    
		return null;
	}
	
	@SuppressWarnings("unused")
	private void setWallpaperWithAPOD(Bitmap bmp)
    {
    	try
    	{
	    	if(APODUtils.apiLevel <= 3)
			{
				activity.getApplicationContext().setWallpaper(bmp);
			}
			else
			{
				android.app.WallpaperManager wallpaperManager = android.app.WallpaperManager.getInstance(activity);
				wallpaperManager.setBitmap(bmp);
			}
    	}
    	catch(Exception ex)
    	{
    	}
    }

	// Called when getAPODByDate() is done
	@Override
	protected void onPostExecute(Void result)
	{		
		Toast.makeText(APODApplication.getAppContext(), "Wallpaper changed successfully", Toast.LENGTH_SHORT).show();
		
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
	}

	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();
		this.progressDialog = ProgressDialog.show(
				activity, 
				"Working ...", 
				"Changing Wallpaper");
	}
}