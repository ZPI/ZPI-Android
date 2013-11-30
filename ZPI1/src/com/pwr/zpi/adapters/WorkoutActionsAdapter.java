package com.pwr.zpi.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pwr.zpi.R;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.database.entity.WorkoutActionAdvanced;
import com.pwr.zpi.database.entity.WorkoutActionSimple;
import com.pwr.zpi.utils.TimeFormatter;

public class WorkoutActionsAdapter extends ArrayAdapter<WorkoutAction> {
	
	private final Context context;
	
	private static final long MILINSECONDS_IN_MINUTE = 60000;
	private static final int SIMPLE = 0x1;
	private static final int ADVANCED = 0x2;
	
	private final int layoutResourceSimpleID;
	private final int layoutResourceAdvancedID;
	
	public WorkoutActionsAdapter(Context context, int layoutResourceSimpleID, int layoutResourceAdvancedID,
		List<WorkoutAction> data) {
		
		super(context, layoutResourceSimpleID, data);
		this.layoutResourceSimpleID = layoutResourceSimpleID;
		this.layoutResourceAdvancedID = layoutResourceAdvancedID;
		this.context = context;
	}
	
	@Override
	public int getItemViewType(int position) {
		WorkoutAction action = getItem(position);
		if (action.isAdvanced()) return ADVANCED;
		else return SIMPLE;
	}
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		WorkoutActionHolder holder = null;
		WorkoutAction action = getItem(position);
		int type = getItemViewType(position);
		//		if (row == null || row.getTag() == null) {
		holder = new WorkoutActionHolder();
		
		LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
		
		switch (type) {
			case SIMPLE:
				row = inflater.inflate(layoutResourceSimpleID, parent, false);
				holder.speedString = (TextView) row.findViewById(R.id.dataTextViewActionSpeedString);
				holder.timeOrDistance = (TextView) row.findViewById(R.id.dataTextViewActionTimeOrDistance);
				
				break;
			
			case ADVANCED:
				row = inflater.inflate(layoutResourceAdvancedID, parent, false);
				holder.pace = (TextView) row.findViewById(R.id.dataTextViewActionPace);
				holder.distance = (TextView) row.findViewById(R.id.dataTextViewActionDistance);
				holder.time = (TextView) row.findViewById(R.id.dataTextViewActionTime);
				break;
		}
		
		holder.image = (ImageView) row.findViewById(R.id.imageViewWorkoutAction);
		
		//			row.setTag(holder);
		//			
		//		}
		//		else {
		//			holder = (WorkoutActionHolder) row.getTag();
		//		}
		switch (type) {
			case SIMPLE:
				//TODO set image here
				//holder.image.setImageResource(/*advanced image id*/);
				WorkoutActionSimple simpleAction = (WorkoutActionSimple) action;
				int speedString = simpleAction.getSpeedType();
				switch (speedString) {
					case WorkoutAction.ACTION_SIMPLE_SPEED_SLOW:
						holder.speedString.setText(context.getResources().getString(R.string.slow));
						break;
					case WorkoutAction.ACTION_SIMPLE_SPEED_STEADY:
						holder.speedString.setText(context.getResources().getString(R.string.steady));
						break;
					case WorkoutAction.ACTION_SIMPLE_SPEED_FAST:
						holder.speedString.setText(context.getResources().getString(R.string.fast));
						break;
				}
				int valueType = simpleAction.getValueType();
				switch (valueType) {
					case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE:
						holder.timeOrDistance.setText(String.format("%.3f%s", simpleAction.getValue() / 1000, context
							.getResources().getString(R.string.km)));
						break;
					case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME:
						holder.timeOrDistance.setText(TimeFormatter.formatTimeHHMMSS((long) (simpleAction.getValue())));
						break;
				}
				break;
			
			case ADVANCED:
				//TODO set image here
				//holder.image.setImageResource(/*advanced image id*/);
				WorkoutActionAdvanced advancedAction = (WorkoutActionAdvanced) action;
				holder.time.setText(TimeFormatter.formatTimeHHMMSS(advancedAction.getTime()));
				holder.pace.setText(TimeFormatter.formatTimeMMSSorHHMMSS(advancedAction.getPace()));
				holder.distance.setText(String.format("%.3f", advancedAction.getDistance() / 1000));
				break;
		}
		
		return row;
	}
	
	static class WorkoutActionHolder {
		ImageView image;
		TextView distance;
		TextView time;
		TextView pace;
		
		TextView speedString;
		TextView timeOrDistance;
	}
	
}
