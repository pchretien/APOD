package com.basbrun;

import android.app.Application;

public class APODApplication extends Application
{
	private static APODDataProvider dataProvider= new APODDataProvider();
	
	@Override
	public void onCreate()
	{
		super.onCreate();		
	}

	public APODDataProvider getDataProvider() {
		return dataProvider;
	}
}
