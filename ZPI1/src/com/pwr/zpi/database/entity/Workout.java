package com.pwr.zpi.database.entity;

import java.util.List;

import com.pwr.zpi.listeners.IOnNextActionListener;
import com.pwr.zpi.utils.TimeFormatter;

public class Workout {
	public final static String TAG = "Workout";
	public static final String LIST_TAG = "List";
	private long ID;
	private String name;
	private List<WorkoutAction> actions;
	private boolean isWarmUp;
	private int repeatCount;
	private IOnNextActionListener onNextActionListener;
	
	// progressing workout fields
	private int currentAction;
	private double lastActionDistance;
	private long lastActionTime;
	// distance to virtual partner or time or distance for current action
	private double howMuchLeft;
	
	public Workout() {
		this.currentAction = 0;
		this.lastActionDistance = 0;
		this.lastActionTime = 0;
		this.howMuchLeft = 0;
		this.ID = -1;
	}
	
	public long getID() {
		return ID;
	}
	
	public void setID(long iD) {
		ID = iD;
	}
	
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
		initHowMuchLeft(actions, 0, 0, 0);
	}
	
	private void initHowMuchLeft(List<WorkoutAction> actions, int position, double deltaDistance, double deltaTime) {
		if (actions != null && actions.size() != position) {
			WorkoutAction action = actions.get(position);
			switch (action.getActionType()) {
				case WorkoutAction.ACTION_SIMPLE:
					howMuchLeft = ((WorkoutActionSimple) action).getValue();
					switch (((WorkoutActionSimple) action).getValueType()) {
						case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE:
							howMuchLeft -= deltaDistance;
							break;
						case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME:
							howMuchLeft -= deltaTime;
							break;
						default:
							break;
					}
					break;
				case WorkoutAction.ACTION_ADVANCED:
					howMuchLeft = ((WorkoutActionAdvanced) action).getDistance();
					howMuchLeft -= deltaDistance;
					break;
				default:
					break;
			}
			
		}
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
	
	// progressing actions methods
	public void progressWorkout(double distance, Long time) {
		double currentDistance = distance - lastActionDistance;
		long currentTime = time - lastActionTime;
		
		WorkoutAction action = getActions().get(currentAction);
		
		switch (action.getActionType()) {
			case WorkoutAction.ACTION_SIMPLE:
				WorkoutActionSimple simple = (WorkoutActionSimple) action;
				progressSimpleAction(simple, currentDistance, currentTime);
				break;
			case WorkoutAction.ACTION_ADVANCED:
				WorkoutActionAdvanced advanced = (WorkoutActionAdvanced) action;
				progressAdvancedAction(advanced, currentDistance, currentTime);
				break;
			default:
				break;
		}
	}
	
	private void progressSimpleAction(WorkoutActionSimple simple, double currentDistance, long currentTime) {
		boolean actionDone = false;
		double deltaDistance = 0;
		double deltaTime = 0;
		switch (simple.getValueType()) {
			case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE:
				double actionDistanceToCover = simple.getValue();
				actionDone = currentDistance >= actionDistanceToCover;
				if (actionDone) {
					deltaDistance = currentDistance - actionDistanceToCover;
				}
				else {
					this.howMuchLeft = actionDistanceToCover - currentDistance;
				}
				break;
			case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME:
				double actionTimeToCover = simple.getValue();
				actionDone = currentTime >= actionTimeToCover;
				if (actionDone) {
					deltaTime = currentTime - actionTimeToCover;
				}
				else {
					this.howMuchLeft = actionTimeToCover - currentTime;
				}
				break;
			default:
				break;
		}
		if (actionDone) {
			currentAction++;
			lastActionDistance += currentDistance - deltaDistance;
			lastActionTime += currentTime - deltaTime;
			initHowMuchLeft(getActions(), currentAction, deltaDistance, deltaTime);
			if (hasOnNextActionListener()) {
				WorkoutAction action = getActions().get(currentAction);
				notifyListeners(action);
			}
		}
	}
	
	private boolean hasOnNextActionListener() {
		return onNextActionListener != null;
	}
	
	private void progressAdvancedAction(WorkoutActionAdvanced advanced, double currentDistance, long currentTime) {
		double virtualPartnerVelocity = advanced.getDistance() / advanced.getTime(); // km / milisecond
		double virtualPartnerDistance = virtualPartnerVelocity * currentTime;
		double distanceBetweenUserAndVirutalPartner = currentDistance - virtualPartnerDistance;
		
		if (virtualPartnerDistance >= advanced.getDistance()) {
			double deltaDistance = virtualPartnerDistance - advanced.getDistance();;
			
			currentAction++;
			lastActionDistance += currentDistance - deltaDistance;
			lastActionTime += currentTime;
			initHowMuchLeft(getActions(), currentAction, deltaDistance, 0);
			if (hasNextAction() && hasOnNextActionListener()) {
				WorkoutAction action = getActions().get(currentAction);
				notifyListeners(action);
			}
		}
		else {
			this.howMuchLeft = distanceBetweenUserAndVirutalPartner;
		}
	}
	
	private void notifyListeners(WorkoutAction action) {
		switch (action.getActionType()) {
			case WorkoutAction.ACTION_SIMPLE:
				onNextActionListener.onNextActionSimple((WorkoutActionSimple) action);
				break;
			case WorkoutAction.ACTION_ADVANCED:
				onNextActionListener.onNextActionAdvanced((WorkoutActionAdvanced) action);
				break;
			default:
				break;
		}
	}
	
	public int getCurrentAction() {
		return currentAction;
	}
	
	public double getHowMuchLeft() {
		return howMuchLeft;
	}
	
	public CharSequence getHowMuchLeftCurrentActionStringWithUnits() {
		WorkoutAction currentAction = getActions().get(getCurrentAction());
		return formatActionValue(currentAction, getHowMuchLeft());
	}
	
	public static CharSequence formatActionValue(WorkoutAction action, Double value) {
		StringBuilder sb = new StringBuilder();
		boolean getValue = value == null;
		if (action.getActionType() == WorkoutAction.ACTION_ADVANCED) {
			if (getValue) {
				value = ((WorkoutActionAdvanced) action).getDistance();
			}
			sb.append(String.format("%.0f", value));
			sb.append("m");
		}
		else {
			WorkoutActionSimple simple = (WorkoutActionSimple) action;
			if (getValue) {
				value = simple.getValue();
			}
			if (simple.getValueType() == WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE) {
				sb.append(String.format("%.0f", value));
				sb.append("m");
			}
			else {
				sb.append(TimeFormatter.formatTimeHHMMSS(value.longValue()));
				sb.append("h");
			}
		}
		return sb.toString();
	}
	
	public boolean hasNextAction() {
		return currentAction < getActions().size();
	}
	
	public IOnNextActionListener getOnNextActionListener() {
		return onNextActionListener;
	}
	
	public void setOnNextActionListener(IOnNextActionListener onNextActionListener) {
		this.onNextActionListener = onNextActionListener;
	}
}
