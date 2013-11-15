package com.pwr.zpi.database.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * it's just a generalization class
 */
public abstract class WorkoutAction implements Comparable<WorkoutAction>, Parcelable {
	
	public static final String TAG = "WorkoutAction";
	
	public static final int ACTION_SIMPLE = 0x1;
	public static final int ACTION_ADVANCED = 0x2;
	
	public static final int ACTION_SIMPLE_SPEED_FAST = 0x1;
	public static final int ACTION_SIMPLE_SPEED_STEADY = 0x2;
	public static final int ACTION_SIMPLE_SPEED_SLOW = 0x3;
	
	public static final int ACTION_SIMPLE_VALUE_TYPE_TIME = 0x1;
	public static final int ACTION_SIMPLE_VALUE_TYPE_DISTANCE = 0x2;
	
	public static final int ACTION_ADVANCED_TYPE_TIME_DISTANCE = 0x1;
	public static final int ACTION_ADVANCED_TYPE_DISTANCE_PACE = 0x2;
	public static final int ACTION_ADVANCED_TYPE_PACE_TIME = 0x3;
	
	/**
	 * one of ACTION_SIMPLE, ACTION_ADVANCED
	 */
	private long ID;
	private int actionType;
	private int orderNumber;
	
	/**
	 * @param actionType one of ACTION_SIMPLE, ACTION_ADVANCED
	 */
	public WorkoutAction(int actionType) {
		this.actionType = actionType;
		this.ID = -1;
	}
	
	public WorkoutAction(Parcel in)
	{
		readFromfarcel(in);
	}
	
	public long getID() {
		return ID;
	}
	
	public void setID(long iD) {
		ID = iD;
	}
	
	public int getActionType() {
		return actionType;
	}
	
	public void setActionType(int actionType) {
		this.actionType = actionType;
	}
	
	public int getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@Override
	public int compareTo(WorkoutAction another) {
		return getOrderNumber() - another.getOrderNumber();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(ID);
		out.writeInt(actionType);
		out.writeInt(orderNumber);
		
	}
	
	private void readFromfarcel(Parcel in)
	{
		ID = in.readLong();
		actionType = in.readInt();
		orderNumber = in.readInt();
	}
	
	public boolean isAdvanced()
	{
		return false;
	}
	
	public boolean isSimple()
	{
		return false;
	}
}
