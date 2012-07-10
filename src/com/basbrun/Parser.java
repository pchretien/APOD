package com.basbrun;

public class Parser 
{
	public static String FindFirstElementSrc(String html, String elementType)
	{
		int elementStart = html.toUpperCase().indexOf("<"+elementType.toUpperCase());
		if(elementStart < 0)
			return null;
		
		String src = html.substring(elementStart);
		src = src.substring(src.toUpperCase().indexOf("SRC"));
		src = src.substring(src.toUpperCase().indexOf("\"")+1);
		src = src.substring(0, src.toUpperCase().indexOf("\""));
		
		return src;
	}
}
