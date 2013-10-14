package com.pwr.zpi;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.pwr.zpi.dialogs.ErrorDialogFragment;
import com.pwr.zpi.listeners.GestureListener;
import com.pwr.zpi.listeners.MyGestureDetector;
import com.pwr.zpi.listeners.MyLocationListener;
import com.pwr.zpi.views.VerticalTextView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreenActivity extends FragmentActivity implements
		OnClickListener, GestureListener {

	private TextView GPSStatusTextView;
	private TextView GPSSignalTextView;
	private Button settingsButton;
	private VerticalTextView historyButton;
	private VerticalTextView planningButton;
	private Button startButton;
	private Button musicButton;
	
	//TODO potem zmieniê
	public static MyLocationListener locationListener;

	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;

	private static final short GPS_NOT_ENABLED = 0;
	private static final short NO_GPS_SIGNAL = 1;
	private static final short GPS_WORKING = 2;

	private static final short LEFT = 0;
	private static final short RIGHT = 1;
	private static final short UP = 2;
	private static final short DOWN = 3;

	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen_activity);

		GPSStatusTextView = (TextView) findViewById(R.id.textViewGPSIndicator);
		GPSSignalTextView = (TextView) findViewById(R.id.GPSSignalTextView);
		settingsButton = (Button) findViewById(R.id.buttonSettings);
		historyButton = (VerticalTextView) findViewById(R.id.buttonHistory);
		planningButton = (VerticalTextView) findViewById(R.id.buttonPlans);
		startButton = (Button) findViewById(R.id.buttonStart);
		musicButton = (Button) findViewById(R.id.buttonMusic);

		locationListener = new MyLocationListener(this);
		
		prepareGestureListener();
		addListeners();
		
		locationListener.getmLocationClient().connect();
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
		musicButton.setOnTouchListener(gestureListener);
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
			short swipeDirection) {
		Intent i = new Intent(MainScreenActivity.this, activity);

		startActivity(i);
		if (swipeDirection == RIGHT) {
			overridePendingTransition(R.anim.in_right_anim,
					R.anim.out_right_anim);
		} else if (swipeDirection == LEFT) {
			overridePendingTransition(R.anim.in_left_anim, R.anim.out_left_anim);
		} else if (swipeDirection == DOWN) {
			overridePendingTransition(R.anim.in_down_anim, R.anim.out_down_anim);
		} else if (swipeDirection == UP) {
			overridePendingTransition(R.anim.in_up_anim, R.anim.out_up_anim);
		}
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

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getSupportFragmentManager(),
						"Location Updates");
			}
			return false;
		}
	}

	/*
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK:
				/*
				 * Try the request again
				 */
				break;
			}
		}
	}
	public void showGPSAccuracy(double accuracy)
	{
		//TODO - zamieniæ na jakiœ wskaŸnik
		GPSSignalTextView.setText(String.format("%.2f", accuracy));
	}
	@Override
	protected void onStop() {
/*		LocationClient locationClient = locationListener.getmLocationClient();
		// If the client is connected
		if (locationClient.isConnected()) {

			locationClient.removeLocationUpdates(locationListener);
		}

		Log.i("test", "on stop");
		locationClient.disconnect();*/
		super.onStop();
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		//locationListener.getmLocationClient().connect();
	}

	@Override
	protected void onDestroy() {
		LocationClient locationClient = locationListener.getmLocationClient();
		// If the client is connected
		if (locationClient.isConnected()) {
			/*
			 * Remove location updates for a listener. The current Activity is
			 * the listener, so the argument is "this".
			 */
			locationClient.removeLocationUpdates(locationListener);
		}
		/*
		 * After disconnect() is called, the client is considered "dead".
		 */
		locationClient.disconnect();
		super.onDestroy();
	}

	
}
