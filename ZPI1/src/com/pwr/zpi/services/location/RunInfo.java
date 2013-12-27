package com.pwr.zpi.services.location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.utils.Pair;

public class RunInfo {
	
	private static final String TAG = RunInfo.class.getSimpleName();
	private State state;
	private Long time;
	private double distance;
	private long startTime;
	private long pauseStartTime;
	private long pauseTime;
	private ArrayList<Location> locationList;
	private LinkedList<LinkedList<Pair<Location, Long>>> traceWithTime;
	private SingleRun singleRun;
	private Calendar calendar;
	private Context context;
	
	public RunInfo(Context context) {
		initFields();
	}
	
	private void initFields() {
		state = State.STOPED;
		pauseTime = 0;
		pauseStartTime = System.currentTimeMillis();
		time = 0L;
		startTime = System.currentTimeMillis();
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
	public State getState() {
		return state;
	}
	
	public Long getTime() {
		return time;
	}
	
	public void setTime(Long time) {
		this.time = time;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public void addDistance(double distance) {
		this.distance += distance;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public long getPauseStartTime() {
		return pauseStartTime;
	}
	
	public void setPauseStartTime(long pauseStartTime) {
		this.pauseStartTime = pauseStartTime;
	}
	
	public long getPauseTime() {
		return pauseTime;
	}
	
	public void setPauseTime(long pauseTime) {
		this.pauseTime = pauseTime;
	}
	
	public void setResumedAfterPause() {
		this.pauseTime += System.currentTimeMillis() - pauseStartTime;
		setState(State.STARTED);
		Log.i(TAG, getState() + "");
		traceWithTime.add(new LinkedList<Pair<Location, Long>>());
	}
	
	public void zeroFields() {
		startTime = System.currentTimeMillis();
		pauseStartTime = System.currentTimeMillis();
		pauseTime = 0;
		time = 0L;
		distance = 0;
	}
	
	public boolean isStateStarted() {
		return state == State.STARTED;
	}
	
	public boolean isStatePaused() {
		return state == State.PAUSED;
	}
	
	public boolean isStateStoped() {
		return state == State.STOPED;
	}
	
	public enum State {
		STARTED, PAUSED, STOPED;
	}
	
	public void updateTime() {
		time = System.currentTimeMillis() - startTime - pauseTime;
	}
	
	public void setLocationList(ArrayList<Location> locationList) {
		this.locationList = locationList;
	}
	
	public List<Location> getLocationList() {
		return locationList;
	}
	
	public void setStarted() {
		locationList = new ArrayList<Location>();
		setState(State.STARTED);
	}
	
	public void zeroFieldsAfterSave() {
		setDistance(0);
		setPauseTime(0);
		locationList = null;
	}
	
	public void updateTraceWithTime(Location newLocation) {
		// not first point after start or resume
		if (traceWithTime.isEmpty()) {
			traceWithTime.add(new LinkedList<Pair<Location, Long>>());
		}
		synchronized (getTime()) {
			traceWithTime.getLast().add(new Pair<Location, Long>(newLocation, getTime()));
		}
	}
	
	public void initBeforeRecording() {
		calendar = Calendar.getInstance();
		singleRun = new SingleRun();
		singleRun.setStartDate(calendar.getTime());
		traceWithTime = new LinkedList<LinkedList<Pair<Location, Long>>>();
	}
	
	public void saveRun(String name) {
		// add last values
		singleRun.setEndDate(calendar.getTime());
		singleRun.setRunTime(getTime());
		singleRun.setDistance(getDistance());
		singleRun.setTraceWithTime(traceWithTime);
		singleRun.setName(name);
		
		// store in DB
		Database db = new Database(context);
		db.insertSingleRun(singleRun);
	}
	
	public void addToLocationList(Location location) {
		locationList.add(location);
	}
}
