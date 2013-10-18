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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
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
	private LocationManager service;
	private int gpsStatus = -1;

	// TODO potem zmieniê
	// public static MyLocationListener locationListener;

	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;

	public static final short GPS_NOT_ENABLED = 0;
	public static final short NO_GPS_SIGNAL = 1;
	public static final short GPS_WORKING = 2;

	private static final short LEFT = 0;
	private static final short RIGHT = 1;
	private static final short UP = 2;
	private static final short DOWN = 3;

	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	public boolean isConnected;
	private Location mLastLocation;
	// service data
	Messenger mService = null;
	boolean mIsBound;
	//final Messenger mMessenger = new Messenger(new IncomingHandler());

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

		// locationListener = new MyLocationListener(this);

		prepareGestureListener();
		addListeners();

		// locationListener.getmLocationClient().connect();

		isConnected = false;
		
		doBindService();
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mMyServiceReceiver,
		          new IntentFilter(MyLocationListener.class.getSimpleName()));
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

		askForGpsStatus();
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	super.onWindowFocusChanged(hasFocus);
	askForGpsStatus();
	 }

	@Override
	protected void onPause() {
		super.onPause();
		
		askForGpsStatus();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureListener.onTouch(null, event);
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
		startActivityIfPossible();

	}

	@Override
	public void onDownToUpSwipe() {
		startActivity(SettingsActivity.class, UP);

	}

	@Override
	public void onClick(View v) {

		if (v == GPSStatusTextView) {
			// if gps is not running
			if (gpsStatus == GPS_NOT_ENABLED) {
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		} else if (v == startButton) {
			startActivityIfPossible();

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

	// TODO wywolac
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

	public void showGPSAccuracy(double accuracy) {
		GPSSignalTextView.setText(String.format("%s: %.2fm", getResources()
				.getString(R.string.gps_accuracy), accuracy));
	}

	@Override
	protected void onStop() {
		//LocalBroadcastManager.getInstance(this).unregisterReceiver(mMyServiceReceiver);
		super.onStop();
	}

	@Override
	protected void onStart() {

		super.onStart();
	}

	@Override
	protected void onDestroy() {
		doUnbindService();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMyServiceReceiver);
		super.onDestroy();
	}

	private void startActivityIfPossible() {
		if (gpsStatus == -1) {
			// Shouldn't be here;
			Log.e("Service_info", "no gps status info");
		} else if (gpsStatus == GPS_NOT_ENABLED) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Add the buttons
			builder.setTitle(R.string.dialog_message_no_gps);
			builder.setPositiveButton(android.R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent intent = new Intent(
									android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(intent);
						}
					});
			builder.setNegativeButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
						}
					});
			// Set other dialog properties

			// Create the AlertDialog
			AlertDialog dialog = builder.create();
			dialog.show();
		} else if (gpsStatus == NO_GPS_SIGNAL) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Add the buttons
			builder.setTitle(R.string.dialog_message_low_gps_accuracy);
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

						}
					});
			// Set other dialog properties

			// Create the AlertDialog
			AlertDialog dialog = builder.create();
			dialog.show();
		} else
			startActivity(ActivityActivity.class, DOWN);
	}

	// SERVICE METHODS
	private void askForGpsStatus() {
		try {
			Message msg = Message.obtain(null,
					MyLocationListener.MSG_ASK_FOR_GPS);
			if (mService != null)
				mService.send(msg);
		} catch (RemoteException e) {

		}

	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i("Service_info", "Main Screen Connected");
			mService = new Messenger(service);
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			Log.i("Service_info", "Main Screen Disconnected");
			mService = null;
		}
	};

	void doBindService() {
		Log.i("Service_info", "Main Screen Binding");
		bindService(new Intent(MainScreenActivity.this,
				MyLocationListener.class), mConnection,
				Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		Log.i("Service_info", "Main Screen Unbinding");
		if (mIsBound) {
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;

		}
	}

	// handler for the events launched by the service
	private BroadcastReceiver mMyServiceReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	Log.i("Service_info", "onReceive");
	    	
	    	// Extract data included in the Intent
	        int message_type = intent.getIntExtra(MyLocationListener.MESSAGE,-1);
	        
	        switch (message_type) {
			case MyLocationListener.MSG_SEND_LOCATION:
				Log.i("Service_info", "got Location");
				isConnected = true;
				mLastLocation = (Location) intent.getParcelableExtra("Location");
				showGPSAccuracy(mLastLocation.getAccuracy());
				//if (mLastLocation.getAccuracy() < MyLocationListener.REQUIRED_ACCURACY)
				askForGpsStatus();
				//	gpsStatus = GPS_WORKING;
				
				break;
			case MyLocationListener.MSG_ASK_FOR_GPS:
				gpsStatus = intent.getIntExtra("gpsStatus", GPS_NOT_ENABLED);
				Log.i("Service_info", "gotGpsStatus: "+gpsStatus);
				switch (gpsStatus) {
				case GPS_NOT_ENABLED:
					GPSStatusTextView.setText(getResources().getString(
							R.string.gps_disabled));
					break;
				case NO_GPS_SIGNAL:
					GPSStatusTextView.setText(getResources().getString(
							R.string.gps_enabled));
					break;
				case GPS_WORKING:
					GPSStatusTextView.setText(getResources().getString(
							R.string.gps_enabled));
					break;
				}

				break;
			}
	        
	        // Do stuff...
	    }
	};
	
	
}
