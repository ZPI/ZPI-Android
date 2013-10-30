package com.pwr.zpi.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pwr.zpi.R;
import com.pwr.zpi.utils.Pair;
import com.pwr.zpi.utils.TimeFormatter;

public class SplitsAdapter extends ArrayAdapter<Pair<Integer, Pair<Long, Long>>> {
	
	private final Context context;
	private final int layoutResourceId;
	
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
		}
		else {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new RowHolder();
			holder.kilometer = (TextView) row.findViewById(R.id.textViewKilometerNumber);
			holder.splitTime = (TextView) row.findViewById(R.id.textViewSplitTime);
			holder.totalTime = (TextView) row.findViewById(R.id.textViewTotalTime);
			
			row.setTag(holder);
		}
		
		Pair<Integer, Pair<Long, Long>> data = getItem(position);
		
		holder.kilometer.setText(String.valueOf(data.first));
		holder.splitTime.setText(TimeFormatter.formatTimeMMSSorHHMMSS(data.second.first));
		holder.totalTime.setText(TimeFormatter.formatTimeMMSSorHHMMSS(data.second.second));
		
		return row;
	}
	
	private class RowHolder {
		TextView kilometer;
		TextView splitTime;
		TextView totalTime;
	}
	
}
