package com.pwr.zpi;

import com.pwr.zpi.listeners.GestureListener;
import com.pwr.zpi.listeners.MyGestureDetector;
import com.pwr.zpi.views.VerticalTextView;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreenActivity extends Activity implements OnClickListener,
		GestureListener {

	private TextView GPSStatusTextView;
	private Button settingsButton;
	private VerticalTextView historyButton;
	private VerticalTextView planningButton;
	private Button startButton;
	private Button musicButton;

	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;

	private static final short GPS_NOT_ENABLED = 0;
	private static final short NO_GPS_SIGNAL = 1;
	private static final short GPS_WORKING = 2;

	private static final short LEFT = 0;
	private static final short RIGHT = 1;
	private static final short UP = 2;
	private static final short DOWN = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen_activity);

		GPSStatusTextView = (TextView) findViewById(R.id.textViewGPSIndicator);
		settingsButton = (Button) findViewById(R.id.buttonSettings);
		historyButton = (VerticalTextView) findViewById(R.id.buttonHistory);
		planningButton = (VerticalTextView) findViewById(R.id.buttonPlans);
		startButton = (Button) findViewById(R.id.buttonStart);
		musicButton = (Button) findViewById(R.id.buttonMusic);

		prepareGestureListener();
		addListeners();
	}

	private void addListeners() {
		GPSStatusTextView.setOnClickListener(this);
		settingsButton.setOnClickListener(this);
		historyButton.setOnClickListener(this);
		startButton.setOnClickListener(this);
		planningButton.setOnClickListener(this);
		musicButton.setOnClickListener(this);
		// TODO add listeners to buttons so that swipe will work

		GPSStatusTextView.setOnTouchListener(gestureListener);
		settingsButton.setOnTouchListener(gestureListener);
		historyButton.setOnTouchListener(gestureListener);
		startButton.setOnTouchListener(gestureListener);
		planningButton.setOnTouchListener(gestureListener);
	}

	private void prepareGestureListener() {
		// Gesture detection
		gestureDetector = new GestureDetector(this, new MyGestureDetector(this,
				true, true, true, true));
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

	// TODO check if gps has signal
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

	private void startActivity(Class<? extends Activity> activity,
			final short swipeDirection) {
		final Intent i = new Intent(MainScreenActivity.this, activity);

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				Toast.makeText(MainScreenActivity.this, "loading",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				startActivity(i);
				if (swipeDirection == RIGHT) {
					overridePendingTransition(R.anim.in_right_anim,
							R.anim.out_right_anim);
				} else if (swipeDirection == LEFT) {
					overridePendingTransition(R.anim.in_left_anim,
							R.anim.out_left_anim);
				} else if (swipeDirection == DOWN) {
					overridePendingTransition(R.anim.in_down_anim,
							R.anim.out_down_anim);
				} else if (swipeDirection == UP) {
					overridePendingTransition(R.anim.in_up_anim,
							R.anim.out_up_anim);
				}
				return null;
			}

		}.execute();
	}

	@Override
	public void onLeftToRightSwipe() {
		startActivity(PlaningActivity.class, RIGHT);

	}

	@Override
	public void onRightToLeftSwipe() {
		startActivity(HistoryActivity.class, LEFT);

	}

	@Override
	public void onUpToDownSwipe() {
		startActivity(ActivityActivity.class, DOWN);

	}

	@Override
	public void onDownToUpSwipe() {
		startActivity(SettingsActivity.class, UP);

	}

	@Override
	public void onClick(View v) {

		if (v == GPSStatusTextView) {
			// if gps is not running
			if (checkGPS() == GPS_NOT_ENABLED) {
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		} else if (v == startButton) {
			startActivity(ActivityActivity.class, DOWN);

		} else if (v == historyButton) {
			startActivity(HistoryActivity.class, LEFT);

		} else if (v == settingsButton) {
			startActivity(SettingsActivity.class, UP);

		} else if (v == planningButton) {
			startActivity(PlaningActivity.class, RIGHT);
		} else if (v == musicButton) {
			startSystemMusicPlayer();
		}
	}

	private void startSystemMusicPlayer() {
		Intent i;
		i = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
		startActivity(i);
	}
}