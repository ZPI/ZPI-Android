package com.pwr.zpi.database.entity;

import java.util.List;

public class Workout {

	private String name;
	private List<WorkoutAction> actions;
	private boolean isWarmUp;
	private int repeatCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<WorkoutAction> getActions() {
		return actions;
	}

	public void setActions(List<WorkoutAction> actions) {
		this.actions = actions;
	}

	public boolean isWarmUp() {
		return isWarmUp;
	}

	public void setWarmUp(boolean isWarmUp) {
		this.isWarmUp = isWarmUp;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}
}
