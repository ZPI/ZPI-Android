package com.pwr.zpi.services.location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.location.Location;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.pwr.zpi.RunListener;
import com.pwr.zpi.listeners.ICountDownListner;
import com.pwr.zpi.utils.AssetsPlayer;
import com.pwr.zpi.utils.AssetsPlayer.AssetsMp3Files;
import com.pwr.zpi.utils.CounterRunnable;

public class TimeHandlers implements ICountDownListner {
	
	private static final String TAG = TimeHandlers.class.getSimpleName();
	public static final int COUNTER_COUNT_DOWN = 0x1;
	public static final int COUNTER_WARM_UP = 0x2;
	
	private final List<RunListener> listeners;
	private final Handler handler;
	private final RunInfo info;
	private final Runnables runnables;
	private final AssetsPlayer soundsPlayer;
	private final ICountDownCallback callback;
	
	private int countDownTime;
	
	public TimeHandlers(ICountDownCallback callback, RunInfo info, List<RunListener> listeners,
		AssetsPlayer soundsPlayer) {
		this.info = info;
		this.listeners = listeners;
		this.soundsPlayer = soundsPlayer;
		this.callback = callback;
		handler = new Handler();
		runnables = new Runnables();
	}
	
	public void stopAll() {
		handler.removeCallbacksAndMessages(null);
	}
	
	public void setCountDownTime(int countDownTime2) {
		this.countDownTime = countDownTime2;
	}
	
	public void zeroFields() {
		handler.post(runnables.zeroFieldsHandler);
	}
	
	public void startTimeEvaluation() {
		Log.i(TAG, "starting time evaluation");
		if (info.isWorkoutWarmUp()) {
			Log.i(TAG, "warm up count down");
			startWarmUp();
		}
		else {
			Log.i(TAG, "count down without warm up");
			startRunAfterCountDown();
		}
	}
	
	private void runTimerTask() {
		if (info.isStateStarted() && info.getStartTime() != 0) {
			synchronized (info.getTime()) {
				info.updateTime();
				boolean changeWorkout = info.progresWorkoutIfExists();
				
				Log.i(TAG, info.getTime() + "");
				Iterator<RunListener> it = listeners.iterator();
				while (it.hasNext()) {
					RunListener listener = it.next();
					try {
						listener.handleTimeChange();
						if (changeWorkout) {
							listener.handleWorkoutChange(info.getWorkout(), info.isFirstTime());
						}
						Log.i(TAG, listeners.size() + "");
					}
					catch (RemoteException e) {
						Log.w(TAG, "Failed to tell listener about new Time ", e);
					}
				}
			}
		}
		handler.postDelayed(runnables.timeHandler, 1000);
	}
	
	private void startCountingTime() {
		handler.post(runnables.zeroFieldsHandler);
		handler.post(runnables.startTimeSetHandler);
		handler.post(runnables.timeHandler);
	}
	
	private void startWarmUp() {
		handler.post(runnables.startTimeSetHandler);
		int minutes = info.getWorkoutWarmUpMinutes();
		int seconds = minutes * 60;
		handler.post(new CounterRunnable(COUNTER_WARM_UP, seconds, this));
	}
	
	private void startRunAfterCountDown() {
		handler.post(runnables.zeroFieldsHandler);
		soundsPlayer.changePlayer(AssetsMp3Files.Beep);
		Log.i(TAG, "count down start " + countDownTime + " handler " + handler);
		handler.post(new CounterRunnable(COUNTER_COUNT_DOWN, countDownTime, this));
	}
	
	@Override
	public void onCountDownUpadte(int counterID, int howMuchLeft) {
		Log.i(TAG, "Count down update " + howMuchLeft);
		switch (counterID) {
			case COUNTER_COUNT_DOWN:
				callback.listenersHandleCountDownChange(howMuchLeft);
				Log.i(TAG, "beep !");
				soundsPlayer.play();
				break;
			case COUNTER_WARM_UP:
				if (!info.isStatePaused())
				{
					info.processWorkout();
					Iterator<RunListener> it = listeners.iterator();
					info.updateTime();
					while (it.hasNext()) {
						RunListener listener = it.next();
						try {
							listener.handleTimeChange();
							listener.handleWorkoutChange(info.getWorkout(), info.isFirstTime());
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									info.setIsFirstTime(false);
								}
							});
							Log.i(TAG, listeners.size() + "");
						}
						catch (RemoteException e) {
							Log.w(TAG, "Failed to tell listener about workout update", e);
						}
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
				callback.listenersHandleCountDownChange(howMuchLeft);
				soundsPlayer.play();
				break;
			case COUNTER_WARM_UP:
				info.initBeforeRecording();
				
				info.setDistance(0);
				info.setPauseTime(0);
				info.setLocationList(new ArrayList<Location>());
				
				info.setTime(System.currentTimeMillis());
				
				startRunAfterCountDown();
				info.setWorkoutWarmUpDone();
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
				callback.listenersHandleCountDownChange(howMuchLeft);
				startCountingTime();
				break;
			default:
				break;
		}
	}
	
	public interface ICountDownCallback {
		public void listenersHandleCountDownChange(int howMuchLeft);
	}
	
	private class Runnables {
		public Runnable timeHandler = new Runnable() {
			@Override
			public void run() {
				runTimerTask();
			}
		};
		
		public Runnable startTimeSetHandler = new Runnable() {
			@Override
			public void run() {
				info.setStartTime(System.currentTimeMillis());
			}
		};
		
		public Runnable zeroFieldsHandler = new Runnable() {
			@Override
			public void run() {
				info.zeroFields();
			}
		};
	}
}
