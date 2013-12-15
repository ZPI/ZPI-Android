package com.pwr.zpi.adapters;

import android.view.View;
import android.widget.TextView;

import com.pwr.zpi.R;
import com.pwr.zpi.database.entity.Workout;

public class WorkoutRowBuilder extends RowBuilder<Workout> {
	
	@Override
	public AbstractRowHolder buildRowHolder(View row, Workout item) {
		RunHolder holder = new RunHolder();
		holder.name = (TextView) row.findViewById(R.id.textViewWorkoutItemName);
		return holder;
	}
	
	@Override
	public void fillRowDataToHolder(AbstractRowHolder holder, Workout item, int position, int[] layoutResourcesIDs) {
		((RunHolder) holder).name.setText(item.getName());
	}
	
	static class RunHolder extends AbstractRowHolder {
		TextView name;
	}
}
