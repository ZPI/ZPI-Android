package com.pwr.zpi.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.location.Location;
import android.util.Log;

import com.pwr.zpi.R;
import com.pwr.zpi.database.entity.SingleRun;

public class LineChart {

	private static Double speedMin, speedMax, distanceMin, distanceMax,
			altitudeMin, altitudeMax;

	public static Intent getChartForData(SingleRun run, Context context) {

		int numberOfPoints = countNumberOfPoints(run);
		int currentIndex = 0;
		double[] speedValues = new double[numberOfPoints];
		double[] distanceValues = new double[numberOfPoints];
		double[] altitudeValues = new double[numberOfPoints];

		Resources res = context.getResources();
		
		speedMin = distanceMin = altitudeMin = Double.MAX_VALUE;
		speedMax = distanceMax = altitudeMax = Double.MIN_VALUE;

		LinkedList<LinkedList<Pair<Location, Long>>> traceWithTime = run
				.getTraceWithTime();
		double cumulativeDistance = 0;
		for (LinkedList<Pair<Location, Long>> subrun : traceWithTime) {
			Pair<Location, Long> previous = subrun.removeFirst();

			for (Pair<Location, Long> current : subrun) {
				speedValues[currentIndex] = GeographicalEvaluations
						.calculateSpeedBetweenPoints(current.first,
								current.second, previous.first, previous.second);
				cumulativeDistance += GeographicalEvaluations.countDistance(
						current.first, previous.first);
				distanceValues[currentIndex] = cumulativeDistance;
				altitudeValues[currentIndex] = current.first.getAltitude();
				findMinMax(speedValues[currentIndex],
						distanceValues[currentIndex],
						altitudeValues[currentIndex]);
				previous = current;
				currentIndex++;
			}
		}

		List<double[]> x = new ArrayList<double[]>();
		x.add(distanceValues);
		x.add(distanceValues); // this is not a bug ;)

		List<double[]> values = new ArrayList<double[]>();
		values.add(speedValues);

		int[] colors = new int[] { Color.BLUE, Color.YELLOW };
		PointStyle[] styles = new PointStyle[] { PointStyle.POINT,
				PointStyle.POINT };
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(2);
		setRenderer(renderer, colors, styles);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = (XYSeriesRenderer) renderer
					.getSeriesRendererAt(i);
			r.setLineWidth(3f);
		}
		setChartSettings(renderer, res.getString(R.string.chart_title), res.getString(R.string.chart_ox_distance), res.getString(R.string.chart_oy1_speed),
				distanceMin, distanceMax, speedMin, speedMax, Color.LTGRAY,
				Color.LTGRAY);
		renderer.setXLabels(12);
		renderer.setYLabels(10);
		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setZoomButtonsVisible(true);
		// renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
		// renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
		renderer.setZoomRate(1.05f);
		renderer.setLabelsColor(Color.WHITE);
		renderer.setXLabelsColor(Color.GREEN);
		renderer.setYLabelsColor(0, colors[0]);
		renderer.setYLabelsColor(1, colors[1]);

		renderer.setYTitle(res.getString(R.string.chart_oy2_altitude), 1);
		renderer.setYAxisAlign(Align.RIGHT, 1);
		renderer.setYLabelsAlign(Align.LEFT, 1);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.BLACK);

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		addXYSeries(dataset, new String[] { res.getString(R.string.chart_legend_oy1) }, x, values, 100);
		values.clear();
		values.add(altitudeValues);
		addXYSeries(dataset, new String[] { res.getString(R.string.chart_legend_oy2) }, x, values, 1);

		Intent intent = ChartFactory.getCubicLineChartIntent(context, dataset,
				renderer, 0.3f, res.getString(R.string.chart_title));
		
		return intent;
	}

	private static void findMinMax(double speed, double distance,
			double altitude) {
		if (speed < speedMin) {
			speedMin = speed;
		} else if (speed > speedMax) {
			speedMax = speed;
		}

		if (distance < distanceMin) {
			distanceMin = distance;
		} else if (distance > distanceMax) {
			distanceMax = distance;
		}

		if (altitude < altitudeMin) {
			altitudeMin = altitude;
		} else if (altitude > altitudeMax) {
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

	private static void setRenderer(XYMultipleSeriesRenderer renderer,
			int[] colors, PointStyle[] styles) {
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 20 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}
	}

	private static void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}

	private static XYMultipleSeriesDataset buildDataset(String[] titles,
			List<double[]> xValues, List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		addXYSeries(dataset, titles, xValues, yValues, 0);
		return dataset;
	}

	private static void addXYSeries(XYMultipleSeriesDataset dataset,
			String[] titles, List<double[]> xValues, List<double[]> yValues,
			int scale) {
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i], scale);
			double[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
	}
}
