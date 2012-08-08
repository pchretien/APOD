package com.basbrun;

import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class APODSearchActivity extends Activity 
{
	private ListView listViewSearchResults = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.apod_search);
	    
	    listViewSearchResults = (ListView)findViewById(R.id.listViewSearchResult);
	    
	    listViewSearchResults.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
                return onLongListItemClick(v,pos,id);
            }
        });
	    
	    listViewSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                onListItemClick(listViewSearchResults, v,pos,id);
            }
        });
	    
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) 
	    {
	      String query = intent.getStringExtra(SearchManager.QUERY);	      
	      //app.startProgressDialog(this);
	      new APODAsyncSearch(query, this, (APODApplication)this.getApplication()).execute();
	    }
	}
	
	public void displayResults(List<APODSearchItem> results)
	{
		// Display search results here ...
		APODSearchItemAdapter adapter = new APODSearchItemAdapter(this, results);
		listViewSearchResults.setAdapter(adapter);
	}
	
	protected boolean onLongListItemClick(View v, int pos, long id) 
	{
		APODSearchItem item = (APODSearchItem)listViewSearchResults.getItemAtPosition(pos);
		String message = "OnItemLongClick - " + item.getDate();
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();	
		return true;
	}
	
	public void onListItemClick(ListView l, View v, int position, long id) 
	{
		APODSearchItem item = (APODSearchItem)listViewSearchResults.getItemAtPosition(position);
		new APODAsyncLoader(item.getPagePath(), this, (APODApplication)this.getApplication(), 0).execute();
	}
}
