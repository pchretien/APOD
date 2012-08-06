package com.basbrun;

public class APODSearchItem 
{
	private String title;
	private String date;
	private String pagePath;
	
	public APODSearchItem(String title, String date, String page) 
	{
		super();
		this.title = title;
		this.date = date;
		this.pagePath = page;
	}
	
	public String getTitle() 
	{
		return title;
	}
	
	public void setTitle(String title) 
	{
		this.title = title;
	}
	
	public String getDate() 
	{
		return date;
	}
	
	public void setDate(String date) 
	{
		this.date = date;
	}

	public String getPagePath() {
		return pagePath;
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}
	
}
