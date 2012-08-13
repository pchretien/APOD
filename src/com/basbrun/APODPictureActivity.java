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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

// The APODPictureActivity allows to view the picture in full size.
// The APOD FULL picture is not loaded here in order to avoid out of
// memory problems for low end devices.
public class APODPictureActivity extends APODBaseActivity
{
	private APODApplication app = null;
	private APODData apodData = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.apod_picture);

	    // Get the current APOD
	    getData();
	    
	    int version = Integer.parseInt(android.os.Build.VERSION.SDK);

	    // Build the full path of the image to display
        String src = app.getDataProvider().getBitmapPathFromCache(apodData.getDate());
        if(src != null && version < 16)
        	src = "file:/" + src;
        else
        	src = apodData.getSrc();
        
        // Build the content of the HTML page
        String htmlPage = String.format("<html><body><a href=\"%s\"><img src=\"%s\"/></a></body></html>", apodData.getPagePath(), src);
        
        // Create the WebView
        WebView webView = (WebView) findViewById(R.id.webViewPicture);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadDataWithBaseURL("", htmlPage, "text/html", "utf-8", "");
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
    	super.onCreateOptionsMenu(menu);
    	
    	for(int i=0; i<menu.size(); i++)
    	{
    		MenuItem menuItem = menu.getItem(i);
    		if(	menuItem.getItemId() == R.id.menu_set_date ||
    			menuItem.getItemId() == R.id.menu_search ||
    			menuItem.getItemId() == R.id.menu_random)
    			menuItem.setVisible(false);
    	}
    	
    	return true;
    } 

	// Get the current APOD
	private void getData() 
	{
		app = (APODApplication)getApplication();	    
        apodData = app.getDataProvider().getAPOD();
	}
}
