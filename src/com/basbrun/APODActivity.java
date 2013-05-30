//// APOD ////
//
// This project is an Android client to visualize the Astronomy Picture Of The
// Day (APOD) published by NASA at http://apod.nasa.gov/apod/. This project is
// free of charges and have been developed with the objective to learn Android
// programmation.
//
// Copyright (C) 2012  Philippe Chretien
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License Version 2
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
//
// You will find the latest version of this code at the following address:
// http://github.com/pchretien
//
// You can contact me at the following email address:
// philippe.chretien at gmail.com

package com.basbrun;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.basbrun.APODData.ApodContentType;

// APODActivity is the main application activity. The APOD are displayed trough
// this activity. The menu and all gestures are bound to this activity.
public class APODActivity extends APODBaseActivity //implements OnClickListener
{
	// Constants and listeners for gesture detection
	private static final int SWIPE_MIN_DISTANCE = 30;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;
    
    // The image view holds the picture of the day in the main page
    private ImageView imgView;
    
    // The web view holds the description of the APOD
    WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apod_main);
        
        // Bind the XML layout definition to the code
        bindings();

        // Create gesture detector listeners
    	createGestureDetector();
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	RefreshContent(); 
    }
    
    public void RefreshContent()
    {
    	// Connect to the APODDataProvider singleton and get the current APOD
        getData();
        
        // Check for errors in the returned apodData object
        checkForErrors();
        
        // Set the title of the application for the date of the current APOD
        changeTitle();

        // Initialize the Activity fields 
    	fillWithApodData();  

    	ImageButton nextButton = (ImageButton)this.findViewById(R.id.button_next);
    	nextButton.setEnabled(true);
    	
    	if(app.getDataProvider().isTodayAPOD())
    		nextButton.setEnabled(false);
    }

    // Get the APODDataProvider singleton stores in the APODApplication 
    // Get the APODData of the current day.
	private void getData() 
	{
		// Get a reference to the Application main class
        // We derived the main application class to store the 
        // APODDataProvider singleton
        app = (APODApplication)getApplication();
        
        // Get a reference to the currently loaded APOD
        apodData = app.getDataProvider().getAPOD();
	}

    // Create the appropriate gesture detector to detect left and right Fling
    // This gesture is applied to the ImageView and the WebView of the APODActivity
	private void createGestureDetector() {
		// Gesture detection callback methods
        gestureDetector = new GestureDetector(new APODGestureDetector());
        gestureListener = new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                return gestureDetector.onTouchEvent(event);
            }
        };
	}

    // Initialize the Activity fields depending on the type of data loaded
    // from the APOD website
	private void fillWithApodData() 
	{
		// Load the APOD description into the Activity webview
        webView.loadDataWithBaseURL(app.getDataProvider().getAPODRoot(),
				apodData.getDescription(),
				"text/html",
				null,
				null);
        webView.setOnTouchListener(gestureListener);
        imgView.setOnTouchListener(gestureListener);
        
		switch(apodData.getApodDataType())
    	{
    		// Standard image
	    	case IMG:
	    		imgView.setImageBitmap(apodData.getBitmap());
	    		imgView.setOnClickListener(onPictureClick);
	    		break;
	
	    	// Most of the <iframe/> elements contains YouTube videos
	    	case IFRAME:
	    		imgView.setImageResource(R.drawable.play);
	    		imgView.setOnClickListener(onVideoClick);	    		
	    		break;
	
	    	// Reporting errors to the user ...
	    	case ERROR:
	    		break;
    	}
	}

    // Change the Activity title to the date of the current APOD ...
	private void changeTitle() 
	{		
        String title = String.format(
        		"APOD: %d/%02d/%02d", 
        		apodData.getDate().get(Calendar.YEAR), 
        		apodData.getDate().get(Calendar.MONTH)+1, 
        		apodData.getDate().get(Calendar.DATE));
        setTitle(title);
	}

	// Validate the content of the apodData object and display error messages 
	// if required.
	private void checkForErrors() 
	{
		imgView.setVisibility(View.VISIBLE);
    	webView.setVisibility(View.VISIBLE);
    	
		// If there is a connection error or an out of date error, the
        // APODDataProvider returns an APODData object of type NONE or ERROR.
        if(apodData.getApodDataType() == ApodContentType.ERROR)
        {
        	TextView aboutMsg = new TextView(this);
        	aboutMsg.setText(apodData.getError());        	
    		aboutMsg.setGravity(Gravity.CENTER_HORIZONTAL);
    		
        	new AlertDialog.Builder(this)
    		.setView(aboutMsg)
    		.setPositiveButton("OK", null)
    		.show();
        	
        	imgView.setVisibility(View.INVISIBLE);
        	webView.setVisibility(View.INVISIBLE);
        }
	}

	// Bind XML layout elements to the code.
	private void bindings() 
	{
		// Bind to the Previous on screen button
        ImageButton previousButton = (ImageButton)this.findViewById(R.id.button_prev);
        previousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
            {
                loadPrevious();
            }
        });
        
        // Bind to the Today on screen button
        Button todayButton = (Button)this.findViewById(R.id.button_today);
        todayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
            {
                loadToday();
            }
        });
        
     	// Bind to the Next on screen button
        ImageButton nextButton = (ImageButton)this.findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
            {
                loadNext();
            }
        });

        // Bind the image view on the activity layout
    	imgView = (ImageView)findViewById(R.id.imageViewAPOD);
    	
    	// Bind the web view on the activity layout
    	webView = (WebView) findViewById(R.id.webViewDescription);
	}

    // Main menu selection callbacks. When a menu item is clicked, 
    // this function is called with the id of the menu item selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	switch (item.getItemId()) 
        {
	        // Load the APOD for a specific date using the DatePickerDialog
        	case R.id.menu_set_date:
        		Calendar calendar = apodData.getDate();
        		
        		DatePickerDialog datePicker = new DatePickerDialog(
        				this,
        				mDateSetListener,
        				calendar.get(Calendar.YEAR),
        				calendar.get(Calendar.MONTH),
        				calendar.get(Calendar.DAY_OF_MONTH));
        		datePicker.setOnDismissListener(mDismissListener);
        		datePicker.show();
                return true;
        		
        	case R.id.menu_random:
        		Calendar newCalendar = Calendar.getInstance();
        		
        		Calendar today = Calendar.getInstance();
        		int currentYear = today.get(Calendar.YEAR);
        		int currentMonth = today.get(Calendar.MONTH);
        		int currentDay = today.get(Calendar.DATE);
        		
        		int firstApodYear = 1995;
        		int firstApodMonth = 5;
        		int firstApodDay = 20;
        		
        		Random rnd = new Random();
        		int newYear = rnd.nextInt(currentYear-firstApodYear+1)+firstApodYear;
        		
        		int minMonth = 0;
        		int maxMonth = 11;
        		if(newYear == firstApodYear)
        			minMonth = firstApodMonth;
        		if(newYear == currentYear)
        			maxMonth = currentMonth;
        		int newMonth = rnd.nextInt(maxMonth-minMonth+1)+minMonth;
        		
        		newCalendar.set(newYear, newMonth, 1);
        		
        		int minDay = 1;
        		int maxDay = newCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        		if(newYear == firstApodYear && newMonth == firstApodMonth)
        			minDay = firstApodDay;
        		if(newYear == currentYear && newMonth == currentMonth)
        			maxDay = currentDay;
        		int newDay = rnd.nextInt(maxDay-minDay+1)+minDay;
        		
        		newCalendar.set(newYear, newMonth, newDay);
        		
        		new APODAsyncLoader(newCalendar, null, this, (APODApplication)this.getApplication(), 0).execute();
        				
        		return true;
        		
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Load the APOD of the date after the current date
	private void loadNext() 
	{
		Calendar date = (Calendar)apodData.getDate().clone();
		date.add(Calendar.DATE, 1);
		new APODAsyncLoader(date, null, this, (APODApplication)this.getApplication(), 0).execute();
	}

	// Load today's APOD
	private void loadToday() 
	{
		// Load the APOD
		Calendar date = GregorianCalendar.getInstance();
		new APODAsyncLoader(date, "", this, (APODApplication)this.getApplication(), 0).execute();
	}

	// Load the APOD of the date before the current date
	private void loadPrevious() 
	{
		// Load the APOD
		Calendar date = (Calendar)apodData.getDate().clone();
		date.add(Calendar.DATE, -1);
		new APODAsyncLoader(date, null, this, (APODApplication)this.getApplication(), 0).execute();
	}

    // Listener for the DatePickerDialog. This date picker is called when selecting @Set Date@ from the
    // menu. If the Set button is clicked, the date from the date picker is used to load a precise APOD.
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
	{
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			Calendar date = GregorianCalendar.getInstance();
			date.set(year, monthOfYear, dayOfMonth);
        	new APODAsyncLoader(date, null, APODActivity.this, (APODApplication)APODActivity.this.getApplication(), 0).execute();
		}
    };

    // Called when the date picker Cancel button is pressed or when the dialog is dismissed.
    private DialogInterface.OnDismissListener mDismissListener = new DialogInterface.OnDismissListener()
    {
    	public void onDismiss(DialogInterface dialog)
		{
		}
    };

    // Called when a user click on the picture. This brings the picture in a webview
    // An hyperlink in the webview will bring the user to the APOD website if the 
    // picture is clicked.
    private OnClickListener onPictureClick = new OnClickListener()
    {
        public void onClick(View v)
        {
        	startActivity(new Intent(APODActivity.this, APODPictureActivity.class));
    		return;
        }
    };

    // Called when the user click on the Play Button picture. This picture is used
    // to display a video instead of a picture.
    private OnClickListener onVideoClick = new OnClickListener()
    {
        public void onClick(View v)
        {
        	Intent intent = new Intent(Intent.ACTION_VIEW);
        	Uri data = Uri.parse(apodData.getSrc());
        	intent.setData(data);
        	startActivity(intent);

        	return;
        }
    };

    // Gesture detector to detect the left and right Fling gestures on the picture.
    // The left Fling brings the APOD of the previous day and the right Fling brings
    // the APOD of the next day.
    class APODGestureDetector extends SimpleOnGestureListener
    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            try
            {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                {
                	// This is not a valid fling
                    return false;
                }

                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                {
                	if(app.getDataProvider().isTodayAPOD())
                		return true;
                	
                	Calendar date = (Calendar)apodData.getDate().clone();
                    date.add(Calendar.DATE, 1);
                    new APODAsyncLoader(date, null, APODActivity.this, (APODApplication)APODActivity.this.getApplication(), 0).execute();

                    // This is a valid fling
                    return true;
                }
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                {
                	Calendar date = (Calendar)apodData.getDate().clone();
                    date.add(Calendar.DATE, -1);
                    new APODAsyncLoader(date, null, APODActivity.this, (APODApplication)APODActivity.this.getApplication(), 0).execute();

                    // This is a valid fling
                    return true;
                }
            }
            catch (Exception e)
            {
                // nothing
            }

            return false;
        }
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    //Handle the back button
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	        //Ask the user if they want to quit
	        new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle(R.string.quit)
	        .setMessage(R.string.really_quit)
	        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

	            public void onClick(DialogInterface dialog, int which) {

	                //Stop the activity
	                APODActivity.this.finish();    
	            }

	        })
	        .setNegativeButton(R.string.no, null)
	        .show();

	        return true;
	    }
	    else 
	    {
	        return super.onKeyDown(keyCode, event);
	    }
	}	
}

