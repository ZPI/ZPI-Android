package com.pwr.zpi.listeners;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Dialog;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pwr.zpi.ActivityActivity;
import com.pwr.zpi.MainScreenActivity;
import com.pwr.zpi.R;
import com.pwr.zpi.dialogs.ErrorDialogFragment;

public class MyLocationListener extends Service implements LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		android.location.GpsStatus.Listener {

	// private ActivityActivity activityActivity;
	// private MainScreenActivity mainScreenActivity;

	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	private static final long LOCATION_UPDATE_FREQUENCY = 1000;
	private static final long MAX_UPDATE_TIME = 5000;
	public static final int REQUIRED_ACCURACY = 30; // in meters
	private GoogleMap mMap;
	private boolean isStarted;
	private boolean isPaused;
	private boolean isGpsFix;
	private boolean isConnected;
	private Location mLastRecordedLocation;
	private long mLastRecordedLocationTime;
	//private Location mLastLocation;

	// Service data
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_SEND_LOCATION = 3;
	public static final int MSG_ASK_FOR_GPS = 4;
	public static final String MESSAGE = "Message";
	public static final String MESSAGE_FROM = "Message_from";
	public static final int MESSAGE_FROM_MAIN_SCREEN = 1;
	public static final int MESSAGE_FROM_ACTIVITY = 2;
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	

	private boolean isGpsOk() {
		return (mLastRecordedLocation == null) ? false : mLastRecordedLocation
				.getAccuracy() < REQUIRED_ACCURACY;
		// return true;
		// return isGpsFix &&
		// mLastRecordedLocation.getAccuracy()<REQUIRED_ACCURACY;
	}

	private int checkGPS() {

		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		service.addGpsStatusListener(this);
		short gpsStatus = 0;

		
		boolean enabled = service
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!enabled) 		
			gpsStatus = MainScreenActivity.GPS_NOT_ENABLED;
		else if (isConnected && (mLastRecordedLocation ==null || mLastRecordedLocation.getAccuracy()>MyLocationListener.REQUIRED_ACCURACY))
				
			gpsStatus = MainScreenActivity.NO_GPS_SIGNAL;
		else	
			gpsStatus = MainScreenActivity.GPS_WORKING;
		
		return gpsStatus;
	}
	
	// LOCATION METHODS FOR THE LISTENERS
	@Override
	public void onLocationChanged(Location location) {
		
		sendLocationToClients(location);
		mLastRecordedLocationTime = SystemClock.elapsedRealtime();
		mLastRecordedLocation = location;
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.i("Location_info", "onConnected");
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		isConnected = true;
	}

	@Override
	public void onDisconnected() {
		Log.i("Location_info", "onDisconnected");

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.i("Location_info", "onConnectionFailed");
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		// TODO send message, move everything to MainScreen
		// if (connectionResult.hasResolution()) {
		// try {
		// // Start an Activity that tries to resolve the error
		// connectionResult
		// .startResolutionForResult(
		// mainScreenActivity,
		// MainScreenActivity.CONNECTION_FAILURE_RESOLUTION_REQUEST);
		// /*
		// * Thrown if Google Play services canceled the original
		// * PendingIntent
		// */
		// } catch (IntentSender.SendIntentException e) {
		// // Log the error
		// e.printStackTrace();
		// }
		// } else {
		// /*
		// * If no resolution is available, display a dialog to the user with
		// * the error.
		// */
		// // Get the error dialog from Google Play services
		// Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
		// connectionResult.getErrorCode(), mainScreenActivity,
		// MainScreenActivity.CONNECTION_FAILURE_RESOLUTION_REQUEST);
		//
		// // If Google Play services can provide an error dialog
		// if (errorDialog != null) {
		// // Create a new DialogFragment for the error dialog
		// ErrorDialogFragment errorFragment = new ErrorDialogFragment();
		// // Set the dialog in the DialogFragment
		// errorFragment.setDialog(errorDialog);
		// // Show the error dialog in the DialogFragment
		// errorFragment.show(
		// mainScreenActivity.getSupportFragmentManager(),
		// "Location Updates");
		// }
		// }

	}

	
	@Override
	public void onGpsStatusChanged(int event) {

		switch (event) {
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			if (mLastRecordedLocation != null) {
				isGpsFix = (SystemClock.elapsedRealtime() - mLastRecordedLocationTime) < MAX_UPDATE_TIME;
			}

			break;
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			isGpsFix = true;
			break;
		}

	}

	// SERVICE METHODS

    @Override
    public IBinder onBind(Intent intent) {
    	Log.i("Service_info","onBind");
        return mMessenger.getBinder();
    }

	/**
	 * Handler of incoming messages from clients.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				break;
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
            case MyLocationListener.MSG_ASK_FOR_GPS:
            	Log.i("Service_info","Service: asked for gps signal");
            	int gpsStatus = checkGPS();
            	
        		Intent intent = new Intent(MyLocationListener.class.getSimpleName());
        	    // Add data
        		intent.putExtra(MESSAGE, MSG_ASK_FOR_GPS);
        	    intent.putExtra("gpsStatus", gpsStatus);
        	    LocalBroadcastManager.getInstance(MyLocationListener.this).sendBroadcast(intent);
//            	
//            	for (int i = mClients.size() - 1; i >= 0; i--) {
//        			try {
//        				// TODO Send Location
//
//        				mClients.get(i).send(Message.obtain(null, MyLocationListener.MSG_ASK_FOR_GPS, gpsStatus, 0));
//        				
//        			} catch (RemoteException e) {
//        				// The client is dead. Remove it from the list;
//        				// we are going through the list from back to front
//        				// so this is safe to do inside the loop.
//        				mClients.remove(i);
//        			}
//            	}
            	break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	private void sendLocationToClients(Location location) {
		Log.i("Service_info", "Service is sending location");
		
		Intent intent = new Intent(MyLocationListener.class.getSimpleName());
	    // Add data
		intent.putExtra(MESSAGE, MSG_SEND_LOCATION);
	    intent.putExtra("Location", location);
	    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

	}

	@Override
	public void onCreate() {
		Log.i("Service_info", "Service created");
		mLocationClient = new LocationClient(getApplicationContext(), this, this);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setInterval(LOCATION_UPDATE_FREQUENCY);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		isStarted = false;
		isPaused = false;
		isGpsFix = false;
		isConnected = false;
		mLocationClient.connect();
		super.onCreate();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("Service_info", "Service Unbind");
		 // If the client is connected
		
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		Log.i("Service_info", "Service Unbind");
		
		 if (mLocationClient.isConnected()) {
			 
			 mLocationClient.removeLocationUpdates(this); }
			 
			 mLocationClient.disconnect();
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
}
