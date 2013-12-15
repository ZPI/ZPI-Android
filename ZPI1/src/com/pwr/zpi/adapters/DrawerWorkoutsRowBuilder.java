package com.pwr.zpi.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pwr.zpi.R;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.database.entity.WorkoutActionSimple;

public class DrawerWorkoutsRowBuilder extends RowBuilder<WorkoutAction> {
	
	private final Workout workout;
	private final Context context;
	
	public DrawerWorkoutsRowBuilder(Workout workout, Context context) {
		this.workout = workout;
		this.context = context;
	}
	
	@Override
	public boolean isTagOK(Object tag, WorkoutAction item) { //FIXME this method should optimize code (commented code below still not working :/ )
		//		if (tag != null) {
		//			RowHolder holder = (RowHolder) tag;
		//			if (holder.actionTextAdvanced.getVisibility() == View.GONE && item.isSimple()) return true;
		//			else if (holder.actionTextAdvanced.getVisibility() == View.VISIBLE
		//				&& (item.isAdvanced() || item.isWarmUp())) return true;
		//		}
		return false;
	}
	
	@Override
	public AbstractRowHolder buildRowHolder(View row, WorkoutAction item) {
		RowHolder rowHolder = new RowHolder();
		rowHolder.actionType = (ImageView) row.findViewById(R.id.imageViewState);
		rowHolder.actionTextAdvanced = (TextView) row.findViewById(R.id.textViewWorkoutActionText);
		rowHolder.actionTextSimple = (TextView) row.findViewById(R.id.textViewWorkoutActionSimpleText);
		rowHolder.actionTypeSimple = (TextView) row.findViewById(R.id.textViewWorkoutActionSimpleTypeText);
		rowHolder.actionPointInTime = (TextView) row.findViewById(R.id.textViewState);
		return rowHolder;
	}
	
	@Override
	public void fillRowDataToHolder(AbstractRowHolder holder, WorkoutAction item, int position, int[] layoutResourcesIDs) {
		RowHolder rowHolder = (RowHolder) holder;
		
		int textColor = android.R.color.white;
		WorkoutAction action = item;
		switch (action.getActionType()) {
			case WorkoutAction.ACTION_SIMPLE:
				rowHolder.actionTextSimple.setVisibility(View.VISIBLE);
				rowHolder.actionTypeSimple.setVisibility(View.VISIBLE);
				rowHolder.actionTextAdvanced.setVisibility(View.GONE);
				int speedType = ((WorkoutActionSimple) action).getSpeedType();
				switch (speedType) {
					case WorkoutAction.ACTION_SIMPLE_SPEED_FAST:
						rowHolder.actionTypeSimple.setText(R.string.fast);
						
						break;
					case WorkoutAction.ACTION_SIMPLE_SPEED_STEADY:
						rowHolder.actionTypeSimple.setText(R.string.steady);
						break;
					case WorkoutAction.ACTION_SIMPLE_SPEED_SLOW:
						rowHolder.actionTypeSimple.setText(R.string.slow);
						break;
				}
				
				break;
			case WorkoutAction.ACTION_ADVANCED:
				
				rowHolder.actionTextSimple.setVisibility(View.INVISIBLE);
				rowHolder.actionTypeSimple.setVisibility(View.INVISIBLE);	//not gone because we still wont it to determine height
				rowHolder.actionTextAdvanced.setVisibility(View.VISIBLE);
				
				if (workout.getHowMuchLeft() > 0) {
					textColor = R.color.workout_action_text_good;
				}
				else {
					textColor = R.color.workout_action_text_bad;
				}
				break;
			case WorkoutAction.ACTION_WARM_UP:
				rowHolder.actionTextSimple.setVisibility(View.INVISIBLE);
				rowHolder.actionTypeSimple.setVisibility(View.INVISIBLE);	//not gone because we still wont it to determine height
				rowHolder.actionTextAdvanced.setVisibility(View.VISIBLE);
				break;
			default:
				break;
		}
		
		if (position < workout.getCurrentAction()) {
			textColor = R.color.white;
			rowHolder.actionType.setVisibility(View.VISIBLE);
			rowHolder.actionPointInTime.setVisibility(View.VISIBLE);
			rowHolder.actionType.setImageResource(R.drawable.done);
			rowHolder.actionPointInTime.setText(R.string.done);
			if (action.isSimple()) {
				rowHolder.actionTextSimple.setText(Workout.formatActionValue(action, null));
			}
			else {
				rowHolder.actionTextAdvanced.setText(Workout.formatActionValue(action, null));
			}
		}
		else if (position == workout.getCurrentAction()) {
			rowHolder.actionType.setImageResource(R.drawable.now);//setText(Workout.formatActionValue(action, null));
			rowHolder.actionPointInTime.setText(R.string.now);
			rowHolder.actionType.setVisibility(View.VISIBLE);
			rowHolder.actionPointInTime.setVisibility(View.VISIBLE);
			if (action.isSimple()) {
				rowHolder.actionTextSimple.setText(workout.getHowMuchLeftCurrentActionStringWithUnits());
			}
			else {
				rowHolder.actionTextAdvanced.setText(workout.getHowMuchLeftCurrentActionStringWithUnits());
			}
		}
		else {
			textColor = R.color.white;
			if (action.isSimple()) {
				rowHolder.actionTextSimple.setText(Workout.formatActionValue(action, null));
			}
			else {
				rowHolder.actionTextAdvanced.setText(Workout.formatActionValue(action, null));
			}
		}
		rowHolder.actionTextAdvanced.setTextColor(context.getResources().getColor(textColor));
	}
	
	private class RowHolder extends AbstractRowHolder {
		protected TextView actionTextSimple;
		protected TextView actionTypeSimple;
		protected TextView actionTextAdvanced;
		protected TextView actionPointInTime;
		protected ImageView actionType;
	}
}
