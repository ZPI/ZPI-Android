package com.pwr.zpi.utils;

import android.location.Location;

public class GeographicalEvaluations {
	
	private GeographicalEvaluations() {}
	
	private static final int R = 6371;
	
	public static float countBearing(Location location, Location lastLocation)
	{
		double dLon = location.getLongitude() - lastLocation.getLongitude();
		double lat1 = lastLocation.getLatitude();
		double lat2 = location.getLatitude();
		
		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) -
			Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
		double brng = Math.atan2(y, x);
		return (float) Math.toDegrees(brng);
	}
	
	/**
	 * @param location
	 * @param lastLocation
	 * @return distance in kilometers
	 */
	public static double countDistance(Location location, Location lastLocation) {
		double dLat = Math.toRadians(location.getLatitude() - lastLocation.getLatitude());
		double dLon = Math.toRadians(location.getLongitude() - lastLocation.getLongitude());
		
		double lat1 = Math.toRadians(location.getLatitude());
		double lat2 = Math.toRadians(lastLocation.getLatitude());
		
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
			Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;
		
		return d;
	}
	
	/**
	 * @param location
	 * @param time in milliseconds
	 * @param lastLocation
	 * @param lastTime in milliseconds
	 * @return speed in km / h
	 */
	public static double calculateSpeedBetweenPoints(double distance, long time, long lastTime) {
		return distance / (time - lastTime) * 1000 * 60 * 60; //TODO check calculation?
	}
}
