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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class APODActivity extends Activity implements OnClickListener
{

	private APODApplication app = null;
	private APODData apodData = null;

	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Get a reference to the Application main class
        app = (APODApplication)getApplication();

        // Get a reference to the loded APOD
        apodData = app.getDataProvider().getAPOD();

    	TextView textView = (TextView)findViewById(R.id.textViewPath);
    	ImageView imgView = (ImageView)findViewById(R.id.imageViewAPOD);

    	// Gesture detection
        gestureDetector = new GestureDetector(new APODGestureDetector());
        gestureListener = new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                return gestureDetector.onTouchEvent(event);
            }
        };

    	switch(apodData.getApodDataType())
    	{
    	case IMG:
    		textView.setText(apodData.getPagePath());
    		imgView.setImageBitmap(apodData.getBitmap());
    		imgView.setOnClickListener(onPictureClick);
            imgView.setOnTouchListener(gestureListener);
    		break;

    	case IFRAME:
    		textView.setText(apodData.getPagePath());
    		imgView.setImageResource(R.drawable.play);
    		imgView.setOnClickListener(onVideoClick);
    		imgView.setOnTouchListener(gestureListener);
    		break;

    	case NONE:
    		textView.setText(apodData.getPagePath());
    		break;

    	case ERROR:
    		textView.setText(apodData.getError());
    		break;
    	}

    	WebView webView = (WebView) findViewById(R.id.webViewDescription);
    	webView.loadDataWithBaseURL(app.getDataProvider().getDomainRoot(),
				apodData.getDescription(),
				"text/html",
				null,
				null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.apod_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
    	Calendar date;
    	TextView aboutMsg;
        switch (item.getItemId()) {

	        case R.id.menu_previous:
	        	progressDialog = ProgressDialog.show(APODActivity.this, APODActivity.this.getResources().getString(R.string.loading), APODActivity.this.getResources().getString(R.string.loading_your));

	        	date = (Calendar)apodData.getDate().clone();
                date.add(Calendar.DATE, -1);
                new APODAsyncLoad().execute(date);
	            return true;

	        case R.id.menu_today:
	        	progressDialog = ProgressDialog.show(APODActivity.this, APODActivity.this.getResources().getString(R.string.loading), APODActivity.this.getResources().getString(R.string.loading_your));

	        	date = GregorianCalendar.getInstance();
	        	new APODAsyncLoad().execute(date);
	            return true;

	        case R.id.menu_next:
	        	progressDialog = ProgressDialog.show(APODActivity.this, APODActivity.this.getResources().getString(R.string.loading), APODActivity.this.getResources().getString(R.string.loading_your));

	        	date = (Calendar)apodData.getDate().clone();
                date.add(Calendar.DATE, 1);
                new APODAsyncLoad().execute(date);
	            return true;

        	case R.id.menu_set_date:
        		Calendar calendar = GregorianCalendar.getInstance();
        		DatePickerDialog datePicker = new DatePickerDialog(
        				this,
        				mDateSetListener,
        				calendar.get(Calendar.YEAR),
        				calendar.get(Calendar.MONTH),
        				calendar.get(Calendar.DAY_OF_MONTH));
        		datePicker.setOnDismissListener(mDismissListener);
        		datePicker.show();

                return true;

        	case R.id.menu_settings:
        		new AlertDialog.Builder(this)
        		.setMessage("Settings")
        		.setPositiveButton("Ok", null)
        		.show();

                return true;

        	case R.id.menu_about:
        		aboutMsg = new TextView(this);
        		aboutMsg.setText(" Copyright Philippe Chretien (2012) \nwww.basbrun.com");
        		aboutMsg.setGravity(Gravity.CENTER_HORIZONTAL);

        		new AlertDialog.Builder(this)
        		.setView(aboutMsg)
        		.setPositiveButton("Ok", null)
        		.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
	{
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			progressDialog = ProgressDialog.show(APODActivity.this, APODActivity.this.getResources().getString(R.string.loading), APODActivity.this.getResources().getString(R.string.loading_your));

			Calendar date = GregorianCalendar.getInstance();
			date.set(year, monthOfYear, dayOfMonth);
        	new APODAsyncLoad().execute(date);
		}
    };

    private DialogInterface.OnDismissListener mDismissListener = new DialogInterface.OnDismissListener()
    {
    	public void onDismiss(DialogInterface dialog)
		{
    		Toast.makeText(APODActivity.this, "Set date canceled ...", Toast.LENGTH_SHORT).show();
		}
    };

    private OnClickListener onPictureClick = new OnClickListener()
    {
        public void onClick(View v)
        {
        	startActivity(new Intent(APODActivity.this, APODPictureActivity.class));
    		return;
        }
    };

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

    class APODAsyncLoad extends AsyncTask<Calendar, Void, Void>
    {
		@Override
		protected Void doInBackground(Calendar... params)
		{
			if(params[0].after(Calendar.getInstance()))
				return null;

			app.getDataProvider().getAPODByDate(params[0]);
			return null;
		}

		protected void onPostExecute(Void result)
		{
			if(progressDialog != null)
				progressDialog.dismiss();

			startActivity(new Intent(APODActivity.this, APODActivity.class));
	        APODActivity.this.finish();
		}
    }

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
                    new APODAsyncLoad().execute(date);

                    // This is a valid fling
                    return true;
                }
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                {
                	progressDialog = ProgressDialog.show(APODActivity.this, APODActivity.this.getResources().getString(R.string.loading), APODActivity.this.getResources().getString(R.string.loading_your));

                    Calendar date = (Calendar)apodData.getDate().clone();
                    date.add(Calendar.DATE, -1);
                    new APODAsyncLoad().execute(date);

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

	public void onClick(View v)
	{
		// TODO Auto-generated method stub
	}
}

