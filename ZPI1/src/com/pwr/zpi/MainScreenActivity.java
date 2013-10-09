package com.pwr.zpi;

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

public class MainScreenActivity extends Activity {

	TextView GPSStatusTextView;

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
		gestureDetector = new GestureDetector(this, new MyGestureDetector());
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

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Toast.makeText(MainScreenActivity.this, "Left Swipe",
							Toast.LENGTH_SHORT).show();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Toast.makeText(MainScreenActivity.this, "Right Swipe",
							Toast.LENGTH_SHORT).show();
				} else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					Toast.makeText(MainScreenActivity.this, "Up Swipe",
							Toast.LENGTH_SHORT).show();
				} else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					Toast.makeText(MainScreenActivity.this, "Down Swipe",
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
			}
			return false;
		}

	}

}
