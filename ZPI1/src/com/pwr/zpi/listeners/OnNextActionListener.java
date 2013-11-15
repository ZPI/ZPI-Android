package com.pwr.zpi.listeners;

import android.content.Context;

import com.pwr.zpi.R;
import com.pwr.zpi.ZPIApplication;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.database.entity.WorkoutActionAdvanced;
import com.pwr.zpi.database.entity.WorkoutActionSimple;
import com.pwr.zpi.utils.SpeechSynthezator;

public class OnNextActionListener implements IOnNextActionListener {
	
	Context context;
	
	public OnNextActionListener(Context context) {
		this.context = context;
	}
	
	@Override
	public void onNextActionSimple(WorkoutActionSimple simple) {
		if (SpeechSynthezator.hasSyntezator()) {
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
			SpeechSynthezator ss = ((ZPIApplication) context.getApplicationContext()).getSyntezator();
			ss.say(builder.toString());
		}
	}
	
	@Override
	public void onNextActionAdvanced(WorkoutActionAdvanced advanced) {
		if (SpeechSynthezator.hasSyntezator()) {
			StringBuilder builder = new StringBuilder();
			builder.append(context.getString(R.string.action));
			builder.append(" ");
			builder.append(context.getString(R.string.chase_virtual_partner));
			
			SpeechSynthezator ss = ((ZPIApplication) context.getApplicationContext()).getSyntezator();
			ss.say(builder.toString());
		}
	}
	
}
