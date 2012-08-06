package com.basbrun;

import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class APODSearchActivity extends Activity 
{
	private APODApplication app = null;
	
	private TextView searchBox = null;
	private ListView listViewSearchResults = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.apod_search);
	    
	    searchBox = (TextView)this.findViewById(R.id.textViewSearchQuery);
	    listViewSearchResults = (ListView)findViewById(R.id.listViewSearchResult);
	    
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) 
	    {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      searchBox.setText(" " + query);
	      
	      // Do search here ...
	      app = (APODApplication)getApplication();
	      List<APODSearchItem> results = app.getDataProvider().searchAPOD(query);
	      
	      // Display search results here ...
	      APODSearchItemAdapter adapter = new APODSearchItemAdapter(this, results);
	      listViewSearchResults.setAdapter(adapter);
	    }
	}
}
