package com.pwr.zpi.adapters;

import java.util.ArrayList;
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
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.database.entity.WorkoutAction;

public class DrawerWorkoutsAdapter extends ArrayAdapter<WorkoutAction> {
	
	private static final int ACTIVE_HEIGHT = 200;
	private static final int NOT_ACTIVE_HEIGHT = 100;
	
	private final Context context;
	private final Workout workout;
	
	//TODO change to list adapter
	public DrawerWorkoutsAdapter(Context context, Workout workout) {
		this.context = context;
		this.workout = workout;
		List<WorkoutAction> actions = new ArrayList<WorkoutAction>();
		for (int i = 0; i < workout.getRepeatCount(); i++) {
			actions.addAll(workout.getActions());
		}
		workout.setActions(actions);
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return workout.getActions().get(childPosition);
	}
	
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View row = convertView;
		RowHolder rowHolder;
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(R.layout.workout_drawer_list_item, parent, false);
			
			rowHolder = new RowHolder();
			rowHolder.icon = (ImageView) row.findViewById(R.id.imageViewWorkoutActionIcon);
			rowHolder.text = (TextView) row.findViewById(R.id.textViewWorkoutActionText);
			rowHolder.layout = (LinearLayout) row.findViewById(R.id.linearLayoutDrawerItem);
			
			row.setTag(rowHolder);
		} else {
			rowHolder = (RowHolder) row.getTag();
		}
		
		int textColor = android.R.color.black;
		WorkoutAction action = workout.getActions().get(childPosition);
		switch (action.getActionType()) {
			case WorkoutAction.ACTION_SIMPLE:
				rowHolder.icon.setBackgroundResource(R.drawable.ic_launcher);
				break;
			case WorkoutAction.ACTION_ADVANCED:
				rowHolder.icon.setBackgroundResource(R.drawable.ic_launcher);
				if (workout.getHowMuchLeft() > 0) {
					textColor = R.color.workout_action_text_good;
				} else {
					textColor = R.color.workout_action_text_bad;
				}
				break;
			default:
				break;
		}
		
		if (childPosition < workout.getCurrentAction()){
			textColor = R.color.workout_action_text_not_active;
			rowHolder.layout.setBackgroundColor(context.getResources().getColor(R.color.workout_drawer_not_active));
			rowHolder.text.setText(context.getResources().getString(R.string.done));
			
			row.setMinimumHeight(NOT_ACTIVE_HEIGHT);
		} else if (childPosition == workout.getCurrentAction()) {
			//TODO text from how much left and color
			rowHolder.layout.setBackgroundColor(context.getResources().getColor(R.color.workout_drawer_active));
			rowHolder.text.setText(workout.getHowMuchLeftCurrentActionStringWithUnits());
			
			row.setMinimumHeight(ACTIVE_HEIGHT);
		} else {
			textColor = R.color.workout_action_text_not_active;
			rowHolder.layout.setBackgroundColor(context.getResources().getColor(R.color.workout_drawer_not_active));
			rowHolder.text.setText(Workout.formatActionValue(action, null));
			
			row.setMinimumHeight(NOT_ACTIVE_HEIGHT);
		}
		rowHolder.text.setTextColor(context.getResources().getColor(textColor));
		return row;
	}
	
	private class RowHolder {
		protected LinearLayout layout;
		protected TextView text;
		protected ImageView icon;
	}
}
