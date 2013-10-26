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

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SingleRunHistoryActivity extends FragmentActivity implements OnClickListener{

	GoogleMap mMap;
	private TextView distanceTextView;
	private TextView timeTextView;
	private TextView avgPaceTextView;
	private Button chartButton;
	
	SingleRun run;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_run_history_activity);
		
		init();
		loadData(getIntent().getLongExtra(HistoryActivity.ID_TAG, 0));
		
		addListeners();
	}
	
	private void init() {
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mMap = mapFragment.getMap();
		chartButton = (Button) findViewById(R.id.buttonCharts);
	}
	
	private void addListeners() {
		chartButton.setOnClickListener(this);
	}

	private void loadData(long runID)
	{
		Database database = new Database(this);
		run = database.getRun(runID);
		LinkedList<LinkedList<Pair<Location,Long>>> traceWithTime = run.getTraceWithTime();
		for (LinkedList<Pair<Location,Long>> singleTrace:traceWithTime)
		{
			PolylineOptions polyLine = new PolylineOptions();
			for (Pair<Location,Long> singlePoint:singleTrace)
			{
				polyLine.add(new LatLng(singlePoint.first.getLatitude(), singlePoint.first.getLongitude()));
			}
			if (mMap!=null)mMap.addPolyline(polyLine);
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
		}
	}
}
