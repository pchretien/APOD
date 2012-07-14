package com.basbrun;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;

public class APODSplashScreenActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.apod_splash_screen);
		
		Calendar calendar = Calendar.getInstance();
		new APODAsyncLoader(this, null, 4000).execute(calendar);
	}

}
