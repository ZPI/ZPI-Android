package com.pwr.zpi.utils;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import android.location.Location;

import com.pwr.zpi.database.entity.SingleRun;

public class LineChartDataEvaluator {
	
	private static final int AVRAGE_FROM_LAST = 200; // current speed is an avrage from this amount of seconds
	private static Double speedMin, speedMax, distanceMin, distanceMax, altitudeMin, altitudeMax;
	
	private static void findMinMax(double speed, double distance, double altitude) {
		if (speed < speedMin && speed != Double.NaN && !Double.isInfinite(speed)) {
			speedMin = speed;
		}
		else if (speed > speedMax && speed != Double.NaN && !Double.isInfinite(speed)) {
			speedMax = speed;
		}
		
		if (distance < distanceMin) {
			distanceMin = distance;
		}
		else if (distance > distanceMax) {
			distanceMax = distance;
		}
		
		if (altitude < altitudeMin) {
			altitudeMin = altitude;
		}
		else if (altitude > altitudeMax) {
			altitudeMax = altitude;
		}
	}
	
	private static int countNumberOfPoints(SingleRun run) {
		int numberOfPoints = 0;
		for (LinkedList<Pair<Location, Long>> subs : run.getTraceWithTime()) {
			numberOfPoints += subs.size() - 1;
		}
		return numberOfPoints;
	}
	
	public static ChartDataHelperContainter evaluateDate(SingleRun run) {
		int numberOfPoints = countNumberOfPoints(run);
		int currentIndex = 0;
		double[] speedValues = new double[numberOfPoints];
		double[] distanceValues = new double[numberOfPoints];
		double[] altitudeValues = new double[numberOfPoints];
		
		speedMin = distanceMin = altitudeMin = Double.MAX_VALUE;
		speedMax = distanceMax = altitudeMax = Double.MIN_VALUE;
		
		LinkedList<LinkedList<Pair<Location, Long>>> traceWithTime = run.getTraceWithTime();
		double cumulativeDistance = 0;
		for (LinkedList<Pair<Location, Long>> subrun : traceWithTime) {
			Pair<Location, Long> previous = subrun.removeFirst();
			LinkedBlockingQueue<Pair<Double, Long>> pointsCountedQueue = new LinkedBlockingQueue<Pair<Double, Long>>();
			
			long timeSum = 0;
			double distanceSum = 0;
			for (Pair<Location, Long> current : subrun) {
				
				double distance = current.first.distanceTo(previous.first);
				
				long time = current.second - previous.second;
				if (distance > 0.5) {
					distanceSum += distance;
					timeSum += time;
					pointsCountedQueue.add(new Pair<Double, Long>(distance, time));
				}
				
				while (timeSum > AVRAGE_FROM_LAST * 1000 && !pointsCountedQueue.isEmpty()) {
					Pair<Double, Long> p = pointsCountedQueue.poll();
					distanceSum -= p.first;
					timeSum -= p.second;
				}
				
				speedValues[currentIndex] = (distanceSum / timeSum * 3600);
				cumulativeDistance += distance / 1000;
				distanceValues[currentIndex] = cumulativeDistance;
				altitudeValues[currentIndex] = current.first.getAltitude();
				findMinMax(speedValues[currentIndex], distanceValues[currentIndex], altitudeValues[currentIndex]);
				previous = current;
				currentIndex++;
			}
		}
		
		ChartDataHelperContainter container = new ChartDataHelperContainter(distanceValues, speedValues,
			altitudeValues, speedMin, speedMax, distanceMin, distanceMax, altitudeMin, altitudeMax);
		
		return container;
	}
}
