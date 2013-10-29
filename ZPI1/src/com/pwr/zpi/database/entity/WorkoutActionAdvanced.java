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

	/**
	 * distance pace constructor
	 * @param distance
	 * @param pace
	 */
	public WorkoutActionAdvanced(double distance, double pace) {
		this.distance = distance;
		this.pace = pace;
		this.type = WorkoutAction.ACTION_ADVANCED_TYPE_DISTANCE_PACE;
	}
	/**
	 * time distance constructor
	 * @param time
	 * @param distance
	 */
	public WorkoutActionAdvanced(long time, double distance) {
		this.distance = distance;
		this.time = time;
		this.type = WorkoutAction.ACTION_ADVANCED_TYPE_TIME_DISTANCE;
	}
	
	/**
	 * pace time constructor
	 * @param pace
	 * @param time
	 */
	public WorkoutActionAdvanced(double pace, long time) {
		this.time = time;
		this.pace = pace;
		this.type = WorkoutAction.ACTION_ADVANCED_TYPE_PACE_TIME;
	}
	
	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getPace() {
		return pace;
	}

	public void setPace(double pace) {
		this.pace = pace;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
