package com.basbrun;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class APODSearchActivity extends Activity 
{
	String query;
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
	    handleIntent(intent);
	    
	    changeTitle();
	}
	
	@Override
	protected void onNewIntent(Intent intent) 
	{
	    setIntent(intent);
	    handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) 
	{
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) 
	    {
	      query = intent.getStringExtra(SearchManager.QUERY);	      
	      //app.startProgressDialog(this);
	      new APODAsyncSearch(query, this, (APODApplication)this.getApplication()).execute();
	    }
	}
	
	private void changeTitle() 
	{		
        String title = String.format(
        		"APOD: \"%s\"", 
        		query);
        setTitle(title);
	}
	
	public void displayResults(List<APODSearchItem> results)
	{
		if(results == null)
		{
			TextView aboutMsg = new TextView(this);
        	aboutMsg.setText(R.string.connection_error);        	
    		aboutMsg.setGravity(Gravity.CENTER_HORIZONTAL);
    		
        	new AlertDialog.Builder(this)
    		.setView(aboutMsg)
    		.setPositiveButton("OK", null)
    		.show();
        	
        	return;
		}
		
		if(results.size() == 0)
		{
			TextView aboutMsg = new TextView(this);
        	aboutMsg.setText("Nothing Found");        	
    		aboutMsg.setGravity(Gravity.CENTER_HORIZONTAL);
    		
        	new AlertDialog.Builder(this)
    		.setView(aboutMsg)
    		.setPositiveButton("OK", null)
    		.show();
        	
        	return;
		}
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
