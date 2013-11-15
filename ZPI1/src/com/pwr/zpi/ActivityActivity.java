package com.pwr.zpi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CameraPosition.Builder;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pwr.zpi.adapters.DrawerWorkoutsAdapter;
import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.dialogs.MyDialog;
import com.pwr.zpi.listeners.MyLocationListener;
import com.pwr.zpi.listeners.OnNextActionListener;
import com.pwr.zpi.services.MyServiceConnection;
import com.pwr.zpi.utils.BeepPlayer;
import com.pwr.zpi.utils.GeographicalEvaluations;
import com.pwr.zpi.utils.Notifications;
import com.pwr.zpi.utils.Pair;
import com.pwr.zpi.utils.TimeFormatter;

public class ActivityActivity extends FragmentActivity implements OnClickListener {
	
	private static final float MIN_SPEED_FOR_AUTO_PAUSE = 0.3f;
	
	private GoogleMap mMap;
	
	private Button stopButton;
	private Button pauseButton;
	private Button resumeButton;
	private ImageButton workoutDdrawerButton;
	private TextView DataTextView1;
	private TextView DataTextView2;
	private TextView clickedContentTextView;
	private TextView LabelTextView1;
	private TextView LabelTextView2;
	private TextView clickedLabelTextView;
	private TextView unitTextView1;
	private TextView unitTextView2;
	private TextView clickedUnitTextView;
	private TextView GPSAccuracy;
	private TextView countDownTextView;
	private LinearLayout startStopLayout;
	private RelativeLayout dataRelativeLayout1;
	private RelativeLayout dataRelativeLayout2;
	private Location mLastLocation;
	private boolean isPaused;
	private SingleRun singleRun;
	private LinkedList<LinkedList<Pair<Location, Long>>> traceWithTime;
	private Calendar calendar;
	private PolylineOptions traceOnMap;
	private Polyline traceOnMapObject;
	private static final float traceThickness = 5;
	private static final int traceColor = Color.RED;
	
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
	
	// service data
	boolean mIsBound;
	
	// time counting fields
	private Handler handler;
	private Runnable timeHandler;
	private static final int COUNT_DOWN_TIME = 5;
	private static final String TAG = ActivityActivity.class.getSimpleName();
	BeepPlayer beepPlayer;
	
	// progress dialog lost gps
	private ProgressDialog lostGPSDialog;
	
	// workout drawer fields
	private DrawerWorkoutsAdapter expandableListAdapter;
	private ListView listView;
	private Workout workout;
	private DrawerLayout drawerLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		
		initFields();
		addListeners();
		
		initDisplayedData();
		
		prepareServiceAndStart();
		
