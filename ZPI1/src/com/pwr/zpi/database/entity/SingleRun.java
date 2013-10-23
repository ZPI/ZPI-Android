package com.pwr.zpi.database.entity;

import java.util.Date;
import java.util.LinkedList;

import com.pwr.zpi.utils.Pair;

import android.location.Location;

public class SingleRun {

	private long runID;
	private LinkedList<Integer> runSubIDs; // when there are two or more subsequent runs ex o--(run)--o ..(space).. o--(run)--o
	private Date startDate;
	private Date endDate;
	
	private LinkedList<LinkedList<Pair<Location, Long>>> traceWithTime;
	private double distance;
	private long runTime;

	public SingleRun()
	{
		
	}
	//for testing history list
	public SingleRun(Date startDate,double distance, long runTime)
	{
		this.startDate = startDate;
		this.distance = distance;
		this.runTime = runTime;
	}
	
	public long getRunID() {
		return runID;
	}

	public void setRunID(long runID) {
		this.runID = runID;
	}

	public LinkedList<Integer> getRunSubIDs() {
		return runSubIDs;
	}

	public void setRunSubIDs(LinkedList<Integer> runSubIDs) {
		this.runSubIDs = runSubIDs;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public LinkedList<LinkedList<Pair<Location, Long>>> getTraceWithTime() {
		return traceWithTime;
	}

	public void setTraceWithTime(
			LinkedList<LinkedList<Pair<Location, Long>>> traceWithTime) {
		this.traceWithTime = traceWithTime;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public long getRunTime() {
		return runTime;
	}

	public void setRunTime(long runTime) {
		this.runTime = runTime;
	}
	
}
