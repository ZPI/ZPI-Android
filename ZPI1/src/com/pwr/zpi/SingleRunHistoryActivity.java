package com.pwr.zpi;

import java.net.IDN;
import java.util.LinkedList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.utils.LineChart;
import com.pwr.zpi.utils.Pair;
import com.pwr.zpi.utils.TimeFormatter;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SingleRunHistoryActivity extends FragmentActivity implements
		OnClickListener {

	protected static final String RUN_ID = "runID";

	GoogleMap mMap;
	private TextView distanceTextView;
	private TextView timeTextView;
	private TextView avgPaceTextView;
	private TextView avgSpeedTextView;
	private Button chartButton;
	private Button splitsButton;

	SingleRun run;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_run_history_activity);

		init();
		loadData(getIntent().getLongExtra(HistoryActivity.ID_TAG, 0));
		initFields();
		showData();
		addListeners();
	}

	private void init() {
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mMap = mapFragment.getMap();
		chartButton = (Button) findViewById(R.id.buttonCharts);
		splitsButton = (Button) findViewById(R.id.buttonSplits);
	}
	
	private void initFields() {
		distanceTextView = (TextView) findViewById(R.id.TextView1History);
		timeTextView = (TextView) findViewById(R.id.TextView2History);
		avgPaceTextView = (TextView) findViewById(R.id.TextView3History);
		avgSpeedTextView = (TextView) findViewById(R.id.TextView4History);
	}
	
	private void showData()
	{
		//show distance
		distanceTextView.setText(String.format("%.3f",run.getDistance()/1000));
		
		//show time
		long time = run.getRunTime();
		timeTextView.setText(TimeFormatter.formatTimeHHMMSS(time));
		
		//show avg pace
		double speed = run.getDistance()/1000/run.getRunTime()*1000*60*60;
		double pace = (double)1/speed*60;

		if (pace<300) //slower is completely irrelevant + it makes text to long
		{
			// convert pace to show second
			double rest = pace - (int) pace;
			rest = rest * 60;
			avgPaceTextView.setText(String.format("%d:%02.0f", (int) pace,
					rest));
		}
		else
			avgPaceTextView.setText(getResources().getString(R.string.dashes));
		//show avg speed
		avgSpeedTextView.setText(String.format("%.2f",speed));
		
	}
	
	private void addListeners() {
		chartButton.setOnClickListener(this);
		splitsButton.setOnClickListener(this);
	}

	private void loadData(long runID) {
		Database database = new Database(this);
		run = database.getRun(runID);
		LinkedList<LinkedList<Pair<Location, Long>>> traceWithTime = run
				.getTraceWithTime();
		for (LinkedList<Pair<Location, Long>> singleTrace : traceWithTime) {
			PolylineOptions polyLine = new PolylineOptions();
			for (Pair<Location, Long> singlePoint : singleTrace) {
				polyLine.add(new LatLng(singlePoint.first.getLatitude(),
						singlePoint.first.getLongitude()));
			}
			if (mMap != null)
				mMap.addPolyline(polyLine);
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_right_anim, R.anim.out_right_anim);

	}

	@Override
	public void onClick(View view) {
		if (view == chartButton) {
			Intent i = LineChart.getChartForData(run, this);
			startActivity(i);
		} else if (view == splitsButton) {
			Intent i = new Intent(SingleRunHistoryActivity.this,
					SplitsActivity.class);
			i.putExtra(RUN_ID, run.getRunID());
			startActivity(i);
		}
	}
}
