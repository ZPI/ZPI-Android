package com.pwr.zpi.database.entity;

public final class WorkoutActionSimple extends WorkoutAction {

	private int speedType;
	private int valueType;
	private double value;

	public WorkoutActionSimple(int speedType, int valueType, double value) {
		super(WorkoutAction.ACTION_SIMPLE);
		this.speedType = speedType;
		this.valueType = valueType;
		this.value = value;
	}
	
	/**
	 * @return one of WorkoutAction constants ACTION_SIMPLE_SPEED_FAST,
	 * ACTION_SIMPLE_SPEED_STEADY, ACTION_SIMPLE_SPEED_SLOW
	 */
	public int getSpeedType() {
		return speedType;
	}

	/**
	 * @param one of WorkoutAction constants ACTION_SIMPLE_SPEED_FAST,
	 * ACTION_SIMPLE_SPEED_STEADY, ACTION_SIMPLE_SPEED_SLOW
	 */
	public void setSpeedType(int speedType) {
		this.speedType = speedType;
	}

	/**
	 * @return one of WorkoutAction constants ACTION_SIMPLE_VALUE_TYPE_TIME,
	 * ACTION_SIMPLE_VALUE_TYPE_DISTANCE
	 */
	public int getValueType() {
		return valueType;
	}

	/**
	 * @param one of WorkoutAction constants ACTION_SIMPLE_VALUE_TYPE_TIME,
	 * ACTION_SIMPLE_VALUE_TYPE_DISTANCE
	 */
	public void setValueType(int valueType) {
		this.valueType = valueType;
	}

	/**
	 * @return time or distance check valueType 
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value - time or distance as it's set in valueType
	 */
	public void setValue(double value) {
		this.value = value;
	}

}
