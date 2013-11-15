package com.pwr.zpi.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.pwr.zpi.ActivityActivity;
import com.pwr.zpi.MainScreenActivity;
import com.pwr.zpi.R;
import com.pwr.zpi.RunListener;
import com.pwr.zpi.RunListenerApi;
import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.utils.Notifications;
import com.pwr.zpi.utils.Pair;
import com.pwr.zpi.utils.SpeechSynthezator;

public class LocationService extends Service implements LocationListener, ConnectionCallbacks,
OnConnectionFailedListener {
	
	private static final String TAG = LocationService.class.getSimpleName();
	
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	
	public static final int STARTED = 1;
	public static final int PAUSED = 2;
	public static final int STOPED = 3;
	private int state;
	private static final long LOCATION_UPDATE_FREQUENCY = 1000;
	private static final long MAX_UPDATE_TIME = 5000;
	public static final int REQUIRED_ACCURACY = 3000;
	private LinkedList<LinkedList<Pair<Location, Long>>> traceWithTime;
	private final List<RunListener> listeners = new ArrayList<RunListener>();
	private ConnectionResult connectionResult;
	private Location latestLocation;
	private ArrayList<Location> locationList;
	private SingleRun singleRun;
	private Calendar calendar;
	// time counting fields
	private Handler handler;
	private Runnable timeHandler;
	public static final String CONNECTION_FIAILED_TAG = "connectionFailed";
	
	private SpeechSynthezator speechSynthezator;
	
	long startTime;
	long pauseStartTime;
	long pauseTime;
	//saved here in case of activity closing
	Long time;
	double distance;
	Workout workout;
	private boolean connectionFailed;
	private boolean isConnected;
	private final RunListenerApi.Stub apiEndpoint = new RunListenerApi.Stub() {
		
		@Override
		public List<Location> getWholeRun() throws RemoteException {
			return locationList;
		}
		
		@Override
		public double getDistance() throws RemoteException {
			return distance;
		}
		
		@Override
		public long getTime() throws RemoteException {
			synchronized (time) {
				return time;
			}
		}
		
		@Override
		public Location getLatestLocation() throws RemoteException {
			return latestLocation;
		}
		
		@Override
		public Intent getConnectionResult() throws RemoteException {
			Intent intent = new Intent(LocationService.class.getSimpleName());
			intent.putExtra(CONNECTION_FIAILED_TAG, connectionFailed);
			if (connectionFailed)
			{
				int statusCode = connectionResult.getErrorCode();
				PendingIntent pendingIntent = connectionResult.getResolution();
				// Add data
				intent.putExtra("pending_intent", connectionResult.getResolution());
				intent.putExtra("status_code", statusCode);
			}
			return intent;
		}
		
		@Override
		public void setStarted(Workout workout) throws RemoteException {
			if (state == STOPED)
			{
				locationList = new ArrayList<Location>();
				state = STARTED;
				prepareWorkout(workout);
				initActivityRecording();
				Notification note = Notifications.createNotification(LocationService.this, ActivityActivity.class,
					R.string.app_name, R.string.notification_message);
				startForeground(1, note);
			}
			
		}
		
		@Override
		public void setPaused() throws RemoteException {
			pauseStartTime = System.currentTimeMillis();
			state = PAUSED;
			handler.removeCallbacks(timeHandler);
		}
		
		@Override
		public void setResumed() throws RemoteException {
			pauseTime += System.currentTimeMillis() - pauseStartTime;
			state = STARTED;
			traceWithTime.add(new LinkedList<Pair<Location, Long>>());
			handler.post(timeHandler);
		}
		
		@Override
		public void setStoped() throws RemoteException {
			state = STOPED;
			saveRun();
			distance = 0;
			pauseTime = 0;
			locationList = null;
			stopForeground(true);
			
		}
		
		@Override
		public void addListener(RunListener listener) throws RemoteException {
			synchronized (listeners) {
				listeners.add(listener);
			}
		}
		
		@Override
		public void removeListener(RunListener listener) throws RemoteException {
			
			synchronized (listeners) {
				listeners.clear();
			}
			//we dont need more then one listener at once
		}
		
		@Override
		public int getGPSStatus() throws RemoteException {
			
			return checkGPS();
		}
		
		@Override
		public void prepareTextToSpeech() throws RemoteException {
			speechSynthezator = new SpeechSynthezator(getApplicationContext());
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		if (LocationService.class.getName().equals(intent.getAction())) {
			Log.d(TAG, "Bound by intent " + intent);
			return apiEndpoint;
		}
		else return null;
	}
	
	private void prepareWorkout(Workout workout) {
		this.workout = workout;
		if (workout != null) {
			workout.getOnNextActionListener().setConext(getApplicationContext());
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		state = STOPED;
		pauseTime = 0;
		time = new Long(0);
		isConnected = false;
		connectionFailed = false;
		handler = new Handler();
		mLocationClient = new LocationClient(getApplicationContext(), this,
			this);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setInterval(LOCATION_UPDATE_FREQUENCY);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationClient.connect();
		Log.i(TAG, "Service creating");
		Toast.makeText(getApplicationContext(), "Service creating", Toast.LENGTH_LONG).show(); //TODO remove
	}
	
	@Override
	public void onDestroy() {
		
		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
		}
		
		mLocationClient.disconnect();
		Log.i(TAG, "Service destroying");
		super.onDestroy();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		Log.i(TAG, "new Location state: " + state);
		
		if (state == STARTED) {
			locationList.add(location);
			updateTraceWithTime(location);
			if (latestLocation != null) {
				distance += location.distanceTo(latestLocation);
			}
		}
		latestLocation = location;
		synchronized (listeners) {
			Iterator<RunListener> it = listeners.iterator();
			while (it.hasNext())
			{
				RunListener listener = it.next();
				try {
					listener.handleLocationUpdate();
				}
				catch (RemoteException e) {
					Log.w(TAG, "Failed to tell listener" + listener + " about update ", e);
					it.remove();
				}
			}
		}
		
	}
	
	private void updateTraceWithTime(Location newLocation)
	{
		if (newLocation.getAccuracy() < REQUIRED_ACCURACY) {
			// not first point after start or resume
			
			if (traceWithTime.isEmpty()) {
				traceWithTime.add(new LinkedList<Pair<Location, Long>>());
			}
			synchronized (time) {
				traceWithTime.getLast().add(new Pair<Location, Long>(newLocation, time));
			}
		}
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
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.i(TAG, "onConnectionFailed LocationListener telling " + listeners.size() + " listeners");
		this.connectionResult = connectionResult;
		connectionFailed = true;
		for (RunListener listener : listeners)
		{
			try {
				listener.handleConnectionResult();
			}
			catch (RemoteException e) {
				Log.w(TAG, "Failed to tell listener about connaction failed ", e);
			}
		}
	}
	
	private int checkGPS() {
		
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		//service.addGpsStatusListener(this);
		short gpsStatus = 0;
		
		boolean enabled = service
			.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if (!enabled) {
			gpsStatus = MainScreenActivity.GPS_NOT_ENABLED;
		}
		else if (isConnected
			&& (latestLocation == null || latestLocation
			.getAccuracy() > REQUIRED_ACCURACY)) {
			gpsStatus = MainScreenActivity.NO_GPS_SIGNAL;
		}
		else {
			gpsStatus = MainScreenActivity.GPS_WORKING;
		}
		
		return gpsStatus;
	}
	
	private void initActivityRecording()
	{
		singleRun = new SingleRun();
		calendar = Calendar.getInstance();
		singleRun.setStartDate(calendar.getTime());
		traceWithTime = new LinkedList<LinkedList<Pair<Location, Long>>>();
		
		startCountingTime();
	}
	
	private void startCountingTime()
	{
		startTime = System.currentTimeMillis();
		prepareTimeCountingHandler();
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
		if (state == STARTED)
		{
			synchronized (time) {
				time = System.currentTimeMillis() - startTime - pauseTime;
				boolean changeWorkout = false;
				if (workout != null) {
					processWorkout();
					changeWorkout = true;
				}
				Log.i(TAG, time + "");
				Iterator<RunListener> it = listeners.iterator();
				while (it.hasNext())
				{
					RunListener listener = it.next();
					try {
						listener.handleTimeChange();
						if (changeWorkout) {
							listener.handleWorkoutChange(workout);
						}
						Log.i(TAG, listeners.size() + "");
					}
					catch (RemoteException e) {
						Log.w(TAG, "Failed to tell listener about new Time ", e);
						//		it.remove();
						
					}
				}
				
			}
			handler.postDelayed(timeHandler, 1000);
		}
		
	}
	
	private void processWorkout() {
		if (workout.hasNextAction()) {
			workout.getOnNextActionListener().setSyntezator(speechSynthezator);
			workout.progressWorkout(distance, time);
		}
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
}
