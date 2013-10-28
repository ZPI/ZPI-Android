package com.pwr.zpi.adapters;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.pwr.zpi.R;
import com.pwr.zpi.utils.Pair;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SplitsAdapter extends ArrayAdapter<Pair<Integer, Pair<Long, Long>>>{

	private Context context; 
	private int layoutResourceId;    
	
	public SplitsAdapter(Context context, int textViewResourceId,
			List<Pair<Integer, Pair<Long, Long>>> objects) {
		super(context, textViewResourceId, objects);
		this.layoutResourceId = textViewResourceId;
        this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RowHolder holder = null;
		
		if (row != null) {
			holder = (RowHolder) row.getTag();
		} else {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new RowHolder();
            holder.kilometer = (TextView) row.findViewById(R.id.textViewKilometerNumber);
            holder.splitTime = (TextView) row.findViewById(R.id.textViewSplitTime);
            holder.totalTime = (TextView) row.findViewById(R.id.textViewTotalTime);
            
            row.setTag(holder);
		}
		
		Pair<Integer, Pair<Long, Long>> data = getItem(position);
		
		holder.kilometer.setText(String.valueOf(data.first));
		holder.splitTime.setText(longToTimeFormat(data.second.first));
		holder.totalTime.setText(longToTimeFormat(data.second.second));
		
		return row;
	}
	
	private CharSequence longToTimeFormat(long milliseconds) {
		Date date = new Date(milliseconds);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int minutes = c.get(Calendar.MINUTE);
		int seconds = c.get(Calendar.SECOND);
		int hours = c.get(Calendar.HOUR);
		StringBuilder sb = new StringBuilder();
		if (hours != 0) {
			sb.append(hours).append(":");
		}
		sb.append(minutes).append(":").append(seconds);
		return sb.toString();
	}

	private class RowHolder {
		TextView kilometer;
		TextView splitTime;
		TextView totalTime;
	}

}
