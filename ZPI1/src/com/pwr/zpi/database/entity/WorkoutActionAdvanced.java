package com.pwr.zpi.database.entity;

public final class WorkoutActionAdvanced extends WorkoutAction {
	
	/**
	 * one of WorkoutAction constants ACTION_ADVANCED_TYPE_TIME_DISTANCE,
	 * ACTION_ADVANCED_TYPE_DISTANCE_PACE, ACTION_ADVANCED_TYPE_PACE_TIME
	 */
	private int type;
	private double distance;
	private double pace;
	private long time;
	
	private WorkoutActionAdvanced() {
		super(WorkoutAction.ACTION_ADVANCED);
	}
	
	public WorkoutActionAdvanced(int type, double distance, double pace, long time) {
		this();
		this.type = type;
		this.distance = distance;
		this.pace = pace;
		this.time = time;
	}
	
	/**
	 * distance pace constructor
	 * 
	 * @param distance
	 * @param pace
	 */
	public WorkoutActionAdvanced(double distance, double pace) {
		this(WorkoutAction.ACTION_ADVANCED_TYPE_DISTANCE_PACE, distance, pace, 0);
	}
	
	/**
	 * time distance constructor
	 * 
	 * @param time
	 * @param distance
	 */
	public WorkoutActionAdvanced(long time, double distance) {
		this(WorkoutAction.ACTION_ADVANCED_TYPE_TIME_DISTANCE, distance, 0, time);
	}
	
	/**
	 * pace time constructor
	 * 
	 * @param pace
	 * @param time
	 */
	public WorkoutActionAdvanced(double pace, long time) {
		this(WorkoutAction.ACTION_ADVANCED_TYPE_PACE_TIME, 0, pace, time);
	}
	
	/**
	 * @return distance in km
	 */
	public double getDistance() {
		if (type == WorkoutAction.ACTION_ADVANCED_TYPE_PACE_TIME && pace != 0) {
			distance = time / (pace * 60 * 1000);
		}
		return distance;
	}
	
	/**
	 * @param distance in km
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	/**
	 * @return pace in min / km
	 */
	public double getPace() {
		if (type == WorkoutAction.ACTION_ADVANCED_TYPE_TIME_DISTANCE && distance != 0) {
			pace = time / 1000 / 60 / distance;
		}
		return pace;
	}
	
	/**
	 * @param pace in min / km
	 */
	public void setPace(double pace) {
		this.pace = pace;
	}
	
	/**
	 * @return time in miliseconds
	 */
	public long getTime() {
		if (type == WorkoutAction.ACTION_ADVANCED_TYPE_DISTANCE_PACE) {
			time = (long) (pace * distance * 60 * 1000);
		}
		return time;
	}
	
	/**
	 * @param time in miliseconds
	 */
	public void setTime(long time) {
		this.time = time;
	}
	
	/**
	 * one of WorkoutAction constants ACTION_ADVANCED_TYPE_TIME_DISTANCE,
	 * ACTION_ADVANCED_TYPE_DISTANCE_PACE, ACTION_ADVANCED_TYPE_PACE_TIME
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * one of WorkoutAction constants ACTION_ADVANCED_TYPE_TIME_DISTANCE,
	 * ACTION_ADVANCED_TYPE_DISTANCE_PACE, ACTION_ADVANCED_TYPE_PACE_TIME
	 */
	public void setType(int type) {
		this.type = type;
	}
}
