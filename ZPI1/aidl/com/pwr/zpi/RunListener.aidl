package com.pwr.zpi;

import com.pwr.zpi.database.entity.Workout;
 
interface RunListener {
 
  	void handleLocationUpdate();
	void handleConnectionResult();
	void handleTimeChange();
	void handleWorkoutChange(in Workout workout);
	void handleCountDownChange(int countDownNumber);
}