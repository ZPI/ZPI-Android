package com.pwr.zpi.listeners;

import java.util.LinkedList;

import android.app.Dialog;
import android.content.IntentSender;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;

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
import com.pwr.zpi.dialogs.ErrorDialogFragment;

public class MyLocationListener implements LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, android.location.GpsStatus.Listener {

	private ActivityActivity activityActivity;
	private MainScreenActivity mainScreenActivity;
	private LinkedList<LinkedList<Location>> trace;
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	private static final long LOCATION_UPDATE_FREQUENCY = 1000;
	private static final long MAX_UPDATE_TIME = 5000;
	private static final int REQUIRED_ACCURACY = 100; //in meters
	private GoogleMap mMap;
	private boolean isStarted;
	private boolean isPaused;
	private boolean isGpsFix;
	private Location mLastRecordedLocation;
	private long mLastRecordedLocationTime;

	public MyLocationListener(MainScreenActivity mainScreenActivity) {
		this.mainScreenActivity = mainScreenActivity;
		mLocationClient = new LocationClient(mainScreenActivity, this, this);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setInterval(LOCATION_UPDATE_FREQUENCY);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		isStarted = false;
		isPaused = false;
		isGpsFix = false;
		
	}



	public ActivityActivity getActivityActivity() {
		return activityActivity;
	}

	public void setActivityActivity(ActivityActivity activityActivity,
			GoogleMap mMap) {
		this.activityActivity = activityActivity;
		this.mMap = mMap;
		Location location = mLocationClient.getLastLocation();
		if (location != null) {
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					location.getLatitude(), location.getLongitude()), 13));
		}
	}

	public MainScreenActivity getMainScreenActivity() {
		return mainScreenActivity;
	}

	public void setMainScreenActivity(MainScreenActivity mainScreenActivity) {
		this.mainScreenActivity = mainScreenActivity;
	}

	public LinkedList<LinkedList<Location>> getTrace() {
		return trace;
	}

	public void setTrace(LinkedList<LinkedList<Location>> trace) {
		this.trace = trace;
	}

	public LocationClient getmLocationClient() {
		return mLocationClient;
	}

	public void setmLocationClient(LocationClient mLocationClient) {
		this.mLocationClient = mLocationClient;
	}

	public LocationRequest getmLocationRequest() {
		return mLocationRequest;
	}

	public void setmLocationRequest(LocationRequest mLocationRequest) {
		this.mLocationRequest = mLocationRequest;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	public void start(ActivityActivity activityActivity) {
		this.activityActivity = activityActivity;
		trace = new LinkedList<LinkedList<Location>>();
		setStarted(true);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (isStarted) {
			if (!isPaused) {
				// TODO upade ONLY when accuracy is good
				
				Location lastLocation;
				if (!trace.isEmpty() && !trace.getLast().isEmpty()) {
					lastLocation = trace.getLast().getLast();

					activityActivity.countData(location, lastLocation);
				}
				if (trace.isEmpty())
					trace.add(new LinkedList<Location>());
				trace.getLast().add(location);
			}
		} else {
			double accuracy = location.getAccuracy();
			mainScreenActivity.showGPSAccuracy(accuracy);
		}
		mLastRecordedLocationTime = SystemClock.elapsedRealtime();
		mLastRecordedLocation = location;
	}

	@Override
	public void onConnected(Bundle bundle) {

		mLocationClient.requestLocationUpdates(mLocationRequest, this);


		Location location = mLocationClient.getLastLocation();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult
						.startResolutionForResult(
								mainScreenActivity,
								MainScreenActivity.CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					connectionResult.getErrorCode(), mainScreenActivity,
					MainScreenActivity.CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(mainScreenActivity.getSupportFragmentManager(),
						"Location Updates");
			}
		}

	}

	public boolean isGpsOk()
	{
		return isGpsFix && mLastRecordedLocation.getAccuracy()<REQUIRED_ACCURACY;
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
	
	
	
}
