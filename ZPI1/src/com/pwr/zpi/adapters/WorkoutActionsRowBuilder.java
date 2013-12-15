package com.pwr.zpi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pwr.zpi.R;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.database.entity.WorkoutActionAdvanced;
import com.pwr.zpi.database.entity.WorkoutActionSimple;
import com.pwr.zpi.utils.TimeFormatter;

public class WorkoutActionsRowBuilder extends RowBuilder<WorkoutAction> {
	
	private final Context context;
	
	private static final int SIMPLE = 0x1;
	private static final int ADVANCED = 0x2;
	
	public WorkoutActionsRowBuilder(Context context) {
		this.context = context;
	}
	
	@Override
	public boolean isTagOK(Object tag, WorkoutAction item) { //FIXME change to optimize row builder
		return false;
	}
	
	@Override
	public View inflateView(LayoutInflater inflater, ViewGroup parent, int[] layoutResourcesIDs, WorkoutAction item) {
		int type = getItemViewType(item);
		switch (type) {
			case SIMPLE:
				return inflater.inflate(layoutResourcesIDs[0], parent, false);
			case ADVANCED:
				return inflater.inflate(layoutResourcesIDs[1], parent, false);
			default:
				return null;
		}
	}
	
	@Override
	public AbstractRowHolder buildRowHolder(View row, WorkoutAction item) {
		WorkoutActionHolder holder = new WorkoutActionHolder();
		int type = getItemViewType(item);
		switch (type) {
			case SIMPLE:
				holder.speedString = (TextView) row.findViewById(R.id.dataTextViewActionSpeedString);
				holder.timeOrDistance = (TextView) row.findViewById(R.id.dataTextViewActionTimeOrDistance);
				
				break;
			
			case ADVANCED:
				holder.pace = (TextView) row.findViewById(R.id.dataTextViewActionPace);
				holder.distance = (TextView) row.findViewById(R.id.dataTextViewActionDistance);
				holder.time = (TextView) row.findViewById(R.id.dataTextViewActionTime);
				break;
		}
		holder.image = (ImageView) row.findViewById(R.id.imageViewWorkoutAction);
		return holder;
	}
	
	@Override
	public void fillRowDataToHolder(AbstractRowHolder holder, WorkoutAction item, int position, int[] layoutResourcesIDs) {
		WorkoutActionHolder rowHolder = (WorkoutActionHolder) holder;
		int type = getItemViewType(item);
		switch (type) {
			case SIMPLE:
				//TODO set image here
				//holder.image.setImageResource(/*advanced image id*/);
				WorkoutActionSimple simpleAction = (WorkoutActionSimple) item;
				int speedString = simpleAction.getSpeedType();
				switch (speedString) {
					case WorkoutAction.ACTION_SIMPLE_SPEED_SLOW:
						rowHolder.speedString.setText(context.getResources().getString(R.string.slow));
						break;
					case WorkoutAction.ACTION_SIMPLE_SPEED_STEADY:
						rowHolder.speedString.setText(context.getResources().getString(R.string.steady));
						break;
					case WorkoutAction.ACTION_SIMPLE_SPEED_FAST:
						rowHolder.speedString.setText(context.getResources().getString(R.string.fast));
						break;
				}
				int valueType = simpleAction.getValueType();
				switch (valueType) {
					case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE:
						rowHolder.timeOrDistance.setText(String.format("%.3f%s", simpleAction.getValue() / 1000,
							context.getResources().getString(R.string.km)));
						break;
					case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME:
						rowHolder.timeOrDistance
							.setText(TimeFormatter.formatTimeHHMMSS((long) (simpleAction.getValue())));
						break;
				}
				break;
			
			case ADVANCED:
				//TODO set image here
				//holder.image.setImageResource(/*advanced image id*/);
				WorkoutActionAdvanced advancedAction = (WorkoutActionAdvanced) item;
				rowHolder.time.setText(TimeFormatter.formatTimeHHMMSS(advancedAction.getTime()));
				rowHolder.pace.setText(TimeFormatter.formatTimeMMSSorHHMMSS(advancedAction.getPace()));
				rowHolder.distance.setText(String.format("%.3f", advancedAction.getDistance() / 1000));
				break;
		}
	}
	
	public int getItemViewType(WorkoutAction action) {
		if (action.isAdvanced()) return ADVANCED;
		else return SIMPLE;
	}
	
	static class WorkoutActionHolder extends AbstractRowHolder {
		ImageView image;
		TextView distance;
		TextView time;
		TextView pace;
		
		TextView speedString;
		TextView timeOrDistance;
	}
}
