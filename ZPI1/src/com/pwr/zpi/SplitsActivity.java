package com.pwr.zpi;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.pwr.zpi.adapters.SplitsAdapter;
import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.utils.GeographicalEvaluations;
import com.pwr.zpi.utils.Pair;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class SplitsActivity extends Activity {

	ListView splitsListView;
	RelativeLayout noSplitsInfoRelativeLayout;
	SingleRun singleRun;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splits_activity);
		
		init();
	}

	private void init() {
		singleRun = getSingleRunFromIntent();
		
		splitsListView = (ListView) findViewById(R.id.listViewSplits);
		noSplitsInfoRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutNoSplitsInfo);
		splitsListView.setAdapter(new SplitsAdapter(this, R.layout.splits_activity_list_item, parseData(singleRun.getTraceWithTime())));
		if (splitsListView.getAdapter().getCount() == 0) {
			splitsListView.setVisibility(View.GONE);
			noSplitsInfoRelativeLayout.setVisibility(View.VISIBLE);
		}
	}


	//parses location and time to distance kilometer by kilometer and associated times
	private List<Pair<Integer, Pair<Long, Long>>> parseData(
			LinkedList<LinkedList<Pair<Location, Long>>> traceWithTime) {
		List<Pair<Integer, Pair<Long, Long>>> result = new ArrayList<Pair<Integer,Pair<Long,Long>>>();
		
		int nextKilometer = 1;
		long cumulativeTime = 0;
		double currentDistance = 0;
		long currentTime = 0;
		
		for (LinkedList<Pair<Location,Long>> subrun : traceWithTime) {
			Pair<Location, Long> previous = subrun.removeFirst();
			for (Pair<Location, Long> current : subrun) {
				currentDistance += GeographicalEvaluations.countDistance(previous.first, current.first);
				currentTime += current.second - previous.second;
				
				if (currentDistance >= nextKilometer) {
					cumulativeTime += currentTime;
					result.add(new Pair<Integer, Pair<Long,Long>>(nextKilometer, new Pair<Long, Long>(currentTime, cumulativeTime)));
					
					nextKilometer++;
					currentTime = 0;
				}
				
				previous = current;
			}
		}
		
		return result;
	}

	private SingleRun getSingleRunFromIntent() {
		long run_id = getIntent().getLongExtra(SingleRunHistoryActivity.RUN_ID, -1);
		SingleRun singleRun = new Database(this).getRun(run_id);
		return singleRun;
	}
	
}
