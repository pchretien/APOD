package com.basbrun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class APODBaseActivity extends Activity
{
	// This method is called when the Activity is ready to create the menu.
	// We use the inflater to load an XML menu definition 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
    	super.onCreateOptionsMenu(menu);
    	
    	// Pump the xml menu definition to the Activity menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.apod_menu, menu);
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
