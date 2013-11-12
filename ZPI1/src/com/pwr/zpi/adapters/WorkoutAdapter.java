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
import com.pwr.zpi.database.entity.Workout;

public class WorkoutAdapter extends ArrayAdapter<Workout> {
	
	private final Context context;
	private final int layoutResourceId;
	
	public WorkoutAdapter(Context context, int layoutResourceId, List<Workout> data) {
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
			holder.name = (TextView) row.findViewById(R.id.textViewWorkoutItemName);
			row.setTag(holder);
		}
		else
		{
			holder = (RunHolder) row.getTag();
		}
		
		Workout workout = getItem(position);
		
		holder.name.setText(workout.getName());
		
		return row;
	}
	
	static class RunHolder
	{
		TextView name;
	}
}
