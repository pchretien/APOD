package com.basbrun;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class APODSearchItemAdapter extends BaseAdapter 
{
	List<APODSearchItem> searchResult;
	LayoutInflater inflater;
	
	public APODSearchItemAdapter(Context context, List<APODSearchItem> searchResult)
	{
		inflater = LayoutInflater.from(context);
		this.searchResult = searchResult;
	}
	
	public int getCount() 
	{
		return searchResult.size();
	}

	public Object getItem(int position) 
	{
		return searchResult.get(position);
	}

	public long getItemId(int position) 
	{
		return position;
	}
	
	private class APODSearchItemViewHolder
	{
		TextView title;
		TextView date;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{

		APODSearchItemViewHolder holder;
	
		if(convertView == null) 
		{
	
			holder = new APODSearchItemViewHolder();
		
			convertView = inflater.inflate(R.layout.search_item, null);	
			holder.title = (TextView)convertView.findViewById(R.id.serchItemTitle);	
			holder.date = (TextView)convertView.findViewById(R.id.searchItemDate);	
			convertView.setTag(holder);	
		} 
		else 
		{
			holder = (APODSearchItemViewHolder) convertView.getTag();
		}
	
		// Return the Title of the search item
		holder.title.setText(searchResult.get(position).getTitle());	
		
		// Return the date of the returned search item
		String date = searchResult.get(position).getDate();
		holder.date.setText(date);
		return convertView;

	}

}
