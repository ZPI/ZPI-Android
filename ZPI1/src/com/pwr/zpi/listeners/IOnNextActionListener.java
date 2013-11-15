package com.pwr.zpi.listeners;

import com.pwr.zpi.database.entity.WorkoutActionAdvanced;
import com.pwr.zpi.database.entity.WorkoutActionSimple;

public interface IOnNextActionListener {
	
	public void onNextActionSimple(WorkoutActionSimple simple);
	
	public void onNextActionAdvanced(WorkoutActionAdvanced advanced);
}