		startTimerAfterCountDown();
		Notifications
		.createNotification(this, ActivityActivity.class, R.string.app_name, R.string.notification_message);
		
	}
	
	// MOCK
	private Workout getWorkoutData() {
		//TODO get workout id from intent
		Intent i = getIntent();
		Workout workout = new Workout();
		
		ArrayList<WorkoutAction> actions = i.getParcelableArrayListExtra(NewWorkoutActivity.LIST_TAG);
		workout.setActions(actions);
		workout.setName(i.getStringExtra(NewWorkoutActivity.NAME_TAG));
		workout.setRepeatCount(i.getIntExtra(NewWorkoutActivity.REPEAT_TAG, 1));
		workout.setWarmUp(i.getBooleanExtra(NewWorkoutActivity.WORMUP_TAG, false));
		return workout;
	}
	
	private void initFields() {
		stopButton = (Button) findViewById(R.id.stopButton);
		pauseButton = (Button) findViewById(R.id.pauseButton);
		resumeButton = (Button) findViewById(R.id.resumeButton);
		workoutDdrawerButton = (ImageButton) findViewById(R.id.imageButtonWorkoutDrawerButton);
		dataRelativeLayout1 = (RelativeLayout) findViewById(R.id.dataRelativeLayout1);
		dataRelativeLayout2 = (RelativeLayout) findViewById(R.id.dataRelativeLayout2);
		GPSAccuracy = (TextView) findViewById(R.id.TextViewGPSAccuracy);
		countDownTextView = (TextView) findViewById(R.id.textViewCountDown);
		startStopLayout = (LinearLayout) findViewById(R.id.startStopLinearLayout);
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mMap = mapFragment.getMap();
		
		traceWithTime = new LinkedList<LinkedList<Pair<Location, Long>>>();
		pauseTime = 0;
		traceOnMap = new PolylineOptions();
		traceOnMap.width(traceThickness);
		traceOnMap.color(traceColor);
		traceOnMapObject = mMap.addPolyline(traceOnMap);
		
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
		
		// make single run object
		singleRun = new SingleRun();
		calendar = Calendar.getInstance();
		
		singleRun.setStartDate(calendar.getTime());
		isPaused = false;
		
		beepPlayer = new BeepPlayer(this);
		
		Intent i = getIntent();
		listView = (ExpandableListView) findViewById(R.id.left_drawer);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (i.hasExtra(PlaningActivity.ID_TAG)) {
			// drawer initialization
			
			workout = getWorkoutData();
			expandableListAdapter = new DrawerWorkoutsAdapter(this, workout);
			listView.setAdapter(expandableListAdapter);
			listView.addHeaderView(getLayoutInflater().inflate(R.layout.workout_drawer_list_header, null));
			listView.setVisibility(View.VISIBLE);
		}
		else {
			workoutDdrawerButton.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
		}
		
		moveSystemControls(mapFragment);
	}
	
	private void addListeners() {
		stopButton.setOnClickListener(this);
		resumeButton.setOnClickListener(this);
		pauseButton.setOnClickListener(this);
		
		dataRelativeLayout1.setOnClickListener(this);
		dataRelativeLayout2.setOnClickListener(this);
		if (workout != null) {
			workoutDdrawerButton.setOnClickListener(this);
			
			drawerLayout.setDrawerListener(new DrawerListener() {
				
				@Override
				public void onDrawerStateChanged(int arg0) {}
				
				@Override
				public void onDrawerSlide(View arg0, float arg1) {}
				
				@Override
				public void onDrawerOpened(View arg0) {
					if (workout == null) {
						drawerLayout.closeDrawer(Gravity.LEFT);
					}
					else {
						listView.smoothScrollToPosition(workout.getCurrentAction() + 4, workout.getActions()
							.size());
					}
				}
				
				@Override
				public void onDrawerClosed(View arg0) {}
			});
			
			workout.setOnNextActionListener(new OnNextActionListener(this));
		}
	}
	
	private void initDisplayedData() {
		GPSAccuracy.setText(getMyString(R.string.gps_accuracy) + " ?");
		
		initLabels(DataTextView1, LabelTextView1, dataTextView1Content);
		initLabels(DataTextView2, LabelTextView2, dataTextView2Content);
	}
	
	private void prepareServiceAndStart() {
		doBindService();
		LocalBroadcastManager.getInstance(this).registerReceiver(mMyServiceReceiver,
			new IntentFilter(MyLocationListener.class.getSimpleName()));
	}
	
	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMyServiceReceiver);
		doUnbindService();
		Notifications.destroyNotification(this);
		super.onDestroy();
	}
	
	// start of timer methods
	private void startTimerAfterCountDown() {
		handler = new Handler();
		prepareTimeCountingHandler();
		pauseButton.setClickable(false);
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
			getString(R.string.key_countdown_before_start), true)) {
			handler.post(new CounterRunnable(COUNT_DOWN_TIME));
		}
		else {
			startTimeCouting();
		}
	}
	
	private class CounterRunnable implements Runnable {
		
		final int x;
		
		public CounterRunnable(int x) {
			this.x = x;
		}
		
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if (x == 0) {
						startTimeCouting();
					}
					else {
						countDownTextView.setText(x + "");
						beepPlayer.playBeep();
						handler.postDelayed(new CounterRunnable(x - 1), 1000);
					}
				}
			});
		}
	}
	
	private void startTimeCouting() {
		countDownTextView.setVisibility(View.GONE);
		startTime = System.currentTimeMillis();
		pauseButton.setClickable(true);
		handler.post(timeHandler);
	}
	
	private void prepareTimeCountingHandler() {
		timeHandler = new Runnable() {
			
			@Override
			public void run() {
				runTimerTask();
			}
		};
	}
	
	protected void runTimerTask() {
		
		synchronized (time) {
			time = System.currentTimeMillis() - startTime - pauseTime;
			if (workout != null) {
				processWorkout();
			}
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
	
	private void processWorkout() {
		if (workout.hasNextAction()) {
			workout.progressWorkout(distance, time);
			expandableListAdapter.notifyDataSetChanged();
			listView.smoothScrollToPosition(workout.getCurrentAction() + 4, workout.getActions().size());
		}
	}
	
	// end of timer methods
	//TODO przesun\B9\E6 \BFeby nie by\B3y pod innymi ikonami
	private void moveSystemControls(SupportMapFragment mapFragment) {
		
		View zoomControls = mapFragment.getView().findViewById(0x1);
		
		if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
			// ZoomControl is inside of RelativeLayout
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();
			
			// Align it to - parent top|left
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			
			// nie do ko�ca rozumiem t� metod�, trzeba zobaczy� czy u
			// Ciebie
			// jest to samo czy nie za bardzo
			final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources()
				.getDimension(R.dimen.zoom_buttons_margin), getResources().getDisplayMetrics());
			params.setMargins(0, 0, 0, margin);
		}
		View locationControls = mapFragment.getView().findViewById(0x2);
		
		if (locationControls != null && locationControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
			// ZoomControl is inside of RelativeLayout
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationControls.getLayoutParams();
			
			// Align it to - parent top|left
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			
			// Update margins, set to 10dp
			final int margin1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources()
				.getDimension(R.dimen.location_button_margin_top), getResources().getDisplayMetrics());
			final int margin2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources()
				.getDimension(R.dimen.location_button_margin_right), getResources().getDisplayMetrics());
			params.setMargins(0, margin1, margin2, 0);
		}
	}
	
	private void initLabels(TextView textViewInitialValue, TextView textView, int meassuredValue) {
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
	
	private void updateLabels(int meassuredValue, TextView labelTextView, TextView unitTextView,
		TextView contentTextView) {
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
				unitTextView.setText(R.string.empty_string);
				break;
		}
		
		updateData(contentTextView, meassuredValue);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		showAlertDialog();
	}
	
	// invoke when finishing activity
	private void saveRun() {
		// add last values
		singleRun.setEndDate(calendar.getTime());
		singleRun.setRunTime(time);
		singleRun.setDistance(distance);
		singleRun.setTraceWithTime(traceWithTime);
		
		// store in DB
		Database db = new Database(this);
		db.insertSingleRun(singleRun);
	}
	
	private void showAlertDialog() {
		MyDialog dialog = new MyDialog();
		DialogInterface.OnClickListener positiveButtonHandler = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				saveRun();
				finish();
				overridePendingTransition(R.anim.in_up_anim, R.anim.out_up_anim);
				mConnection.sendMessage(MyLocationListener.MSG_STOP);
			}
		};
		dialog.showAlertDialog(this, R.string.dialog_message_on_stop, R.string.empty_string, android.R.string.yes,
			android.R.string.no, positiveButtonHandler, null);
	}
	
	private void showLostGpsSignalDialog() {
		lostGPSDialog = ProgressDialog.show(this, getResources().getString(R.string.dialog_message_on_lost_gpsp), null); // TODO strings
		lostGPSDialog.setCancelable(true);
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
			case R.id.stopButton:
				showAlertDialog();
				break;
			case R.id.pauseButton:
				isPaused = true;
				startStopLayout.setVisibility(View.INVISIBLE);
				resumeButton.setVisibility(View.VISIBLE);
				pauseStartTime = System.currentTimeMillis();
				
				handler.removeCallbacks(timeHandler);
				mConnection.sendMessage(MyLocationListener.MSG_PAUSE);
				break;
			case R.id.resumeButton:
				isPaused = false;
				startStopLayout.setVisibility(View.VISIBLE);
				resumeButton.setVisibility(View.GONE);
				pauseTime += System.currentTimeMillis() - pauseStartTime;
				traceWithTime.add(new LinkedList<Pair<Location, Long>>());
				handler.post(timeHandler);
				mConnection.sendMessage(MyLocationListener.MSG_START);
				break;
			case R.id.dataRelativeLayout1:
				clickedContentTextView = DataTextView1;
				clickedLabelTextView = LabelTextView1;
				clickedUnitTextView = unitTextView1;
				clickedField = 1;
				showMeassuredValuesMenu();
				break;
			case R.id.dataRelativeLayout2:
				clickedContentTextView = DataTextView2;
				clickedLabelTextView = LabelTextView2;
				clickedUnitTextView = unitTextView2;
				clickedField = 2;
				showMeassuredValuesMenu();
				break;
			case R.id.imageButtonWorkoutDrawerButton:
				boolean isOpen = drawerLayout.isDrawerOpen(Gravity.LEFT);
				if (!isOpen) {
					drawerLayout.openDrawer(Gravity.LEFT);
				}
				else {
					drawerLayout.closeDrawer(Gravity.LEFT);
				}
				break;
		}
		
	}
	
	private void pauseRun() {
		if (!isPaused) {
			isPaused = true;
			startStopLayout.setVisibility(View.INVISIBLE);
			resumeButton.setVisibility(View.VISIBLE);
			pauseStartTime = System.currentTimeMillis();
			
			handler.removeCallbacks(timeHandler);
		}
	}
	
	private void resumeRun() {
		if (isPaused) {
			isPaused = false;
			startStopLayout.setVisibility(View.VISIBLE);
			resumeButton.setVisibility(View.GONE);
			pauseTime += System.currentTimeMillis() - pauseStartTime;
			traceWithTime.add(new LinkedList<Pair<Location, Long>>());
			handler.post(timeHandler);
		}
	}
	
	private String getMyString(int stringId) {
		return getResources().getString(stringId);
	}
	
	private void showMeassuredValuesMenu() {
		// chcia�em zrobi� tablice w stringach, ale potem zobaczy�em, �e
		// ju� mam
		// te wszystkie nazwy i teraz nie wiem czy tamto zmienia� w tablic�
		// czy
		// nie ma sensu
		// kolejno�� w tablicy musi odpowiada� nr ID, tzn 0 - dystans itp.
		
		final CharSequence[] items = { getMyString(R.string.distance), getMyString(R.string.pace),
			getMyString(R.string.pace_avrage), getMyString(R.string.time) };
		MyDialog dialog = new MyDialog();
		DialogInterface.OnClickListener itemsHandler = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				updateLabels(item, clickedLabelTextView, clickedUnitTextView, clickedContentTextView);
				if (clickedField == 1) {
					dataTextView1Content = item;
				}
				else {
					dataTextView2Content = item;
				}
			}
		};
		dialog.showAlertDialog(this, R.string.dialog_choose_what_to_display, R.string.empty_string,
			R.string.empty_string, R.string.empty_string, null, null, items, itemsHandler);
	}
	
	// update display
	protected void updateData(TextView textBox, int meassuredValue) {
		
		switch (meassuredValue) {
			case distanceID:
				textBox.setText(String.format("%.3f", distance / 1000));
				break;
			case paceID:
				if (pace < 30) {
					textBox.setText(TimeFormatter.formatTimeMMSSorHHMMSS(pace));
				}
				else {
					textBox.setText(getResources().getString(R.string.dashes));
				}
				break;
			case avgPaceID:
				if (avgPace < 30) {
					textBox.setText(TimeFormatter.formatTimeMMSSorHHMMSS(avgPace));
				}
				else {
					textBox.setText(getResources().getString(R.string.dashes));
				}
				break;
			case timeID:
				textBox.setText(TimeFormatter.formatTimeHHMMSS(time));
				break;
		}
	}
	
	// count everything with 2 last location points
	private void countData(Location location, Location lastLocation) {
		
		Log.i("ActivityActivity", "countData: " + location);
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		
		traceOnMap.add(latLng);
		traceOnMapObject.setPoints(traceOnMap.getPoints());
		
		CameraPosition cameraPosition = buildCameraPosition(latLng, location, lastLocation);
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		
		// mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
		
		float speed = location.getSpeed();
		GPSAccuracy.setText(String.format("%s %.2f m", getString(R.string.gps_accuracy), location.getAccuracy()));
		
		pace = (double) 1 / (speed * 60 / 1000);
		
		double lastDistance = distance / 1000;
		distance += lastLocation.distanceTo(location);
		
		int distancetoShow = (int) (distance / 1000);
		// new km
		if (distancetoShow - (int) lastDistance > 0) {
			addMarker(location, distancetoShow);
		}
		
		synchronized (time) {
			avgPace = ((double) time / 60) / distance;
		}
	}
	
	private CameraPosition buildCameraPosition(LatLng latLng, Location location, Location lastLocation) {
		Builder builder = new CameraPosition.Builder().target(latLng).zoom(17);	// Sets the zoom
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_map_3d), true)) {
			builder.bearing(GeographicalEvaluations.countBearing(location, lastLocation)) // Sets the orientation of the
			// camera to east
			.tilt(60); // Creates a CameraPosition from the builder
		}
		return builder.build();
	}
	
	private void addMarker(Location location, int distance) {
		Marker marker = mMap.addMarker(new MarkerOptions().position(
			new LatLng(location.getLatitude(), location.getLongitude())).title(distance + "km"));
		marker.showInfoWindow();
	}
	
	// this runs on every update
	private void updateGpsInfo(Location newLocation) {
		autoPauseIfEnabled(newLocation);
		
		// no pause and good gps
		if (!isPaused && newLocation.getAccuracy() < MyLocationListener.REQUIRED_ACCURACY) {
			// not first point after start or resume
			
			if (lostGPSDialog != null) {
				lostGPSDialog.dismiss();
				lostGPSDialog = null;
			}
			
			if (!traceWithTime.isEmpty() && !traceWithTime.getLast().isEmpty()) {
				
				if (mLastLocation == null) {
					Log.e("Location_info", "Shouldn't be here, mLastLocation is null");
				}
				
				countData(newLocation, mLastLocation);
			}
			if (traceWithTime.isEmpty()) {
				traceWithTime.add(new LinkedList<Pair<Location, Long>>());
			}
			updateData(DataTextView1, dataTextView1Content);
			updateData(DataTextView2, dataTextView2Content);
			synchronized (time) {
				traceWithTime.getLast().add(new Pair<Location, Long>(newLocation, time));
			}
		}
		else if (newLocation.getAccuracy() >= MyLocationListener.REQUIRED_ACCURACY) {
			// TODO make progress dialog, waiting for gps
			showLostGpsSignalDialog();
		}
		mLastLocation = newLocation;
	}
	
	private void autoPauseIfEnabled(Location newLocation) {
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_auto_pause), false)) {
			if (newLocation.hasSpeed()) {
				Log.e(TAG, "Speed:" + newLocation.getSpeed());
				if (newLocation.getSpeed() < MIN_SPEED_FOR_AUTO_PAUSE) {
					pauseRun();
				}
				else {
					resumeRun();
				}
			}
			else {
				Log.e(TAG, "No speed.. pausing anyway");
				pauseRun();
			}
		}
	}
	
	@Override
	protected void onPause() {
		beepPlayer.stopPlayer();
		super.onPause();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			showAlertDialog();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	// SERVICE METHODS
	private final MyServiceConnection mConnection = new MyServiceConnection(this, MyServiceConnection.ACTIVITY);
	
	void doBindService() {
		
		Log.i("Service_info", "ActivityActivity Binding");
		Intent i = new Intent(ActivityActivity.this, MyLocationListener.class);
		i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		i.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		bindService(i, mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
		
	}
	
	void doUnbindService() {
		Log.i("Service_info", "Activity Unbinding");
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
			
		}
	}
	
	// handler for the events launched by the service
	private final BroadcastReceiver mMyServiceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int messageType = intent.getIntExtra(MyLocationListener.MESSAGE, -1);
			switch (messageType) {
				case MyLocationListener.MSG_SEND_LOCATION:
					Log.i("Service_info", "ActivityActivity: got Location");
					
					Location newLocation = (Location) intent.getParcelableExtra("Location");
					
					updateGpsInfo(newLocation);
					
					break;
			}
		}
	};
}
