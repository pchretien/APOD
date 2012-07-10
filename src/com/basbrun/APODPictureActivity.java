package com.basbrun;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class APODPictureActivity extends Activity 
{

	private APODApplication app = null;
	private APODData apodData = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.apod_picture);

	    app = (APODApplication)getApplication();
        apodData = app.getDataProvider().getAPOD();
        
        String htmlPage = String.format("<html><a href=\"%s\"><img src=\"%s\"/></a></html>", apodData.getPagePath(), apodData.getSrc());
        WebView webView = (WebView) findViewById(R.id.webViewPicture);
        webView.loadData(htmlPage, "text/html", null);
	}
}
