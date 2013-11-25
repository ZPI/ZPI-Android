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
import com.pwr.zpi.database.entity.TreningPlan;

public class TreningPlansAdapter extends ArrayAdapter<TreningPlan> {
	
	private final Context context;
	private final int layoutResourceId;
	
	public TreningPlansAdapter(Context context, int layoutResourceId, List<TreningPlan> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RunHolder holder = null;
		
		if (row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new RunHolder();
			holder.name = (TextView) row.findViewById(R.id.textViewWorkoutItemName);	//looks the same as workout list
			row.setTag(holder);
		}
		else
		{
			holder = (RunHolder) row.getTag();
		}
		
		TreningPlan TreningPlan = getItem(position);
		
		holder.name.setText(TreningPlan.getName());
		
		return row;
	}
	
	static class RunHolder
	{
		TextView name;
	}
}
