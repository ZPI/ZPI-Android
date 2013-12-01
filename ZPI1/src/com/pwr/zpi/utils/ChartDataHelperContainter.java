package com.pwr.zpi.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class ChartDataHelperContainter implements Parcelable {
	
	private double[] distance;
	private double[] speed;
	private double[] altitude;
	
	private Double speedMin, speedMax, distanceMin, distanceMax, altitudeMin, altitudeMax;
	
	public ChartDataHelperContainter(double[] distance, double[] speed, double[] altitude, Double speedMin,
		Double speedMax, Double distanceMin, Double distanceMax, Double altitudeMin, Double altitudeMax) {
		this.distance = distance;
		this.speed = speed;
		this.altitude = altitude;
		this.speedMin = speedMin;
		this.speedMax = speedMax;
		this.distanceMin = distanceMin;
		this.distanceMax = distanceMax;
		this.altitudeMin = altitudeMin;
		this.altitudeMax = altitudeMax;
	}
	
	public ChartDataHelperContainter(Parcel in) {
		readFromParcel(in);
	}
	
	public double[] getDistance() {
		return distance;
	}
	
	public double[] getSpeed() {
		return speed;
	}
	
	public double[] getAltitude() {
		return altitude;
	}
	
	public Double getSpeedMin() {
		return speedMin;
	}
	
	public Double getSpeedMax() {
		return speedMax;
	}
	
	public Double getDistanceMin() {
		return distanceMin;
	}
	
	public Double getDistanceMax() {
		return distanceMax;
	}
	
	public Double getAltitudeMin() {
		return altitudeMin;
	}
	
	public Double getAltitudeMax() {
		return altitudeMax;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeDoubleArray(distance);
		out.writeDoubleArray(speed);
		out.writeDoubleArray(altitude);
		out.writeDouble(distanceMax);
		out.writeDouble(distanceMin);
		out.writeDouble(speedMax);
		out.writeDouble(speedMin);
		out.writeDouble(altitudeMax);
		out.writeDouble(altitudeMin);
	}
	
	private void readFromParcel(Parcel in) {
		distance = in.createDoubleArray();
		speed = in.createDoubleArray();
		altitude = in.createDoubleArray();
		
		distanceMax = in.readDouble();
		distanceMin = in.readDouble();
		speedMax = in.readDouble();
		speedMin = in.readDouble();
		altitudeMax = in.readDouble();
		altitudeMin = in.readDouble();
	}
	
	public static final Parcelable.Creator<ChartDataHelperContainter> CREATOR = new Parcelable.Creator<ChartDataHelperContainter>() {
		@Override
		public ChartDataHelperContainter createFromParcel(Parcel in) {
			return new ChartDataHelperContainter(in);
		}
		
		@Override
		public ChartDataHelperContainter[] newArray(int size) {
			return new ChartDataHelperContainter[size];
		}
	};
}
