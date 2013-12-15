package com.pwr.zpi.adapters;

import android.view.View;
import android.widget.TextView;

import com.pwr.zpi.R;
import com.pwr.zpi.utils.Pair;
import com.pwr.zpi.utils.TimeFormatter;

public class SplitsRowBuilder extends RowBuilder<Pair<Integer, Pair<Long, Long>>> {
	
	@Override
	public RowHolder buildRowHolder(View row, Pair<Integer, Pair<Long, Long>> item) {
		RowHolder holder;
		holder = new RowHolder();
		holder.kilometer = (TextView) row.findViewById(R.id.textViewKilometerNumber);
		holder.splitTime = (TextView) row.findViewById(R.id.textViewSplitTime);
		holder.totalTime = (TextView) row.findViewById(R.id.textViewTotalTime);
		return holder;
	}
	
	@Override
	public void fillRowDataToHolder(AbstractRowHolder holder, Pair<Integer, Pair<Long, Long>> item, int position,
		int[] layoutResourcesIDs) {
		RowHolder rowHolder = (RowHolder) holder;
		rowHolder.kilometer.setText(String.valueOf(item.first));
		rowHolder.splitTime.setText(TimeFormatter.formatTimeMMSSorHHMMSS(item.second.first));
		rowHolder.totalTime.setText(TimeFormatter.formatTimeMMSSorHHMMSS(item.second.second));
	}
	
	private class RowHolder extends AbstractRowHolder {
		TextView kilometer;
		TextView splitTime;
		TextView totalTime;
	}
}
