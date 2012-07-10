package com.basbrun;

import java.util.Calendar;

import android.graphics.Bitmap;

public class APODData 
{
	public enum ApodContentType
	{
		IMG,
		IFRAME,
		NONE,
		ERROR
	}	
	
	private ApodContentType apodDataType;
	
	private Calendar date;
	
	private Bitmap bitmap;
	private String src;
	
	private String page;
	private String pagePath;
	private String description;
	
	private String error;
	
	public APODData( 
			ApodContentType apodDataType,
			Calendar date,
			Bitmap bitmap,
			String src,
			String page,
			String pagePath,
			String description,
			String error)
	{
		this.apodDataType = apodDataType;
		this.date = date;
		this.bitmap = bitmap;
		this.src = src;
		this.page = page;
		this.pagePath = pagePath;
		this.description = description;
		this.error = error;
	}
	
	public Calendar getDate() {
		return date;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public String getPage() {
		return page;
	}
	public String getPagePath() {
		return pagePath;
	}
	public String getSrc() {
		return src;
	}
	public ApodContentType getApodDataType() {
		return apodDataType;
	}
	public String getError() {
		return error;
	}
	public String getDescription() {
		return description;
	}
}
