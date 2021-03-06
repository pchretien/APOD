package com.basbrun;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;

// Splash screen ...
public class APODSplashScreenActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.apod_splash_screen);
		
		// If I pass null as Calendar I receive an exception on the call to execute() !!!!!
		Calendar calendar = Calendar.getInstance();
		new APODAsyncLoader(calendar, null, this, (APODApplication)this.getApplication(), 2000).execute();
	}
}
