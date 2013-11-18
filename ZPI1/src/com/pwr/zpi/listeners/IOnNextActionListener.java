package com.pwr.zpi.listeners;

import android.content.Context;
import android.os.Parcelable;

import com.pwr.zpi.database.entity.WorkoutActionAdvanced;
import com.pwr.zpi.database.entity.WorkoutActionSimple;
import com.pwr.zpi.database.entity.WorkoutActionWarmUp;
import com.pwr.zpi.utils.SpeechSynthezator;

public interface IOnNextActionListener extends Parcelable {
	
	public void setConext(Context context);
	
	public void onNextActionSimple(WorkoutActionSimple simple);
	
	public void onNextActionAdvanced(WorkoutActionAdvanced advanced);
	
	public void setSyntezator(SpeechSynthezator speechSynthezator);
	
	public void onNextActionWarmUP(WorkoutActionWarmUp action);
}
