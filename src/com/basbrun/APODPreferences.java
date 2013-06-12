package com.basbrun;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

// This class is the application Preferences page activity
public class APODPreferences extends PreferenceActivity 
{
	/** Called when the activity is first created. */
	private static final int CLEAR_CACHE_ALERT = 1;
	private static final int CLEAR_CACHE_ALERT_DONE = 2;
	
	private OnSharedPreferenceChangeListener sharedPreferencesListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    this.addPreferencesFromResource(R.xml.preferences);
	    
	    // The Clear Cache button is not really a button ... We bind to the
	    // Preference element and add an onClick callback to display a confirmation
	    // message to the user
	    Preference clearCacheButton = (Preference)findPreference("clear_cache");
	    clearCacheButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() 
	    {
            public boolean onPreferenceClick(Preference preference) 
            {             	
            	showDialog(CLEAR_CACHE_ALERT);
                return true;
            }
        });
	    
	    Preference autoWallpaperButtin = (Preference)findPreference("auto_wallpaper");
	    autoWallpaperButtin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() 
	    {
            public boolean onPreferenceClick(Preference preference) 
            {             	
            	// Schedule a wallpaper download in a few seconds ...
                return true;
            }
        });
	    
	    sharedPreferencesListener = new OnSharedPreferenceChangeListener()
	    {
	    	public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
	    	{
	    		if(key.equalsIgnoreCase("preferences_start_time"))
	    		{
	    			// Reschedule the service ...
	    			APODService.ScheduleService(APODApplication.getAppContext());
	    		}
	    	}
	    };
	    
	    SharedPreferences  preferences = PreferenceManager.getDefaultSharedPreferences(this);
	    preferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener);
	}
	
	// Create the dialog boxes
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		// Create the confirmation dialog
        if (id == CLEAR_CACHE_ALERT) 
        {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Delete all images in APOD cache?").setPositiveButton("Yes", clearCacheDialogClickListener)
            .setNegativeButton("No", clearCacheDialogClickListener);
        	
            return builder.create();
        }
            
        // Create the Done dialog
        if (id == CLEAR_CACHE_ALERT_DONE) 
        {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("   Done   ")
        	       .setCancelable(false)
        	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                //do things
        	           }
        	       });
        	
            return builder.create();
        }
        return null;
    	
    	
    }
	
	// Dialog callback methods
	private DialogInterface.OnClickListener clearCacheDialogClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
            case DialogInterface.BUTTON_POSITIVE:
            	// If the user click Yes in the confirmation dialog,
            	// all files in the cache are deleted
            	WebDataConnector dataConnector = new WebDataConnector("", APODData.getCachingDirectory());
            	dataConnector.clearCache();
            	
            	showDialog(CLEAR_CACHE_ALERT_DONE);            	
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                //No button clicked
                break;
            }
        }
    };

}
