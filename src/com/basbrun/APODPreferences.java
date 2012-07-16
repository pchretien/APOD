package com.basbrun;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class APODPreferences extends PreferenceActivity 
{
	/** Called when the activity is first created. */
	private static final int CLEAR_CACHE_ALERT = 1;
	private static final int CLEAR_CACHE_ALERT_DONE = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    this.addPreferencesFromResource(R.xml.preferences);
	    
	    Preference button = (Preference)findPreference("clear_cache");
	    button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() 
	    {
            public boolean onPreferenceClick(Preference preference) 
            {             	
            	showDialog(CLEAR_CACHE_ALERT);
                return true;
            }
        });
	}
	
	@Override
	protected Dialog onCreateDialog(int id) 
	{
        if (id == CLEAR_CACHE_ALERT) 
        {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Delete all images in APOD cache?").setPositiveButton("Yes", clearCacheDialogClickListener)
            .setNegativeButton("No", clearCacheDialogClickListener);
        	
            return builder.create();
        }
            
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
	
	private DialogInterface.OnClickListener clearCacheDialogClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
            case DialogInterface.BUTTON_POSITIVE:
            	APODDataConnector.clearCache();
            	showDialog(CLEAR_CACHE_ALERT_DONE);            	
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                //No button clicked
                break;
            }
        }
    };

}
