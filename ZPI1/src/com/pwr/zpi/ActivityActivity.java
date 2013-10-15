package com.pwr.zpi;

import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pwr.zpi.listeners.MyLocationListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityActivity extends FragmentActivity implements
		OnClickListener {

	private GoogleMap mMap;

	private Button stopButton;
	private Button pauseButton;
	private Button resumeButton;
	private TextView DataTextView1;
	private TextView DataTextView2;
	private TextView clickedContentTextView;
	private TextView LabelTextView1;
	private TextView LabelTextView2;
	private TextView clickedLabelTextView;
	private TextView unitTextView1;
	private TextView unitTextView2;
	private TextView clickedUnitTextView;
	private RelativeLayout dataRelativeLayout1;
	private RelativeLayout dataRelativeLayout2;

	private boolean isPaused;
	MyLocationListener myLocationListener;

	// private LinkedList<LinkedList<Location>> trace;
	// private PolylineOptions traceOnMap;
	// private LocationRequest mLocationRequest;
	// private static final long LOCATION_UPDATE_FREQUENCY = 1000;

	// measured values
	double pace;
	double avgPace;
	double distance;
	Long time = 0L;
	long startTime;
	long pauseTime;
	long pauseStartTime;

	private int dataTextView1Content;
	private int dataTextView2Content;
	private int clickedField;
	// measured values IDs
	private static final int distanceID = 0;
	private static final int paceID = 1;
	private static final int avgPaceID = 2;
	private static final int timeID = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);

		startTimer();

		stopButton = (Button) findViewById(R.id.stopButton);
		pauseButton = (Button) findViewById(R.id.pauseButton);
		resumeButton = (Button) findViewById(R.id.resumeButton);
		dataRelativeLayout1 = (RelativeLayout) findViewById(R.id.dataRelativeLayout1);
		dataRelativeLayout2 = (RelativeLayout) findViewById(R.id.dataRelativeLayout2);
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mMap = mapFragment.getMap();

		mMap.setMyLocationEnabled(true);

		// trace = new LinkedList<LinkedList<Location>>();
		stopButton.setOnClickListener(this);
		resumeButton.setOnClickListener(this);
		pauseButton.setOnClickListener(this);
		dataRelativeLayout1.setOnClickListener(this);
		dataRelativeLayout2.setOnClickListener(this);
		pauseTime = 0;
		// traceOnMap = new PolylineOptions();

		DataTextView1 = (TextView) findViewById(R.id.dataTextView1);
		DataTextView2 = (TextView) findViewById(R.id.dataTextView2);

		LabelTextView1 = (TextView) findViewById(R.id.dataTextView1Discription);
		LabelTextView2 = (TextView) findViewById(R.id.dataTextView2Discription);

		unitTextView1 = (TextView) findViewById(R.id.dataTextView1Unit);
		unitTextView2 = (TextView) findViewById(R.id.dataTextView2Unit);

		// to change displayed info, change dataTextViewContent and start
		// initLabelsMethod
		dataTextView1Content = distanceID;
		dataTextView2Content = timeID;

		initLabels(DataTextView1, LabelTextView1, dataTextView1Content);
		initLabels(DataTextView2, LabelTextView2, dataTextView2Content);

		// TODO pobra� z intencji zamiast tak
		myLocationListener = MainScreenActivity.locationListener;
		myLocationListener.start(this);
		startTime = System.currentTimeMillis();
		moveSystemControls(mapFragment);

	}

	Handler handler;
	Runnable timeHandler;
