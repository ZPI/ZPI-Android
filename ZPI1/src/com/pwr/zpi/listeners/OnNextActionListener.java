package com.pwr.zpi.listeners;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.pwr.zpi.R;
import com.pwr.zpi.ZPIApplication;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.database.entity.WorkoutActionAdvanced;
import com.pwr.zpi.database.entity.WorkoutActionSimple;
import com.pwr.zpi.utils.SpeechSynthezator;

public class OnNextActionListener implements IOnNextActionListener {
	
	private static final String TAG = OnNextActionListener.class.getSimpleName();
	
	private Context context;
	
	public OnNextActionListener(Parcel in) {}
	public OnNextActionListener() {}
	
	@Override
	public void setConext(Context context) {
		this.context = context;
	}
	
	@Override
	public void onNextActionSimple(WorkoutActionSimple simple) {
		Log.i(TAG, "simple action");
		SpeechSynthezator ss = ((ZPIApplication) context.getApplicationContext()).getSyntezator();
		Log.i(TAG, "context: " + context + " syntezator: " + ss);
		if (context != null && ss != null) {
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
			//TODO			builder.append(String.format("//TODO%", args)simple.getValue())
			// time - how to say - 6 hours 5 minutes or 6,5 hours
			
			Log.i(TAG, "almost said");
			ss.say(builder.toString());
		}
	}
	
	@Override
	public void onNextActionAdvanced(WorkoutActionAdvanced advanced) {
		Log.i(TAG, "advanced action");
		SpeechSynthezator ss = ((ZPIApplication) context.getApplicationContext()).getSyntezator();
		Log.i(TAG, "context: " + context + " syntezator: " + ss);
		if (context != null && ss != null) {
			StringBuilder builder = new StringBuilder();
			builder.append(context.getString(R.string.action));
			builder.append(" ");
			builder.append(context.getString(R.string.chase_virtual_partner));
			
			Log.i(TAG, "almost said");
			ss.say(builder.toString());
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
	
}
