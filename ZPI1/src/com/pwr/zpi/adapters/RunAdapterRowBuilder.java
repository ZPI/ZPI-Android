package com.pwr.zpi.adapters;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import com.pwr.zpi.R;
import com.pwr.zpi.database.entity.SingleRun;

public class RunAdapterRowBuilder extends RowBuilder<SingleRun> {
	
	@Override
	public AbstractRowHolder buildRowHolder(View row, SingleRun item) {
		RunHolder holder = new RunHolder();
		holder.date = (TextView) row.findViewById(R.id.textViewHistoryItemDate);
		holder.distance = (TextView) row.findViewById(R.id.textViewHistoryItemDistance);
		holder.name = (TextView) row.findViewById(R.id.textViewHistoryItemName);
		return holder;
	}
	
	@Override
	public void fillRowDataToHolder(AbstractRowHolder holder, SingleRun item, int position, int[] layoutResourcesIDs) {
		RunHolder runHolder = (RunHolder) holder;
		runHolder.name.setText(item.getName());
		runHolder.date.setText(DateFormat.format("yyyy.MM.dd, kk:mm", item.getStartDate()));
		runHolder.distance.setText(String.format("%.3fkm", item.getDistance() / 1000));
	}
	
	static class RunHolder extends AbstractRowHolder {
		TextView date;
		TextView distance;
		TextView name;
	}
}