//	Timer timer;
//	TimerTask timerTask;

	private void startTimer() {
		handler = new Handler();
		timeHandler = new Runnable() {

			@Override
			public void run() {
				runTimerTask();
			}
		};
		handler.post(timeHandler);
	}

	protected void runTimerTask() {

		synchronized (time) {
			time = System.currentTimeMillis() - startTime - pauseTime;

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					updateData(DataTextView1, dataTextView1Content);
					updateData(DataTextView2, dataTextView2Content);
				}
			});
		}
		handler.postDelayed(timeHandler, 1000);
	}

	private void moveSystemControls(SupportMapFragment mapFragment) {

		View zoomControls = mapFragment.getView().findViewById(0x1);

		if (zoomControls != null
				&& zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
			// ZoomControl is inside of RelativeLayout
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls
					.getLayoutParams();

			// Align it to - parent top|left
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

			// nie do ko�ca rozumiem t� metod�, trzeba zobaczy� czy u Ciebie
			// jest to samo czy nie za bardzo
			final int margin = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP,
					getResources().getDimension(R.dimen.zoom_buttons_margin),
					getResources().getDisplayMetrics());
			params.setMargins(0, 0, 0, margin);
		}
		View locationControls = mapFragment.getView().findViewById(0x2);

		if (locationControls != null
				&& locationControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
			// ZoomControl is inside of RelativeLayout
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationControls
					.getLayoutParams();

			// Align it to - parent top|left
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

			// Update margins, set to 10dp
			final int margin1 = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP,
					getResources().getDimension(
							R.dimen.location_button_margin_top), getResources()
							.getDisplayMetrics());
			final int margin2 = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP,
					getResources().getDimension(
							R.dimen.location_button_margin_right),
					getResources().getDisplayMetrics());
			params.setMargins(0, margin1, margin2, 0);
		}
	}

	private void initLabels(TextView textViewInitialValue, TextView textView,
			int meassuredValue) {
		switch (meassuredValue) {
		case distanceID:
			textView.setText(R.string.distance);
			textViewInitialValue.setText("0.000");
			break;
		case paceID:
			textView.setText(R.string.pace);
			textViewInitialValue.setText("0:00");
			break;
		case avgPaceID:
			textView.setText(R.string.pace_avrage);
			textViewInitialValue.setText("0:00");
			break;
		case timeID:
			textView.setText(R.string.time);
			textViewInitialValue.setText("00:00:00");
			break;
		}

	}

	private void updateLabels(int meassuredValue, TextView labelTextView,
			TextView unitTextView, TextView contentTextView) {
		switch (meassuredValue) {
		case distanceID:
			labelTextView.setText(R.string.distance);
			unitTextView.setText(R.string.km);
			break;
		case paceID:
			labelTextView.setText(R.string.pace);
			unitTextView.setText(R.string.minutes_per_km);
			break;
		case avgPaceID:
			labelTextView.setText(R.string.pace_avrage);
			unitTextView.setText(R.string.minutes_per_km);
			break;
		case timeID:
			labelTextView.setText(R.string.time);
			unitTextView.setText("");
			break;
		}

		updateData(contentTextView, meassuredValue);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		showAlertDialog();
	}

	private void showAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Add the buttons
		builder.setTitle(R.string.dialog_message_on_stop);
		builder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
						overridePendingTransition(R.anim.in_up_anim,
								R.anim.out_up_anim);
					}
				});
		builder.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
		// Set other dialog properties

		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();

	}

	@Override
	public void onClick(View v) {
		if (v == stopButton) {
			// TODO finish and save activity
			showAlertDialog();
		} else if (v == pauseButton) { //stop time
			myLocationListener.setPaused(!myLocationListener.isPaused());
			isPaused = myLocationListener.isPaused();
				stopButton.setVisibility(View.GONE);
				pauseButton.setVisibility(View.GONE);
				resumeButton.setVisibility(View.VISIBLE);
			pauseStartTime = System.currentTimeMillis();
			
			handler.removeCallbacks(timeHandler);
		} else if (v == resumeButton) { //start time
			myLocationListener.setPaused(!myLocationListener.isPaused());
			isPaused = myLocationListener.isPaused();
				stopButton.setVisibility(View.VISIBLE);
				pauseButton.setVisibility(View.VISIBLE);
				resumeButton.setVisibility(View.GONE);
			pauseTime += System.currentTimeMillis() - pauseStartTime;
			// trace.add(new LinkedList<Location>());
			
			handler.post(timeHandler);
		} else if (v == dataRelativeLayout1) {
			clickedContentTextView = DataTextView1;
			clickedLabelTextView = LabelTextView1;
			clickedUnitTextView = unitTextView1;
			clickedField = 1;
			showMeassuredValuesMenu();
		} else if (v == dataRelativeLayout2) {
			clickedContentTextView = DataTextView2;
			clickedLabelTextView = LabelTextView2;
			clickedUnitTextView = unitTextView2;
			clickedField = 2;
			showMeassuredValuesMenu();
		}

	}

	private String getMyString(int stringId) {
		return getResources().getString(stringId);
	}

	private void showMeassuredValuesMenu() {
		// chcia�em zrobi� tablice w stringach, ale potem zobaczy�em, �e ju� mam
		// te wszystkie nazwy i teraz nie wiem czy tamto zmienia� w tablic� czy
		// nie ma sensu
		// kolejno�� w tablicy musi odpowiada� nr ID, tzn 0 - dystans itp.

		final CharSequence[] items = { getMyString(R.string.distance),
				getMyString(R.string.pace), getMyString(R.string.pace_avrage),
				getMyString(R.string.time) };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_choose_what_to_display);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				updateLabels(item, clickedLabelTextView, clickedUnitTextView,
						clickedContentTextView);
				if (clickedField == 1)
					dataTextView1Content = item;
				else
					dataTextView2Content = item;
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void updateData(TextView textBox, int meassuredValue) {

		switch (meassuredValue) {
		case distanceID:
			textBox.setText(String.format("%.3f", distance / 1000));
			break;
		case paceID:
			if (pace < 30) {
				// convert pace to show second
				double rest = pace - (int) pace;
				rest = rest * 60;

				String secondsZero = (rest < 10) ? "0" : "";

				textBox.setText(String.format("%.0f:%s%.0f", pace, secondsZero,
						rest));
			} else {
				textBox.setText(getResources().getString(R.string.dashes));
			}
			break;
		case avgPaceID:
			if (avgPace < 30) {
				// convert pace to show second
				double rest = avgPace - (int) avgPace;
				rest = rest * 60;

				String secondsZero = (rest < 10) ? "0" : "";

				textBox.setText(String.format("%.0f:%s%.0f", avgPace,
						secondsZero, rest));
			} else {
				textBox.setText(getResources().getString(R.string.dashes));
			}
			break;
		case timeID:
			long hours = time / 3600000;
			long minutes = (time / 60000) - hours * 60;
			long seconds = (time / 1000) - hours * 3600 - minutes * 60;
			String hourZero = (hours < 10) ? "0" : "";
			String minutesZero = (minutes < 10) ? "0" : "";
			String secondsZero = (seconds < 10) ? "0" : "";

			textBox.setText(String.format("%s%d:%s%d:%s%d", hourZero, hours,
					minutesZero, minutes, secondsZero, seconds));
			break;
		}

	}

	public void countData(Location location, Location lastLocation) {

		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		mMap.addPolyline(new PolylineOptions().add(
				new LatLng(lastLocation.getLatitude(), lastLocation
						.getLongitude())).add(latLng));

		mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

		float speed = location.getSpeed();
		Toast.makeText(this, location.getAccuracy() + "", Toast.LENGTH_SHORT)
				.show();

		pace = (double) 1 / (speed * 60 / 1000);
		// DataTextView3.setText(pace + " min/km");

		distance += lastLocation.distanceTo(location);
		// DataTextView1.setText(distance / 1000 + " km");

		synchronized (time) {
			avgPace = ((double) time / 60) / distance;
		}

		updateData(DataTextView1, dataTextView1Content);
		updateData(DataTextView2, dataTextView2Content);

	}

	@Override
	protected void onStop() {
		LocationClient locationClient = myLocationListener.getmLocationClient();
		// If the client is connected
		if (locationClient.isConnected()) {
			/*
			 * Remove location updates for a listener. The current Activity is
			 * the listener, so the argument is "this".
			 */
			locationClient.removeLocationUpdates(myLocationListener);
		}
		/*
		 * After disconnect() is called, the client is considered "dead".
		 */
		locationClient.disconnect();
		super.onStop();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		myLocationListener.getmLocationClient().connect();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			showAlertDialog();
		}
		return super.onKeyDown(keyCode, event);
	}

}
