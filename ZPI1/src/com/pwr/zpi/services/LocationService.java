package com.pwr.zpi.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.pwr.zpi.ActivityActivity;
import com.pwr.zpi.MainScreenActivity;
import com.pwr.zpi.R;
import com.pwr.zpi.RunListener;
import com.pwr.zpi.RunListenerApi;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.database.entity.WorkoutActionWarmUp;
import com.pwr.zpi.listeners.ICountDownListner;
import com.pwr.zpi.services.location.RunInfo;
import com.pwr.zpi.services.location.RunInfo.State;
import com.pwr.zpi.utils.AssetsPlayer;
import com.pwr.zpi.utils.AssetsPlayer.AssetsMp3Files;
import com.pwr.zpi.utils.CounterRunnable;
import com.pwr.zpi.utils.LocationAPI;
import com.pwr.zpi.utils.LocationAPI.ILocationCallback;
import com.pwr.zpi.utils.Notifications;
import com.pwr.zpi.utils.SpeechSynthezator;

public class LocationService extends Service implements ICountDownListner, ILocationCallback {
	
	private static final String TAG = LocationService.class.getSimpleName();
	
	public static final String CONNECTION_FIAILED_TAG = "connectionFailed";
	public static final int COUNTER_COUNT_DOWN = 0x1;
	public static final int COUNTER_WARM_UP = 0x2;
	public static final int REQUIRED_ACCURACY = 40;
	public static final long MAX_UPDATE_TIME = 5000;
	
	private final List<RunListener> listeners = new ArrayList<RunListener>();
	private boolean isFirstTime;
	private Location latestLocation;
	
	// time counting fields
	private Handler handler;
	private Runnable timeHandler;
	
	private SpeechSynthezator speechSynthezator;
	private AssetsPlayer soundsPlayer;
	private int countDownTime;
	
	Workout workout;
	private RunInfo info;
	private LocationAPI locationAPI;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		info = new RunInfo(getApplicationContext());
		handler = new Handler();
		locationAPI = new LocationAPI(getApplicationContext(), this, true);
		
