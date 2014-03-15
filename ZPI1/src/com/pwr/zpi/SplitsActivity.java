package com.pwr.zpi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.analytics.tracking.android.EasyTracker;
import com.pwr.zpi.adapters.AdapterFactory;
import com.pwr.zpi.adapters.AdapterFactory.AdapterType;
import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.utils.Pair;
import com.pwr.zpi.views.TopBar;

public class SplitsActivity extends Activity implements OnClickListener {
	
	ListView splitsListView;
	RelativeLayout noSplitsInfoRelativeLayout;
	SingleRun singleRun;
	private RelativeLayout leftButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splits_activity);
		
		init();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}
	
	private void init() {
		singleRun = getSingleRunFromIntent();
		splitsListView = (ListView) findViewById(R.id.listViewSplits);
		noSplitsInfoRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutNoSplitsInfo);
		splitsListView.setAdapter(AdapterFactory.getAdapter(AdapterType.SplitsAdapter, this,
			parseData(singleRun.getTraceWithTime()), null));
		if (splitsListView.getAdapter().getCount() == 0) {
			splitsListView.setVisibility(View.GONE);
			noSplitsInfoRelativeLayout.setVisibility(View.VISIBLE);
		}
		
		TopBar topBar = (TopBar) findViewById(R.id.topBarSplits);
		leftButton = topBar.getLeftButton();
		leftButton.setOnClickListener(this);
	}
	
	//parses location and time to distance kilometer by kilometer and associated times
	private List<Pair<Integer, Pair<Long, Long>>> parseData(LinkedList<LinkedList<Pair<Location, Long>>> traceWithTime) {
		List<Pair<Integer, Pair<Long, Long>>> result = new ArrayList<Pair<Integer, Pair<Long, Long>>>();
		
		int nextKilometer = 1;
		long cumulativeTime = 0;
		double currentDistance = 0;
		long currentTime = 0;
		if (traceWithTime == null) {
			traceWithTime = new LinkedList<LinkedList<Pair<Location, Long>>>();	//this should act as if there are no points
		}
		for (LinkedList<Pair<Location, Long>> subrun : traceWithTime) {
			Pair<Location, Long> previous = subrun.removeFirst();
			for (Pair<Location, Long> current : subrun) {
				currentDistance += previous.first.distanceTo(current.first) / 1000;
				currentTime += current.second - previous.second;
				
				if (currentDistance >= nextKilometer) {
					cumulativeTime += currentTime;
					result.add(new Pair<Integer, Pair<Long, Long>>(nextKilometer, new Pair<Long, Long>(currentTime,
						cumulativeTime)));
					
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
	
	@Override
	public void onClick(View v) {
		if (v == leftButton) {
			finish();
		}
		
	}
	
}
