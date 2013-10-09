package com.pwr.zpi;

import com.pwr.zpi.listeners.GestureListener;
import com.pwr.zpi.listeners.MyGestureDetector;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreenActivity extends Activity implements GestureListener, OnClickListener {

	private TextView GPSStatusTextView;
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private static final short GPS_NOT_ENABLED = 0;
	private static final short NO_GPS_SIGNAL = 1;
	private static final short GPS_WORKING = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen_activity);

		GPSStatusTextView = (TextView) findViewById(R.id.textViewGPSIndicator);
		GPSStatusTextView.setOnClickListener(this);
			
		
		prepareGestureListener();
		addListeners();
	}

	private void addListeners() {
		// TODO add listers to all views
	}

	private void prepareGestureListener() {
		// Gesture detection
		gestureDetector = new GestureDetector(this, new MyGestureDetector(this, true, true, true, true));
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkGPS();
		Toast.makeText(this, "onResume", Toast.LENGTH_LONG);
	}

	@Override
	protected void onPause() {
		super.onPause();
		checkGPS();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureListener.onTouch(null, event);
	}

	private short checkGPS() {
		
		short gpsStatus = 0;
		
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = service
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!enabled) {
			GPSStatusTextView.setText(getResources().getString(
					R.string.gps_disabled));
			gpsStatus = GPS_NOT_ENABLED;
			
		} else {
			GPSStatusTextView.setText(getResources().getString(
					R.string.gps_enabled));
			gpsStatus = NO_GPS_SIGNAL;
			
		}
		return gpsStatus;
	}

	private void startActivity(Class<? extends Activity> activity) {
		Intent i = new Intent(MainScreenActivity.this, activity);
		startActivity(i);
	}

	@Override
	public void onLeftToRightSwipe() {
		startActivity(PlaningActivity.class);
	}

	@Override
	public void onRightToLeftSwipe() {
		startActivity(HistoryActivity.class);
	}

	@Override
	public void onUpToDownSwipe() {
		startActivity(ActivityActivity.class);
	}

	@Override
	public void onDownToUpSwipe() {
		startActivity(SettingsActivity.class);
	}

	@Override
	public void onClick(View v) {
		
		if (v == GPSStatusTextView)
		{
			//if gps is not running
			if (checkGPS() == GPS_NOT_ENABLED)
			{
				Intent intent = new
				Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		}
		
	}

}
