package com.pwr.zpi.database.entity;

import java.util.Date;
import java.util.LinkedList;

import com.pwr.zpi.utils.Pair;

public class TreningPlan {
	//TODO
	
	private LinkedList<Pair<Workout, Date>> workouts;
	private String name;
	
	public LinkedList<Pair<Workout, Date>> getWorkouts() {
		return workouts;
	}
	
	public void setWorkouts(LinkedList<Pair<Workout, Date>> workouts) {
		this.workouts = workouts;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
