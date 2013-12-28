package com.pwr.zpi.utils;

import java.util.concurrent.LinkedBlockingQueue;

import android.location.Location;

public class ActualPaceCalculator {
	
	private static final int NUMBER_OF_POINTS_CONSIDERED = 30;
	private final LinkedBlockingQueue<Location> lastPointsQueue;
	private double distanceSum;
	private long timeSum;
	private Location previous;
	
	public ActualPaceCalculator()
	{
		lastPointsQueue = new LinkedBlockingQueue<Location>();
		distanceSum = 0;
		timeSum = 0;
	}
	
	/**
	 * 
	 * @param location - latest location
	 * @return new avg pace from last points
	 */
	public double addPoint(Location location)
	{
		lastPointsQueue.add(location);
		
		if (previous == null)
		{
			previous = location;
			return 1 / (location.getSpeed() * 6 / 100);
		}
		distanceSum += previous.distanceTo(location);
		timeSum += location.getTime() - previous.getTime();
		previous = location;
		if (lastPointsQueue.size() > NUMBER_OF_POINTS_CONSIDERED)
		{
			Location oldLocation = lastPointsQueue.poll();
			Location nextToRemove = lastPointsQueue.peek();
			distanceSum -= oldLocation.distanceTo(nextToRemove);
			timeSum -= nextToRemove.getTime() - oldLocation.getTime();
			
		}
		return timeSum / distanceSum / 60;
	}
	
	public void reset()
	{
		lastPointsQueue.clear();
		distanceSum = 0;
		timeSum = 0;
		previous = null;
	}
	
}
