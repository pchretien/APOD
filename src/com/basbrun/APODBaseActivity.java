package com.basbrun;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class APODBaseActivity extends Activity
{
	// Reference to the APODApplication to get access to the APODDataProvider
	protected APODApplication app = null;
	
		// A reference to the current APODData downloaded from the website
	// This data is obtained trough the data provider
	protected APODData apodData = null;
	 
	 @Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);		
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
	}

	// This method is called when the Activity is ready to create the menu.
	// We use the inflater to load an XML menu definition 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
    	super.onCreateOptionsMenu(menu);
    	
    	// Pump the xml menu definition to the Activity menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.apod_menu, menu);
        
        // Change menu icons for API levels 
        if(APODUtils.getRunningApiLevel() < 8)
        {
        	// Android 2.1 and older ...
        	for(int i=0; i<menu.size(); i++)
        	{
        		MenuItem item = menu.getItem(i);
        		switch(item.getItemId())
        		{
	        		case R.id.menu_about:
	        			item.setIcon(R.drawable.about);
	        			break;
	        		case R.id.menu_random:
	        			item.setIcon(R.drawable.dice);
	        			break;
	        		case R.id.menu_search:
	        			item.setIcon(R.drawable.search);
	        			break;
	        		case R.id.menu_set_date:
	        			item.setIcon(R.drawable.calendar);
	        			break;
	        		case R.id.menu_settings:
	        			item.setIcon(R.drawable.settings);
	        			break;
	        		case R.id.menu_wallpaper:
	        			item.setIcon(R.drawable.wallpaper);
	        			break;
        		}
        		
        	}
        }
        
        return true;
    } 
    
    // Main menu selection callbacks. When a menu item is clicked, 
    // this function is called with the id of the menu item selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	TextView aboutMsg;
        switch (item.getItemId()) 
        {
	        // Start the APODPreferencesActivity
        	case R.id.menu_settings:
        		startActivity(new Intent(this, APODPreferences.class));
                return true;
            
            // Display credits ... If you are reading this, your name could appear here
            // if you get involved in the project ;)
        	case R.id.menu_about:
        		
        		aboutMsg = new TextView(this);
        		aboutMsg.setText(" Copyright Philippe Chretien (2012) \nwww.basbrun.com");
        		aboutMsg.setGravity(Gravity.CENTER_HORIZONTAL);

        		new AlertDialog.Builder(this)
        		.setView(aboutMsg)
        		.setPositiveButton("OK", null)
        		.show();

                return true;
                
        	case R.id.menu_search:
        		
        		this.onSearchRequested();
                return true;
                
        	case R.id.menu_wallpaper:
        		
        		app = (APODApplication)getApplication();
        		new APODAsyncWallpaper(this, app).execute();
        		
        		return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onSearchRequested() 
    {
    	String initialQuery = ((APODApplication)getApplication()).getDataProvider().getSearchQuery();
        startSearch(initialQuery, initialQuery != null, null, false);
        return true;
     }
}
