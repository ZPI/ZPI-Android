package com.pwr.zpi.database.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class WorkoutActionWarmUp extends WorkoutAction implements Parcelable  {
	
	private int workoutTime;
	
	/**
	 * @param workoutTime in minutes
	 */
	public WorkoutActionWarmUp(int workoutTime) {
		super(WorkoutAction.ACTION_WARM_UP);
		this.workoutTime = workoutTime;
	}
	
	public WorkoutActionWarmUp(Parcel in) {
		super(in);
		readFromParcel(in);
	}
	
	private void readFromParcel(Parcel in) {
		workoutTime = in.readInt();
	}
	
	@Override
	public int describeContents() {return 0;}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		super.writeToParcel(out, flags);
		out.writeInt(workoutTime);
	}
	
	public int getWorkoutTime() {
		return workoutTime;
	}
	
	public static final Parcelable.Creator<WorkoutActionWarmUp> CREATOR = new Parcelable.Creator<WorkoutActionWarmUp>() {
		@Override
		public WorkoutActionWarmUp createFromParcel(Parcel in) {
			return new WorkoutActionWarmUp(in);
		}
		
		@Override
		public WorkoutActionWarmUp[] newArray(int size) {
			return new WorkoutActionWarmUp[size];
		}
	};
	
	@Override
	public boolean isWarmUp() {
		return true;
	}
}
