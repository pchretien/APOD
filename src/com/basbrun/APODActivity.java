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

import com.basbrun.APODData.ApodContentType;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
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
import android.widget.Toast;

// APODActivity is the main application activity. The APOD are displayed trough
// this activity. The menu and all gestures are bound to this activity.
public class APODActivity extends Activity //implements OnClickListener
{

	// Reference to the APODApplication to get access to the APODDataProvider
	private APODApplication app = null;
	
	// A reference to the current APODData downloaded from the website
	// This data is obtained trough the data provider
	private APODData apodData = null;

	// Constants and listeners for gesture detection
	private static final int SWIPE_MIN_DISTANCE = 60;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;

    // The wait spinner dialog
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apod_main);
        
        ImageButton previousButton = (ImageButton)this.findViewById(R.id.button_prev);
        previousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
            {
                loadPrevious();
            }
        });
        
        Button todayButton = (Button)this.findViewById(R.id.button_today);
        todayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
            {
                loadToday();
            }
        });
        
        ImageButton nextButton = (ImageButton)this.findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
            {
                loadNext();
            }
        });

        // Find the image view on the activity layout
    	ImageView imgView = (ImageView)findViewById(R.id.imageViewAPOD);
    	
    	// Find the web view on the activity layout
    	WebView webView = (WebView) findViewById(R.id.webViewDescription);

    	// Gesture detection callback methods
        gestureDetector = new GestureDetector(new APODGestureDetector());
        gestureListener = new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                return gestureDetector.onTouchEvent(event);
            }
        };
        
        // Get a reference to the Application main class
        app = (APODApplication)getApplication();
        
        // Get a reference to the currently loaded APOD
        apodData = app.getDataProvider().getAPOD();
        
        if(apodData.getApodDataType() == ApodContentType.NONE ||
           apodData.getApodDataType() == ApodContentType.ERROR)
        {
        	TextView aboutMsg = new TextView(this);
        	if(apodData.getApodDataType() == ApodContentType.ERROR)
        		aboutMsg.setText(apodData.getError());
        	else
        		aboutMsg.setText(" Connection error! Make sure you are connected to the internet. ");
    		aboutMsg.setGravity(Gravity.CENTER_HORIZONTAL);
    		
        	new AlertDialog.Builder(this)
    		.setView(aboutMsg)
    		.setPositiveButton("OK", null)
    		.show();
        	
        	imgView.setVisibility(View.INVISIBLE);
        	webView.setVisibility(View.INVISIBLE);
        }
        else
        {
        	imgView.setVisibility(View.VISIBLE);
        	webView.setVisibility(View.VISIBLE);
        }
        
        // Change the Activity title ...
        String title = String.format(
        		"APOD (%d/%02d/%02d)", 
        		apodData.getDate().get(Calendar.YEAR), 
        		apodData.getDate().get(Calendar.MONTH)+1, 
        		apodData.getDate().get(Calendar.DATE));
        setTitle(title);
        
        // Load the APOD description into the Activity webview
        webView.loadDataWithBaseURL(app.getDataProvider().getAPODRoot(),
				apodData.getDescription(),
				"text/html",
				null,
				null);
        webView.setOnTouchListener(gestureListener);

        // Initialize the Activity fields depending on the type of data loaded
        // from the APOD website
    	switch(apodData.getApodDataType())
    	{
    		// Standard image
	    	case IMG:
	    		imgView.setImageBitmap(apodData.getBitmap());
	    		imgView.setOnClickListener(onPictureClick);
	            imgView.setOnTouchListener(gestureListener);
	    		break;
	
	    	// Most of the <iframe/> elements contains YouTube videos
	    	case IFRAME:
	    		imgView.setImageResource(R.drawable.play);
	    		imgView.setOnClickListener(onVideoClick);
	    		imgView.setOnTouchListener(gestureListener);
	    		break;
	
	    	// Undefined contend ...
	    	case NONE:
	    		break;
	
	    	// Reporting errors to the user ...
	    	case ERROR:
	    		break;
    	} 
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
    	// Pump the xml menu definition to the Activity menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.apod_menu, menu);
        return true;
    } 
   
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	TextView aboutMsg;
        switch (item.getItemId()) 
        {
	        // Load the APOD for a specific date
        	case R.id.menu_set_date:
        		// Load the date picker. The new date will be set in the callback function
        		Calendar calendar = Calendar.getInstance();
        		if(apodData != null)
        			calendar = apodData.getDate();
        		
        		DatePickerDialog datePicker = new DatePickerDialog(
        				this,
        				mDateSetListener,
        				calendar.get(Calendar.YEAR),
        				calendar.get(Calendar.MONTH),
        				calendar.get(Calendar.DAY_OF_MONTH));
        		datePicker.setOnDismissListener(mDismissListener);
        		datePicker.show();

                return true;

            // Not yet implemented
        	case R.id.menu_settings:
        		startActivity(new Intent(APODActivity.this, APODPreferences.class));
                return true;
            
            // Credits ...
        	case R.id.menu_about:
        		
        		aboutMsg = new TextView(this);
        		aboutMsg.setText(" Copyright Philippe Chretien (2012) \nwww.basbrun.com");
        		aboutMsg.setGravity(Gravity.CENTER_HORIZONTAL);

        		new AlertDialog.Builder(this)
        		.setView(aboutMsg)
        		.setPositiveButton("OK", null)
        		.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

	private void loadNext() {
		Calendar date;
		progressDialog = ProgressDialog.show(APODActivity.this, APODActivity.this.getResources().getString(R.string.loading), APODActivity.this.getResources().getString(R.string.loading_your));

		// Load the APOD
		date = (Calendar)apodData.getDate().clone();
		date.add(Calendar.DATE, 1);
		new APODAsyncLoader(this, progressDialog, 0).execute(date);
	}

	private void loadToday() {
		Calendar date;
		progressDialog = ProgressDialog.show(APODActivity.this, APODActivity.this.getResources().getString(R.string.loading), APODActivity.this.getResources().getString(R.string.loading_your));

		// Load the APOD
		date = GregorianCalendar.getInstance();
		new APODAsyncLoader(this, progressDialog, 0).execute(date);
	}

	private void loadPrevious() {
		Calendar date;
		progressDialog = ProgressDialog.show(APODActivity.this, APODActivity.this.getResources().getString(R.string.loading), APODActivity.this.getResources().getString(R.string.loading_your));

		// Load the APOD
		date = (Calendar)apodData.getDate().clone();
		date.add(Calendar.DATE, -1);
		new APODAsyncLoader(this, progressDialog, 0).execute(date);
	}

    // Listener for the DatePickerDialog. This date picker is called when selecting @Set Date@ from the
    // menu. If the Set button is clicked, the date from the date picker is used to load a precise APOD.
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
	{
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			// Start the spinner
			progressDialog = ProgressDialog.show(APODActivity.this, APODActivity.this.getResources().getString(R.string.loading), APODActivity.this.getResources().getString(R.string.loading_your));

			Calendar date = GregorianCalendar.getInstance();
			date.set(year, monthOfYear, dayOfMonth);
        	new APODAsyncLoader(APODActivity.this, progressDialog, 0).execute(date);
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
                	progressDialog = ProgressDialog.show(APODActivity.this, APODActivity.this.getResources().getString(R.string.loading), APODActivity.this.getResources().getString(R.string.loading_your));

                	Calendar date = (Calendar)apodData.getDate().clone();
                    date.add(Calendar.DATE, 1);
                    new APODAsyncLoader(APODActivity.this, progressDialog, 0).execute(date);

                    // This is a valid fling
                    return true;
                }
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                {
                	progressDialog = ProgressDialog.show(APODActivity.this, APODActivity.this.getResources().getString(R.string.loading), APODActivity.this.getResources().getString(R.string.loading_your));

                    Calendar date = (Calendar)apodData.getDate().clone();
                    date.add(Calendar.DATE, -1);
                    new APODAsyncLoader(APODActivity.this, progressDialog, 0).execute(date);

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
}

