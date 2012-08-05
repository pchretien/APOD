package com.basbrun;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class APODSearchActivity extends Activity 
{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.apod_search);
	    
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      
	      // Do search here ...
	      
	      // Display search results here ...
	      TextView searchBox = (TextView)this.findViewById(R.id.textViewSearchQuery);
	      searchBox.setText(" " + query);
	    }
	}
}
