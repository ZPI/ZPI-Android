package com.pwr.zpi.database.test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.location.Location;
import android.net.TrafficStats;
import android.test.AndroidTestCase;

import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.utils.Pair;

public class DatabaseTest extends AndroidTestCase {

	private Database db;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		if (db == null) {
			db = new Database(mContext);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		mContext.deleteDatabase(db.getDBName());
		db = null;
	}
	
	private SingleRun prepareData(int number) {
		SingleRun singleRun = new SingleRun();
		singleRun.setDistance(number * 13);
		singleRun.setStartDate(new Date(number * 13000L));
		singleRun.setEndDate(new Date(number * 13000L + 3532000000L));
		singleRun.setRunTime(number * 130000L);
		singleRun.setTraceWithTime(prepareTraceWithTime(number));
		return singleRun;
	}
	
	private LinkedList<LinkedList<Pair<Location, Long>>> prepareTraceWithTime(
			int number) {
		LinkedList<LinkedList<Pair<Location, Long>>> trace = new LinkedList<LinkedList<Pair<Location,Long>>>();
		for (int i = 0; i < number; i++) {
			LinkedList<Pair<Location, Long>> subrun = new LinkedList<Pair<Location,Long>>();
			for (int j = 0; j < number; j++) {
				Location l = new Location("");
				l.setLatitude(number * i * j);
				l.setLongitude(number * i / (j + 1));
				l.setAltitude(number);
				subrun.add(new Pair<Location, Long>(l, number * 13000L));
			}
			trace.add(subrun);
		}
		return trace;
	}

	//--tests
	
	public void testInsert() {
		boolean isOK = db.insertSingleRun(prepareData(12));
		assertTrue(isOK);
	}
	
	public void testGet() {
		int testSubjects = 11;
		for (int i = 0; i < testSubjects; i++) {
			boolean isOK = db.insertSingleRun(prepareData((i + 3) * 3));
			assertTrue(isOK);
		}
		
		List<SingleRun> list = db.getAllRuns();
		assertEquals(testSubjects, list.size());
	}
}
