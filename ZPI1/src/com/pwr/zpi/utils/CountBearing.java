package com.pwr.zpi.utils;

import android.location.Location;

public class CountBearing {

	
	public static float countBearing(Location location, Location lastLocation)
	{
		double dLon = location.getLongitude()-lastLocation.getLongitude();
		double lat1 = lastLocation.getLatitude();
		double lat2 = location.getLatitude();
		
				
		double y = Math.sin(dLon)* Math.cos(lat2);
		double x =  Math.cos(lat1)*Math.sin(lat2) -
		        Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
		double brng =  Math.atan2(y, x);
		return (float) Math.toDegrees(brng);
	}
}
