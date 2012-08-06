package com.basbrun;

public class APODSearchItem 
{
	private String title;
	private String date;
	private String page;
	
	public APODSearchItem(String title, String date, String page) 
	{
		super();
		this.title = title;
		this.date = date;
		this.page = page;
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

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
	
}
