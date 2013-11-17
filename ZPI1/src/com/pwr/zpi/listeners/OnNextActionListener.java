package com.pwr.zpi.listeners;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.pwr.zpi.R;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.database.entity.WorkoutActionAdvanced;
import com.pwr.zpi.database.entity.WorkoutActionSimple;
import com.pwr.zpi.database.entity.WorkoutActionWarmUp;
import com.pwr.zpi.utils.SpeechSynthezator;
import com.pwr.zpi.utils.TimeFormatter;

public class OnNextActionListener implements IOnNextActionListener {
	
	private static final String TAG = OnNextActionListener.class.getSimpleName();
	
	private Context context;
	private SpeechSynthezator speechSynthezator;
	
	public OnNextActionListener(Parcel in) {}
	public OnNextActionListener() {}
	
	@Override
	public void setConext(Context context) {
		this.context = context;
	}
	
	@Override
	public void onNextActionSimple(WorkoutActionSimple simple) {
		Log.i(TAG, "simple action");
		if (context != null && speechSynthezator != null) {
			StringBuilder builder = new StringBuilder();
			builder.append(context.getString(R.string.action));
			builder.append(" ");
			switch (simple.getSpeedType()) {
				case WorkoutAction.ACTION_SIMPLE_SPEED_FAST:
					builder.append(context.getString(R.string.run_fast));
					break;
				case WorkoutAction.ACTION_SIMPLE_SPEED_STEADY:
					builder.append(context.getString(R.string.run_steady));
					break;
				case WorkoutAction.ACTION_SIMPLE_SPEED_SLOW:
					builder.append(context.getString(R.string.run_slow));
					break;
				default:
					break;
			}
			builder.append(" ");
			builder.append(context.getString(R.string.for_how_much));
			builder.append(" ");
			switch (simple.getValueType()) {
				case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME:
					builder.append(toMinutes(simple.getValue()));
					builder.append(context.getString(R.string.minutes_speak));
					break;
				case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE:
					builder.append(simple.getValue());
					builder.append(context.getString(R.string.meters));
					break;
				default:
					break;
			}
			
			Log.i(TAG, "almost said");
			speechSynthezator.say(builder.toString());
		}
	}
	
	private String toMinutes(double value) {
		long minutes = (long) (value / 1000);
		minutes /= 60;
		return minutes + "";
	}
	@Override
	public void onNextActionAdvanced(WorkoutActionAdvanced advanced) {
		Log.i(TAG, "advanced action");
		if (context != null && speechSynthezator != null) {
			StringBuilder builder = new StringBuilder();
			builder.append(context.getString(R.string.action));
			builder.append(" ");
			builder.append(context.getString(R.string.chase_virtual_partner));
			builder.append(" ");
			builder.append(context.getString(R.string.with_pace));
			builder.append(" ");
			String pace = TimeFormatter.formatTimeMMSSorHHMMSS(advanced.getPace());
			String[] splitedPace = pace.split(":");
			int minutes = 0;
			int seconds = 0;
			if (splitedPace.length == 3) {
				minutes = Integer.valueOf(splitedPace[0]) * 60 + Integer.valueOf(splitedPace[1]);
				seconds = Integer.valueOf(splitedPace[2]);
			} else {
				minutes = Integer.valueOf(splitedPace[0]);
				seconds = Integer.valueOf(splitedPace[1]);
			}
			builder.append(minutes);
			builder.append(context.getString(R.string.minutes_speak));
			builder.append(" ");
			builder.append(seconds);
			builder.append(context.getString(R.string.seconds_speak));
			builder.append(" ");
			builder.append(context.getString(R.string.over_kilometer));
			
			Log.i(TAG, "almost said");
			speechSynthezator.say(builder.toString());
		}
	}
	
	@Override
	public void onNextActionWarmUP(WorkoutActionWarmUp action) {
		Log.i(TAG, "warmu up listener");
		if (context != null && speechSynthezator != null) {
			StringBuilder builder = new StringBuilder();
			
			builder.append(context.getString(R.string.warm_up_speak));
			builder.append(" ");
			builder.append(context.getString(R.string.for_how_much));
			builder.append(" ");
			builder.append(action.getWorkoutTime());
			builder.append(" ");
			builder.append(context.getString(R.string.minutes_speak));
			
			Log.i(TAG, "warmu up almost said");
			speechSynthezator.say(builder.toString());
		}
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {}
	
	public static final Parcelable.Creator<OnNextActionListener> CREATOR = new Parcelable.Creator<OnNextActionListener>() {
		@Override
		public OnNextActionListener createFromParcel(Parcel in) {
			return new OnNextActionListener(in);
		}
		
		@Override
		public OnNextActionListener[] newArray(int size) {
			return new OnNextActionListener[size];
		}
	};
	
	@Override
	public void setSyntezator(SpeechSynthezator speechSynthezator) {
		this.speechSynthezator = speechSynthezator;
	}
	
}