		Log.i(TAG, "Service creating");
		soundsPlayer = new AssetsPlayer(getApplicationContext(), AssetsMp3Files.Beep);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		if (LocationService.class.getName().equals(intent.getAction())) {
			Log.d(TAG, "Bound by intent " + intent);
			return apiEndpoint;
		}
		else return null;
	}
	
	@Override
	public void onDestroy() {
		if (soundsPlayer != null) {
			soundsPlayer.stopPlayer();
		}
		locationAPI.onDestroy();
		
		Log.i(TAG, "Service destroying");
		super.onDestroy();
	}
	
	private final RunListenerApi.Stub apiEndpoint = new RunListenerApi.Stub() {
		
		@Override
		public List<Location> getWholeRun() throws RemoteException {
			return info.getLocationList();
		}
		
		@Override
		public double getDistance() throws RemoteException {
			return info.getDistance();
		}
		
		@Override
		public long getTime() throws RemoteException {
			synchronized (info.getTime()) {
				return info.getTime();
			}
		}
		
		@Override
		public Location getLatestLocation() throws RemoteException {
			return latestLocation;
		}
		
		@Override
		public Intent getConnectionResult() throws RemoteException {
			Intent intent = new Intent(LocationService.class.getSimpleName());
			intent.putExtra(CONNECTION_FIAILED_TAG, locationAPI.isConnectionFailed());
			if (locationAPI.isConnectionFailed()) {
				ConnectionResult connectionResult = locationAPI.getConnectionResult();
				int statusCode = connectionResult.getErrorCode();
				PendingIntent pendingIntent = connectionResult.getResolution();
				// Add data
				intent.putExtra("pending_intent", connectionResult.getResolution());
				intent.putExtra("status_code", statusCode);
			}
			return intent;
		}
		
		@Override
		public void setStarted(Workout workout, int countDownTime) throws RemoteException {
			if (info.isStateStoped()) {
				handler.post(zeroFieldsHandler);
				info.setStarted();
				
				LocationService.this.countDownTime = countDownTime;
				prepareWorkout(workout);
				initActivityRecording();
				Notification note = Notifications.createNotification(LocationService.this, ActivityActivity.class,
					R.string.app_name, R.string.notification_message);
				startForeground(1, note);
			}
			
		}
		
		@Override
		public void setPaused() throws RemoteException {
			info.setPauseStartTime(System.currentTimeMillis());
			info.setState(State.PAUSED);
		}
		
		@Override
		public void setResumed() throws RemoteException {
			if (info.isStatePaused()) {
				info.setResumedAfterPause();
			}
		}
		
		@Override
		public void setStoped() throws RemoteException {
			info.setState(State.STOPED);
			handler.removeCallbacksAndMessages(null);
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
		
		@Override
		public void doSaveRun(boolean save, String name) throws RemoteException {
			if (save) {
				info.saveRun(name);
			}
			info.zeroFieldsAfterSave();
			stopForeground(true);
		}
		
		@Override
		public void onSoundSettingChange(boolean enabled) throws RemoteException {
			if (speechSynthezator != null) {
				
				speechSynthezator.setSpeakingEnabled(enabled);
				soundsPlayer.setCanPlay(enabled);
			}
		}
		
	};
	
	private void zeroFields() {
		
		info.zeroFields();
		isFirstTime = true;
	}
	
	private void prepareWorkout(Workout workout) {
		this.workout = workout;
		if (workout != null) {
			workout.getOnNextActionListener().setConext(getApplicationContext());
		}
	}
	
	private void updateTraceWithTime(Location newLocation) {
		if (newLocation.getAccuracy() < REQUIRED_ACCURACY) {
			info.updateTraceWithTime(newLocation);
		}
	}
	
	private int checkGPS() {
		
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		//service.addGpsStatusListener(this);
		short gpsStatus = MainScreenActivity.NO_GPS_SIGNAL_INFO;
		
		boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if (!enabled) {
			gpsStatus = MainScreenActivity.GPS_NOT_ENABLED;
		}
		else if (locationAPI.isGPSLost()
			|| (locationAPI.isConnected() && (latestLocation == null || latestLocation.getAccuracy() > REQUIRED_ACCURACY))) {
			gpsStatus = MainScreenActivity.NO_GPS_SIGNAL;
		}
		else {
			gpsStatus = MainScreenActivity.GPS_WORKING;
		}
		
		return gpsStatus;
	}
	
	private void initActivityRecording() {
		
		info.initBeforeRecording();
		
		if (workout != null && !workout.getActions().isEmpty()) {
			workout.getOnNextActionListener().setSyntezator(speechSynthezator);
			workout.notifyListeners(workout.getActions().get(0));
		}
		
		startTimeEvaluation();
	}
	
	private void startTimeEvaluation() {
		Log.i(TAG, "starting time evaluation");
		prepareTimeCountingHandler();
		if (workout != null && workout.isWarmUp()) {
			Log.i(TAG, "warm up count down");
			startWarmUp();
		}
		else {
			Log.i(TAG, "count down without warm up");
			startRunAfterCountDown();
		}
	}
	
	Runnable startTimeSetHandler = new Runnable() {
		@Override
		public void run() {
			info.setStartTime(System.currentTimeMillis());
		}
	};
	
	Runnable zeroFieldsHandler = new Runnable() {
		
		@Override
		public void run() {
			zeroFields();
		}
	};
	
	private void startWarmUp() {
		handler.post(startTimeSetHandler);
		int minutes = ((WorkoutActionWarmUp) workout.getActions().get(0)).getWorkoutTime();
		int seconds = minutes * 60;
		handler.post(new CounterRunnable(COUNTER_WARM_UP, seconds, this));
	}
	
	private void startRunAfterCountDown() {
		handler.post(zeroFieldsHandler);
		soundsPlayer.changePlayer(AssetsMp3Files.Beep);
		Log.i(TAG, "count down start " + countDownTime + " handler " + handler);
		handler.post(new CounterRunnable(COUNTER_COUNT_DOWN, countDownTime, this));
	}
	
	@Override
	public void onCountDownUpadte(int counterID, int howMuchLeft) {
		Log.i(TAG, "Count down update " + howMuchLeft);
		switch (counterID) {
			case COUNTER_COUNT_DOWN:
				listenersHandleCountDownChange(howMuchLeft);
				Log.i(TAG, "beep !");
				soundsPlayer.play();
				break;
			case COUNTER_WARM_UP:
				processWorkout();
				Iterator<RunListener> it = listeners.iterator();
				info.updateTime();
				while (it.hasNext()) {
					RunListener listener = it.next();
					try {
						listener.handleTimeChange();
						listener.handleWorkoutChange(workout, isFirstTime);
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								isFirstTime = false;
							}
						});
						Log.i(TAG, listeners.size() + "");
					}
					catch (RemoteException e) {
						Log.w(TAG, "Failed to tell listener about workout update", e);
					}
				}
				break;
			default:
				break;
		}
		handler.postDelayed(new CounterRunnable(counterID, howMuchLeft - 1, this), 1000);
	}
	
	@Override
	public void onCountDownDone(int counterID, int howMuchLeft) {
		switch (counterID) {
			case COUNTER_COUNT_DOWN:
				soundsPlayer.stopPlayer();
				soundsPlayer.changePlayer(AssetsMp3Files.Go);
				listenersHandleCountDownChange(howMuchLeft);
				soundsPlayer.play();
				break;
			case COUNTER_WARM_UP:
				info.initBeforeRecording();
				
				info.setDistance(0);
				info.setPauseTime(0);
				info.setLocationList(new ArrayList<Location>());
				
				info.setTime(System.currentTimeMillis());
				
				startRunAfterCountDown();
				workout.setWarmUpDone();
				break;
			default:
				break;
		}
		Log.i(TAG, "Count down done");
		handler.postDelayed(new CounterRunnable(counterID, howMuchLeft - 1, this), 1000);
	}
	
	@Override
	public void onCountDownPostAction(int counterID, int howMuchLeft) {
		switch (counterID) {
			case COUNTER_COUNT_DOWN:
				soundsPlayer.stopPlayer();
				listenersHandleCountDownChange(howMuchLeft);
				startCountingTime();
				break;
			default:
				break;
		}
	}
	
	private void listenersHandleCountDownChange(int howMuchLeft) {
		Iterator<RunListener> it = listeners.iterator();
		while (it.hasNext()) {
			RunListener listener = it.next();
			try {
				listener.handleCountDownChange(howMuchLeft);
			}
			catch (RemoteException e) {
				Log.w(TAG, "Failed to tell listener about count down update ", e);
			}
		}
	}
	
	private void startCountingTime() {
		handler.post(zeroFieldsHandler);
		handler.post(startTimeSetHandler);
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
		if (info.isStateStarted() && info.getStartTime() != 0) {
			synchronized (info.getTime()) {
				info.updateTime();
				boolean changeWorkout = false;
				
				if (workout != null) {
					processWorkout();
					changeWorkout = true;
				}
				Log.i(TAG, info.getTime() + "");
				Iterator<RunListener> it = listeners.iterator();
				while (it.hasNext()) {
					RunListener listener = it.next();
					try {
						listener.handleTimeChange();
						if (changeWorkout) {
							listener.handleWorkoutChange(workout, isFirstTime);
						}
						Log.i(TAG, listeners.size() + "");
					}
					catch (RemoteException e) {
						Log.w(TAG, "Failed to tell listener about new Time ", e);
					}
				}
			}
		}
		handler.postDelayed(timeHandler, 1000);
	}
	
	private void processWorkout() {
		if (workout.hasNextAction()) {
			workout.getOnNextActionListener().setSyntezator(speechSynthezator);
			workout.progressWorkout(info.getDistance(), info.getTime());
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		
		if (info.isStateStarted()) {
			info.addToLocationList(location);
			updateTraceWithTime(location);
			if (latestLocation != null) {
				info.addDistance(location.distanceTo(latestLocation));
			}
		}
		latestLocation = location;
		synchronized (listeners) {
			Iterator<RunListener> it = listeners.iterator();
			while (it.hasNext()) {
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
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.i(TAG, "onConnectionFailed LocationListener telling " + listeners.size() + " listeners");
		for (RunListener listener : listeners) {
			try {
				listener.handleConnectionResult();
			}
			catch (RemoteException e) {
				Log.w(TAG, "Failed to tell listener about connaction failed ", e);
			}
		}
	}
	
	@Override
	public void onLostGPSSignal() {
		Log.i("debug1", "gpsLost");
		for (RunListener listener : listeners) {
			try {
				listener.handleLostGPS();
			}
			catch (RemoteException e) {
				Log.w(TAG, "Failed to tell listener" + listener + " about update ", e);
			}
		}
	}
	
}
