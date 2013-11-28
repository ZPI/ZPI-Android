package com.pwr.zpi.database.entity;

import java.util.HashMap;

public class TreningPlan {
	
	private long ID;
	private HashMap<Integer, Workout> workouts;
	private String name;
	
	public HashMap<Integer, Workout> getWorkouts() {
		return workouts;
	}
	
	public void setWorkouts(HashMap<Integer, Workout> workouts) {
		this.workouts = workouts;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getID() {
		return ID;
	}
	
	public void setID(long id) {
		this.ID = id;
	}
	
}
