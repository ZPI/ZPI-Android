package com.pwr.zpi;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pwr.zpi.adapters.AdapterFactory;
import com.pwr.zpi.adapters.AdapterFactory.AdapterType;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.database.entity.WorkoutActionWarmUp;
import com.pwr.zpi.listeners.ActityButtonStateChangeListener;
import com.pwr.zpi.services.LocationService;
import com.pwr.zpi.utils.ActualPaceCalculator;
import com.pwr.zpi.views.GPSSignalDisplayer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityNoMapActivity extends AbstractActivityActivity implements
		OnClickListener {

	public static final String TAG = ActivityNoMapActivity.class.getName();

	// views
	private Button buttonPause;
	private Button buttonResume;
	private Button buttonStop;
	private GPSSignalDisplayer gpsDisplayer;
	private BigDataField field1;
	private BigDataField field2;
	private BigDataField field3;
	private BigDataField field4;
	private ImageButton imageButtonGoToScreen1;
	private LinearLayout startStopLayout;
	private TextView textViewCountDown;

	// service data
	boolean mIsBound;
	boolean isServiceConnected;
	private RunListenerApi api;
	private Handler handlerForService;

	// measured values
	private double pace;
	private double avgPace;
	private double distance;
	private double lastDistance;
	private Long time = 0L;
	private int runNumber;
	private ActualPaceCalculator actualPaceCalculator;

	// workout fields
	private Workout workout;
	private Workout workoutCopy;

	// progress dialog lost gps
	private ProgressDialog lostGPSDialog;
	private boolean progressDialogDisplayed;

	// location data
	private Location mLastLocation;
	private Location mPreLastLocation;

	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		setContentView(R.layout.activity_nomap_activity);
		initFields();
		addListeners();

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		prepareServiceAndStart();
	}
	
	private void initFields() {
		buttonPause = (Button) findViewById(R.id.buttonNoMapPause);
		buttonResume = (Button) findViewById(R.id.buttonNoMapResume);
		buttonStop = (Button) findViewById(R.id.buttonNoMapStop);
		imageButtonGoToScreen1 = (ImageButton) findViewById(R.id.imageButtonGoToActivityActivity);
		gpsDisplayer = (GPSSignalDisplayer) findViewById(R.id.gpsDisplayerNoMapActivity);
		startStopLayout = (LinearLayout) findViewById(R.id.startStopLinearLayout);
		textViewCountDown = (TextView) findViewById(R.id.textViewActivityNoMapCountDown);
		field1 = new BigDataField();
		field2 = new BigDataField();
		field3 = new BigDataField();
		field4 = new BigDataField();
		initBigDataField(field1, findViewById(R.id.layoutNoMapField1));
		initBigDataField(field2, findViewById(R.id.layoutNoMapField2));
		initBigDataField(field3, findViewById(R.id.layoutNoMapField3));
		initBigDataField(field4, findViewById(R.id.layoutNoMapField4));

		initDisplayedData();
		runNumber = getIntent().getIntExtra(ActivityActivity.RUN_NUMBER_TAG, 0);
		initWorkoutData();
		isPaused = false;
		pausedManually = false;
		progressDialogDisplayed = false;
	}

	private void initWorkoutData() {
		Intent intent = this.getIntent();
		if (intent.hasExtra(Workout.TAG)) {
			// drawer initialization
			workout = getWorkoutData(getIntent());
			workoutCopy = new Workout();
			List<WorkoutAction> actions = new ArrayList<WorkoutAction>();
			if (workout.isWarmUp()) {
				workoutCopy.setWarmUp(true);
				int warmUpMinutes = Integer
						.parseInt(PreferenceManager
								.getDefaultSharedPreferences(this)
								.getString(
										this.getString(R.string.key_warm_up_time),
										"3"));
				actions.add(new WorkoutActionWarmUp(warmUpMinutes));
			}

			actions.addAll(workout.getActions());

			workoutCopy.setActions(actions);
		}
	}

	private void addListeners() {
		buttonPause.setOnClickListener(this);
		buttonResume.setOnClickListener(this);
		buttonStop.setOnClickListener(this);
		imageButtonGoToScreen1.setOnClickListener(this);
		field1.container.setOnClickListener(this);
		field2.container.setOnClickListener(this);
		field3.container.setOnClickListener(this);
		field4.container.setOnClickListener(this);
		addOnStateChangedListener();
	}

	private void initBigDataField(BigDataField bigDataField, View parent) {
		bigDataField.textView = (TextView) parent
				.findViewById(R.id.textViewNoMapField1);
		bigDataField.textViewDescription = (TextView) parent
				.findViewById(R.id.textViewNoMapField1Discription);
		bigDataField.textViewUnit = (TextView) parent
				.findViewById(R.id.textViewNoMapField1Unit);
		bigDataField.container = (RelativeLayout) parent
				.findViewById(R.id.relativeLayoutNoMapField1);
	}

	// container for data field
	private class BigDataField {
		protected TextView textViewDescription;
		protected TextView textView;
		protected TextView textViewUnit;
		protected RelativeLayout container;
		protected int contentValue;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonNoMapPause:
			break;
		case R.id.buttonNoMapResume:
			break;
		case R.id.buttonNoMapStop:
			showAlertDialog(isServiceConnected,api,this,distance,time,runNumber);
			break;
		case R.id.imageButtonGoToActivityActivity:
			start1Screen();
			
			break;
		default:
			if (v == field1.container) {

			} else if (v == field2.container) {

			} else if (v == field3.container) {

			} else if (v == field4.container) {

			}
			break;
		}

	}

	private void addOnStateChangedListener() {
		ActityButtonStateChangeListener stateChangedListener = new ActityButtonStateChangeListener(
				this);
		buttonPause.setOnTouchListener(stateChangedListener);
		buttonResume.setOnTouchListener(stateChangedListener);
		buttonStop.setOnTouchListener(stateChangedListener);
		imageButtonGoToScreen1.setOnTouchListener(stateChangedListener);
		field1.container.setOnTouchListener(stateChangedListener);
		field2.container.setOnTouchListener(stateChangedListener);
		field3.container.setOnTouchListener(stateChangedListener);
		field4.container.setOnTouchListener(stateChangedListener);

	}

	private void initDisplayedData() {
		// TODO remember user data
		field1.contentValue = distanceID;
		field2.contentValue = timeID;
		field3.contentValue = paceID;
		field4.contentValue = avgPaceID;
		initLabels(field1.textView, field1.textViewDescription, distanceID);
		initLabels(field2.textView, field2.textViewDescription, timeID);
		initLabels(field3.textView, field3.textViewDescription, paceID);
		initLabels(field4.textView, field4.textViewDescription, avgPaceID);

	}

	private void prepareServiceAndStart() {
		doBindService();

		handlerForService = new Handler();
	}

	private void doBindService() {

		Log.i("Service_info", "ActivityNoMapActivity Binding");
		Intent intent = new Intent(LocationService.class.getName());
		// intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		// intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		bindService(intent, serviceConnection, 0);
		mIsBound = true;
	}

	private void doUnbindService() {
		Log.i("Service_info", "Activity Unbinding");
		if (mIsBound) {
			try {
				api.removeListener(runListener);
				unbindService(serviceConnection);
			} catch (RemoteException e) {

				e.printStackTrace();
			}

			mIsBound = false;

		}
	}

	@Override
	protected void onDestroy() {
		doUnbindService();
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == ActivityActivity.MY_REQUEST_CODE) {
			
			if (isServiceConnected && resultCode == RESULT_OK) {
				try {
					String name = data.getStringExtra(ActivityActivity.NAME_TAG);
					api.doSaveRun(data.getBooleanExtra(ActivityActivity.SAVE_TAG, false), name);
				}
				catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (isServiceConnected) {
				try {
					api.doSaveRun(false, "");
				}
				catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			setResult(RESULT_OK);	//dont know why its needed, but it is...
			finish();
			overridePendingTransition(R.anim.in_up_anim, R.anim.out_up_anim);
			
		}
	}
	
	
	private final ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "Service connection established");
			isServiceConnected = true;
			// that's how we get the client side of the IPC connection
			api = RunListenerApi.Stub.asInterface(service);
			try {
				api.addListener(runListener);

				int countDownTime = Integer
						.parseInt(PreferenceManager
								.getDefaultSharedPreferences(
										ActivityNoMapActivity.this)
								.getString(
										getString(R.string.key_countdown_before_start),
										"0"));

				api.setStarted(workoutCopy, countDownTime); // -,-' must be here
															// because service
															// has different
															// preference
															// context, so when
															// user changes it
															// in setting it
															// doesn't work okay
			} catch (RemoteException e) {
				Log.e(TAG, "Failed to add listener", e);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isServiceConnected = false;
			Log.i(TAG, "Service connection closed");
		}

	};

	private final RunListener.Stub runListener = new RunListener.Stub() {

		@Override
		public void handleLocationUpdate() throws RemoteException {
			Location location = api.getLatestLocation();
			lastDistance = distance;
			distance = api.getDistance();
			updateGpsInfo(location);
		}

		@Override
		public void handleConnectionResult() throws RemoteException {
		}

		@Override
		public void handleTimeChange() throws RemoteException {
			handleTimeUpdates();
		}

		@Override
		public void handleWorkoutChange(Workout workout, boolean firtTime)
				throws RemoteException {
			Log.i(TAG,
					"workout data: " + workout.isWarmUp() + " "
							+ workout.getCurrentAction() + " " + firtTime);

			handleWorkoutUpdate(workout);
		}

		@Override
		public void handleCountDownChange(int countDownNumber)
				throws RemoteException {
			handleCountDownUpdate(countDownNumber);
		}

		@Override
		public void handleLostGPS() throws RemoteException {
			if (!progressDialogDisplayed) {
				showLostGpsSignalDialog();
			}
			handlerForService.post(new Runnable() {

				@Override
				public void run() {
					gpsDisplayer.updateStrengthSignal(Double.MAX_VALUE);
				}
			});

		}

	};

	@Override
	protected void updateGpsInfo(final Location newLocation) {
		autoPauseIfEnabled(newLocation);
		handlerForService.post(new Runnable() {

			@Override
			public void run() {
				if (newLocation.getAccuracy() < LocationService.REQUIRED_ACCURACY) {
					// not first point after start or resume

					if (lostGPSDialog != null) {
						lostGPSDialog.dismiss();
						lostGPSDialog = null;
						progressDialogDisplayed = false;
					}
					if (!isPaused) {
						if (mLastLocation != null) {
							countData(newLocation, mLastLocation);
						}

						updateData(field1.textView, field1.contentValue,
								distance, pace, time, avgPace);
						updateData(field2.textView, field2.contentValue,
								distance, pace, time, avgPace);
						updateData(field3.textView, field3.contentValue,
								distance, pace, time, avgPace);
						updateData(field4.textView, field4.contentValue,
								distance, pace, time, avgPace);
					}
				} else if (newLocation.getAccuracy() >= LocationService.REQUIRED_ACCURACY) {
					// prevents displaying two progress Dialogs at once
					if (!progressDialogDisplayed) {
						showLostGpsSignalDialog();
					}
				}
				mPreLastLocation = mLastLocation;
				mLastLocation = newLocation;
			}
		});

	}

	@Override
	protected void pauseRun() {
		handlerForService.post(new Runnable() {
			@Override
			public void run() {
				if (!isPaused
						&& (!(workout != null && workout.isWarmUp() && workout
								.getCurrentAction() == 0)))// ||
															// pausedManually))
				{
					if (actualPaceCalculator != null) {
						actualPaceCalculator.reset();
					}
					isPaused = true;
					startStopLayout.setVisibility(View.INVISIBLE);
					buttonResume.setVisibility(View.VISIBLE);
					try {
						if (isServiceConnected) {
							api.setPaused();
						}
					} catch (RemoteException e) {
						Log.e(TAG, "Failed to tell that activity is paused", e);
					}
				} else if (!isPaused && pausedManually) {
					Toast.makeText(ActivityNoMapActivity.this,
							getResources().getString(R.string.no_pause),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	protected void resumeRun() {
		handlerForService.post(new Runnable() {
			@Override
			public void run() {
				if (isPaused) {
					isPaused = false;
					pausedManually = false;
					startStopLayout.setVisibility(View.VISIBLE);
					buttonResume.setVisibility(View.GONE);
					try {
						if (isServiceConnected) {
							api.setResumed();
						}
					} catch (RemoteException e) {
						Log.e(TAG, "Failed to tell that activity is resumed", e);
					}
				}
			}
		});
	}

	@Override
	protected void handleTimeUpdates() {
		try {
			time = api.getTime();
			updateViewsAfterTimeChange();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void updateViewsAfterTimeChange() {
		handlerForService.post(new Runnable() {

			@Override
			public void run() {
				updateData(field1.textView, field1.contentValue, distance,
						pace, time, avgPace);
				updateData(field2.textView, field2.contentValue, distance,
						pace, time, avgPace);
				updateData(field3.textView, field3.contentValue, distance,
						pace, time, avgPace);
				updateData(field4.textView, field4.contentValue, distance,
						pace, time, avgPace);
			}
		});
	}

	private void autoPauseIfEnabled(Location newLocation) {
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				getString(R.string.key_auto_pause), false)) {
			if (newLocation.hasSpeed()) {
				Log.e(TAG, "Speed:" + newLocation.getSpeed());
				if (newLocation.getSpeed() < MIN_SPEED_FOR_AUTO_PAUSE) {
					pauseRun();
				} else if (!pausedManually) {
					resumeRun();
				}
			} else {
				Log.e(TAG, "No speed.. pausing anyway");
				pauseRun();
			}
		}
	}

	private void handleWorkoutUpdate(final Workout newWorkout) {
		handlerForService.post(new Runnable() {

			@Override
			public void run() {
				ActivityNoMapActivity.this.workoutCopy
						.updateWorkoutData(newWorkout);
				// TODO show workout change
			}
		});
	}

	private void showLostGpsSignalDialog() {
		handlerForService.post(new Runnable() {
			@Override
			public void run() {
				if (isServiceConnected) {

					progressDialogDisplayed = true;

					lostGPSDialog = ProgressDialog
							.show(ActivityNoMapActivity.this,
									getResources()
											.getString(
													R.string.dialog_message_on_lost_gpsp),
									null);
					lostGPSDialog.setCancelable(true);

				}
			}
		});
	}

	private void handleCountDownUpdate(final int countDownNumber) {
		textViewCountDown.setVisibility(View.VISIBLE);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (countDownNumber == -1) {
					textViewCountDown.setVisibility(View.GONE);
					reset();
					// startRecording();
				} else if (countDownNumber == 0) {
					textViewCountDown.setText(ActivityNoMapActivity.this
							.getString(R.string.go));
				} else {
					textViewCountDown.setText(countDownNumber + "");
				}
			}
		});
	}

	private void reset() {
		distance = 0;

		time = 0L;
	}

	// count everything with 2 last location points
	private void countData(final Location location, final Location lastLocation) {

		Log.i("ActivityActivity", "countData: " + location);
		handlerForService.post(new Runnable() {
			@Override
			public void run() {

				gpsDisplayer.updateStrengthSignal(location.getAccuracy());

				if (actualPaceCalculator == null) {
					actualPaceCalculator = new ActualPaceCalculator();
				}
				pace = actualPaceCalculator.addPoint(location);// (double) 1 /
																// (speed * 60 /
																// 1000);

				synchronized (time) {
					avgPace = ((double) time / 60) / distance;
				}
			}
		});
	}

	private void start1Screen() {
		Intent i = new Intent(ActivityNoMapActivity.this,
				ActivityActivity.class);
		if (workout!=null)
			i.putExtra(Workout.TAG, workout);
		i.putExtra(ActivityActivity.RUN_NUMBER_TAG, runNumber);
		finish();
		startActivity(i);
		overridePendingTransition(R.anim.in_right_anim, R.anim.out_right_anim);

	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		showAlertDialog(isServiceConnected,api,this,distance,time,runNumber);
	}
}
