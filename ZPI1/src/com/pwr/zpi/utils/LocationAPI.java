package com.pwr.zpi.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationAPI implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener {
	
	private static final String TAG = LocationAPI.class.getSimpleName();
	private static final long LOCATION_UPDATE_FREQUENCY = 1000;
	
	private final IOnLocationChangeCallback callback;
	
	private final LocationClient mLocationClient;
	private final LocationRequest mLocationRequest;
	private boolean isConnected;
	private boolean connectionFailed;
	private ConnectionResult connectionResult;
	
	public LocationAPI(Context context, IOnLocationChangeCallback callback) {
		this.callback = callback;
		isConnected = false;
		connectionFailed = false;
		mLocationClient = new LocationClient(context, this, this);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setInterval(LOCATION_UPDATE_FREQUENCY);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationClient.connect();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		callback.onLocationChanged(location);
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		this.connectionResult = connectionResult;
		connectionFailed = true;
		callback.onConnectionFailed(connectionResult);
	}
	
	@Override
	public void onConnected(Bundle bundle) {
		Log.i(TAG, "onConnected LocationListener");
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		isConnected = true;
	}
	
	@Override
	public void onDisconnected() {
		Log.i(TAG, "onDisconnected LocationListener");
		isConnected = false;
	}
	
	public void onDestroy() {
		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
		}
		mLocationClient.disconnect();
	}
	
	public boolean isConnectionFailed() {
		return connectionFailed;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public ConnectionResult getConnectionResult() {
		return connectionResult;
	}
	
	public interface IOnLocationChangeCallback {
		public void onLocationChanged(Location location);
		
		public void onConnectionFailed(ConnectionResult connectionResult);
	}
}
