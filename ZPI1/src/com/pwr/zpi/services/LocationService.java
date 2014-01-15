package com.pwr.zpi.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.pwr.zpi.ActivityActivity;
import com.pwr.zpi.R;
import com.pwr.zpi.RunListener;
import com.pwr.zpi.RunListenerApi;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.services.location.RunInfo;
import com.pwr.zpi.services.location.RunInfo.State;
import com.pwr.zpi.services.location.TimeHandlers;
import com.pwr.zpi.services.location.TimeHandlers.ICountDownCallback;
import com.pwr.zpi.utils.AssetsPlayer;
import com.pwr.zpi.utils.AssetsPlayer.AssetsMp3Files;
import com.pwr.zpi.utils.LocationAPI;
import com.pwr.zpi.utils.LocationAPI.ILocationCallback;
import com.pwr.zpi.utils.Notifications;
import com.pwr.zpi.utils.SpeechSynthezator;

public class LocationService extends Service implements ILocationCallback, ICountDownCallback {
	
	private static final String TAG = LocationService.class.getSimpleName();
	
	public static final String CONNECTION_FIAILED_TAG = "connectionFailed";
	public static final int REQUIRED_ACCURACY = 40;
	public static final long MAX_UPDATE_TIME = 5000;
	
	private final List<RunListener> listeners = new ArrayList<RunListener>();
	private Location latestLocation;
	
	private SpeechSynthezator speechSynthezator;
	private AssetsPlayer soundsPlayer;
	
	private RunInfo info;
	private LocationAPI locationAPI;
	private TimeHandlers timeHandlers;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		info = new RunInfo(getApplicationContext(), speechSynthezator);
		locationAPI = new LocationAPI(getApplicationContext(), this, true, REQUIRED_ACCURACY);
		soundsPlayer = new AssetsPlayer(getApplicationContext(), AssetsMp3Files.Beep);
		timeHandlers = new TimeHandlers(this, info, listeners, soundsPlayer);
		
		Log.i(TAG, "Service creating");
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
			return putConnectionResultToIntent();
		}
		
		private Intent putConnectionResultToIntent() {
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
				timeHandlers.zeroFields();
				timeHandlers.setCountDownTime(countDownTime);
				
				info.setStarted();
				info.prepareWorkout(workout);
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
			timeHandlers.stopAll();
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
			return locationAPI.checkGPS().ordinal();
		}
		
		@Override
		public void prepareTextToSpeech() throws RemoteException {
			speechSynthezator = new SpeechSynthezator(getApplicationContext());
			info.setSpeechSynthezator(speechSynthezator);
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
	
	private void updateTraceWithTime(Location newLocation) {
		if (newLocation.getAccuracy() < REQUIRED_ACCURACY) {
			info.updateTraceWithTime(newLocation);
		}
	}
	
	private void initActivityRecording() {
		
		info.initBeforeRecording();
		timeHandlers.startTimeEvaluation();
	}
	
	@Override
	public void listenersHandleCountDownChange(int howMuchLeft) {
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
