package com.pwr.zpi.adapters;

import android.view.View;
import android.widget.TextView;

import com.pwr.zpi.R;
import com.pwr.zpi.database.entity.TreningPlan;

public class TreningPlansRowBuilder extends RowBuilder<TreningPlan> {
	
	@Override
	public AbstractRowHolder buildRowHolder(View row, TreningPlan item) {
		RunHolder holder = new RunHolder();
		holder.name = (TextView) row.findViewById(R.id.textViewWorkoutItemName);
		return holder;
	}
	
	@Override
	public void fillRowDataToHolder(AbstractRowHolder holder, TreningPlan item, int position, int[] layoutResourcesIDs) {
		RunHolder rowHolder = (RunHolder) holder;
		rowHolder.name.setText(item.getName());
	}
	
	static class RunHolder extends AbstractRowHolder {
		TextView name;
	}
}
