package com.pwr.zpi;

import java.net.IDN;
import java.util.LinkedList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.utils.GeographicalEvaluations;
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

public class SingleRunHistoryActivity extends FragmentActivity implements
		OnClickListener {

	GoogleMap mMap;
	LatLngBounds.Builder boundsBuilder;
	Polyline traceOnMapObject;
	private TextView distanceTextView;
	private TextView timeTextView;
	private TextView avgPaceTextView;
	private TextView avgSpeedTextView;
	private Button chartButton;

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
		mapCenter();
	}

	private void init() {
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mMap = mapFragment.getMap();
		chartButton = (Button) findViewById(R.id.buttonCharts);
	}

	private void mapCenter() {
		
		LinkedList<LinkedList<Pair<Location, Long>>> traceWithTime = run
				.getTraceWithTime();
	
		boundsBuilder = new LatLngBounds.Builder();
		double lastDistance=0;
		double newDistance =0;
		for (LinkedList<Pair<Location, Long>> singleTrace : traceWithTime) {
			PolylineOptions polyLine = new PolylineOptions();
			
			Location lastLocation = null;
			for (Pair<Location, Long> singlePoint : singleTrace) {
				Location location = singlePoint.first;
				LatLng latLng = new LatLng(location.getLatitude(),
						location.getLongitude());
				polyLine.add(latLng);
				boundsBuilder.include(latLng);
				if (lastLocation!= null)
				{
					newDistance+=location.distanceTo(lastLocation);
					int showDistance = (int)(newDistance/1000);
					if (showDistance-(int)(lastDistance/1000)>0)
					{
						addMarker(location,showDistance);
					}
					
				}
				lastDistance = newDistance;
				
				lastLocation = location;
				
			}
			if (mMap != null)
				traceOnMapObject = mMap.addPolyline(polyLine);
		}
			mMap.setOnCameraChangeListener(new OnCameraChangeListener() {

			    @Override
			    public void onCameraChange(CameraPosition arg0) {
			        // Move camera.
			    	LatLngBounds bounds = boundsBuilder.build();
			        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
			        // Remove listener to prevent position reset on camera move.
			        mMap.setOnCameraChangeListener(null);
			    }
			});
	}

	private void addMarker(Location location, int distance) {
		Marker marker = mMap.addMarker(new MarkerOptions().position(
				new LatLng(location.getLatitude(), location.getLongitude()))
				.title(distance + "km"));
		marker.showInfoWindow();
	}
	
	private void initFields() {
		distanceTextView = (TextView) findViewById(R.id.TextView1History);
		timeTextView = (TextView) findViewById(R.id.TextView2History);
		avgPaceTextView = (TextView) findViewById(R.id.TextView3History);
		avgSpeedTextView = (TextView) findViewById(R.id.TextView4History);
	}

	private void showData() {
		// show distance
		distanceTextView
				.setText(String.format("%.3f", run.getDistance() / 1000));

		// show time
		long time = run.getRunTime();
		long hours = time / 3600000;
		long minutes = (time / 60000) - hours * 60;
		long seconds = (time / 1000) - hours * 3600 - minutes * 60;
		String hourZero = (hours < 10) ? "0" : "";
		String minutesZero = (minutes < 10) ? "0" : "";
		String secondsZero = (seconds < 10) ? "0" : "";

		timeTextView.setText(String.format("%s%d:%s%d:%s%d", hourZero, hours,
				minutesZero, minutes, secondsZero, seconds));

		// show avg pace
		double speed = run.getDistance() / 1000 / run.getRunTime() * 1000 * 60
				* 60;
		double pace = (double) 1 / speed * 60;

		if (pace < 300) // slower is completely irrelevant + it makes text to
						// long
		{
			// convert pace to show second
			double rest = pace - (int) pace;
			rest = rest * 60;
			secondsZero = (rest < 10) ? "0" : "";
			avgPaceTextView.setText(String.format("%d:%s%.0f", (int) pace,
					secondsZero, rest));
		} else
			avgPaceTextView.setText(getResources().getString(R.string.dashes));
		// show avg speed
		avgSpeedTextView.setText(String.format("%.2f", speed));

	}

	private void addListeners() {
		chartButton.setOnClickListener(this);
	}

	private void loadData(long runID) {
		Database database = new Database(this);
		run = database.getRun(runID);
		

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
