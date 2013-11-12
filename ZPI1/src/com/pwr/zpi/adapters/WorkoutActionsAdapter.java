package com.pwr.zpi.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pwr.zpi.R;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.database.entity.WorkoutActionAdvanced;
import com.pwr.zpi.database.entity.WorkoutActionSimple;
import com.pwr.zpi.utils.TimeFormatter;

public class WorkoutActionsAdapter extends ArrayAdapter<WorkoutAction> {
	
	private final Context context;
	private final int layoutResourceId;
	private static final long MILINSECONDS_IN_MINUTE = 60000;
	
	public WorkoutActionsAdapter(Context context, int layoutResourceId, List<WorkoutAction> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		WorkoutActionHolder holder = null;
		WorkoutAction action = getItem(position);
		if (row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new WorkoutActionHolder();
			
			holder.image = (ImageView) row.findViewById(R.id.imageViewWorkoutActionIcon);
			
			holder.pace = (TextView) row.findViewById(R.id.dataTextViewActionPace);
			holder.distance = (TextView) row.findViewById(R.id.dataTextViewActionDistance);
			holder.time = (TextView) row.findViewById(R.id.dataTextViewActionTime);
			holder.advancedActionData = (LinearLayout) row
				.findViewById(R.id.linearLayoutAdvancedActionData);
			
			holder.speedString = (TextView) row.findViewById(R.id.dataTextViewActionSpeedString);
			holder.timeOrDistance = (TextView) row.findViewById(R.id.dataTextViewActionTimeOrDistance);
			holder.timeOrDistanceDescription = (TextView) row
				.findViewById(R.id.dataTextViewActionTimeOrDistanceDiscription);
			holder.timeOrDistanceUnit = (TextView) row.findViewById(R.id.dataTextViewActionTimeOrDistanceUnit);
			holder.simpleActionData = (LinearLayout) row.findViewById(R.id.linearLayoutSimpleActionData);
			
			row.setTag(holder);
			
		}
		else
		{
			holder = (WorkoutActionHolder) row.getTag();
		}
		
		if (action.isAdvanced())
		{
			//TODO set image here
			//holder.image.setImageResource(/*advanced image id*/);
			WorkoutActionAdvanced advancedAction = (WorkoutActionAdvanced) action;
			holder.time.setText(TimeFormatter.formatTimeHHMMSS(advancedAction.getTime()));
			holder.pace.setText(TimeFormatter.formatTimeMMSSorHHMMSS(advancedAction.getPace()));
			holder.distance.setText(String.format("%.3f", advancedAction.getDistance() / 1000));
			holder.advancedActionData.setVisibility(View.VISIBLE);
			holder.simpleActionData.setVisibility(View.GONE);
			
		}
		else if (action.isSimple())
		{
			//TODO set image here
			//holder.image.setImageResource(/*advanced image id*/);
			WorkoutActionSimple simpleAction = (WorkoutActionSimple) action;
			int speedString = simpleAction.getSpeedType();
			switch (speedString)
			{
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
			switch (valueType)
			{
				case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE:
					holder.timeOrDistance.setText(String.format("%.3f", simpleAction.getValue() / 1000));
					holder.timeOrDistanceDescription.setText(context.getResources().getString(R.string.distance));
					holder.timeOrDistanceUnit.setText(context.getResources().getString(R.string.km));
					break;
				case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME:
					holder.timeOrDistance
						.setText(TimeFormatter.formatTimeHHMMSS((long) (simpleAction.getValue())));
					holder.timeOrDistanceDescription.setText(context.getResources().getString(R.string.time));
					holder.timeOrDistanceUnit.setText(context.getResources().getString(R.string.empty_string));
					break;
			}
			holder.simpleActionData.setVisibility(View.VISIBLE);
			holder.advancedActionData.setVisibility(View.GONE);
			
		}
		
		return row;
	}
	
	static class WorkoutActionHolder
	{
		ImageView image;
		TextView distance;
		TextView time;
		TextView pace;
		LinearLayout advancedActionData;
		LinearLayout simpleActionData;
		TextView speedString;
		TextView timeOrDistance;
		TextView timeOrDistanceDescription;
		TextView timeOrDistanceUnit;
	}
	
}
