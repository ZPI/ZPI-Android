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
import android.widget.TextView;
import android.widget.Toast;

public class MainScreenActivity extends Activity implements GestureListener {

	private TextView GPSStatusTextView;
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen_activity);

		GPSStatusTextView = (TextView) findViewById(R.id.textViewGPSIndicator);

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

	private void checkGPS() {
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = service
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!enabled) {
			GPSStatusTextView.setText(getResources().getString(
					R.string.gps_disabled));
			// Intent intent = new
			// Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			// startActivity(intent);
		} else {
			GPSStatusTextView.setText(getResources().getString(
					R.string.gps_enabled));
		}
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

}
