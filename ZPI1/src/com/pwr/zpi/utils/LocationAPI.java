package com.pwr.zpi.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.pwr.zpi.services.LocationService;

public class LocationAPI implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener {
	
	private static final String TAG = LocationAPI.class.getSimpleName();
	private static final long LOCATION_UPDATE_FREQUENCY = 1000;
	
	private final ILocationCallback callback;
	private final LocationClient mLocationClient;
	private final LocationRequest mLocationRequest;
	private final Context context;
	private final int requiredAccuracy;
	
	private ConnectionResult connectionResult;
	private Timer lostGPSTimer;
	private Location latestLocation;
	
	private boolean isConnected;
	private boolean connectionFailed;
	private boolean gpsLost;
	private boolean checkGPS = false;
	
	public LocationAPI(Context context, ILocationCallback callback, boolean checkGPS, int requiredAccuracy) {
		this.context = context;
		this.callback = callback;
		this.checkGPS = checkGPS;
		this.requiredAccuracy = requiredAccuracy;
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
		latestLocation = location;
		if (checkGPS) {
			gpsLost = false;
			if (lostGPSTimer != null) {
				lostGPSTimer.cancel();
			}
			lostGPSTimer = new Timer();
			lostGPSTimer.schedule(new CheckForLostGPS(), 0, 1000);
		}
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
	
	public boolean isGPSLost() {
		return gpsLost;
	}
	
	public GPSServiceStatus checkGPS() {
		
		LocationManager service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//service.addGpsStatusListener(this);
		GPSServiceStatus gpsStatus = GPSServiceStatus.NO_GPS_SIGNAL_INFO;
		
		boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if (!enabled) {
			gpsStatus = GPSServiceStatus.GPS_NOT_ENABLED;
		}
		else if (isGPSLost()
			|| (isConnected() && (latestLocation == null || latestLocation.getAccuracy() > requiredAccuracy))) {
			gpsStatus = GPSServiceStatus.NO_GPS_SIGNAL;
		}
		else {
			gpsStatus = GPSServiceStatus.GPS_WORKING;
		}
		
		return gpsStatus;
	}
	
	public interface ILocationCallback {
		public void onLocationChanged(Location location);
		
		public void onConnectionFailed(ConnectionResult connectionResult);
		
		public void onLostGPSSignal();
	}
	
	public enum GPSServiceStatus {
		NO_GPS_SIGNAL_INFO, GPS_NOT_ENABLED, NO_GPS_SIGNAL, GPS_WORKING;
	}
	
	class CheckForLostGPS extends TimerTask {
		
		private long beginTime;
		
		public CheckForLostGPS() {
			beginTime = System.currentTimeMillis();
		}
		
		public void startFromBeginning() {
			beginTime = System.currentTimeMillis();
		}
		
		@Override
		public void run() {
			if (System.currentTimeMillis() - beginTime > LocationService.MAX_UPDATE_TIME) {
				gpsLost = true;
				callback.onLostGPSSignal();
				cancel();
			}
		}
	}
}
