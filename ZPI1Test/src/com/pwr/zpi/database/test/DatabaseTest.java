package com.pwr.zpi.database.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.location.Location;
import android.test.AndroidTestCase;

import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.database.entity.WorkoutActionAdvanced;
import com.pwr.zpi.database.entity.WorkoutActionSimple;
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
	
	private SingleRun prepareDataSingleRun(int number) {
		SingleRun singleRun = new SingleRun();
		singleRun.setDistance(number * 13);
		singleRun.setStartDate(new Date(number * 13000L));
		singleRun.setEndDate(new Date(number * 13000L + 3532000000L));
		singleRun.setRunTime(number * 130000L);
		singleRun.setTraceWithTime(prepareTraceWithTime(number));
		singleRun.setName("testSingleRun");
		return singleRun;
	}
	
	private LinkedList<LinkedList<Pair<Location, Long>>> prepareTraceWithTime(int number) {
		LinkedList<LinkedList<Pair<Location, Long>>> trace = new LinkedList<LinkedList<Pair<Location, Long>>>();
		for (int i = 0; i < number; i++) {
			LinkedList<Pair<Location, Long>> subrun = new LinkedList<Pair<Location, Long>>();
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
	
	private Workout prepareWorkout(int number) {
		Workout workout = new Workout();
		workout.setName("Workout " + number);
		workout.setWarmUp(number % 2 == 0);
		workout.setActions(prepareActions(number));
		return workout;
	}
	
	private List<WorkoutAction> prepareActions(int number) {
		List<WorkoutAction> actions = new ArrayList<WorkoutAction>();
		for (int i = 0; i < number; i++) {
			if (i % 2 == 0) {
				actions.add(prepareSimpleAction(number, i));
			}
			else {
				actions.add(prepareAdvancedAction(number, i));
			}
		}
		return actions;
	}
	
	private WorkoutAction prepareSimpleAction(int number, int i) {
		int speedType = i % 6 == 0 || i % 6 == 3 ? WorkoutAction.ACTION_SIMPLE_SPEED_FAST
			: i % 6 == 1 || i % 6 == 4 ? WorkoutAction.ACTION_SIMPLE_SPEED_STEADY
				: WorkoutAction.ACTION_SIMPLE_SPEED_SLOW;
		int valueType = i % 6 == 3 || i % 6 == 4 || i % 6 == 5 ? WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE
			: WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME;
		double value = number * i;
		return new WorkoutActionSimple(speedType, valueType, value);
	}
	
	private WorkoutAction prepareAdvancedAction(int number, int i) {
		int type = i % 3 == 0 ? WorkoutAction.ACTION_ADVANCED_TYPE_DISTANCE_PACE
			: i % 3 == 1 ? WorkoutAction.ACTION_ADVANCED_TYPE_PACE_TIME
				: WorkoutAction.ACTION_ADVANCED_TYPE_TIME_DISTANCE;
		double distance = number * i;
		double pace = number * i;
		long time = number * i;
		return new WorkoutActionAdvanced(type, distance, pace, time);
	}
	
	// --tests
	
	public void testInsert() {
		boolean isOK = db.insertSingleRun(prepareDataSingleRun(12));
		assertTrue(isOK);
	}
	
	public void testGet() {
		int testSubjects = 11;
		for (int i = 0; i < testSubjects; i++) {
			boolean isOK = db.insertSingleRun(prepareDataSingleRun((i + 3) * 3));
			assertTrue(isOK);
		}
		
		List<SingleRun> list = db.getAllRuns();
		assertEquals(testSubjects, list.size());
	}
	
	public void testInsertWorkout() {
		boolean isOK = db.insertWorkout(prepareWorkout(13)) != -1;
		assertTrue(isOK);
	}
	
	public void testGetSimpleWorkouts() {
		final int TESTS_NUMBER_ITEMS = 13;
		insertWorkouts(TESTS_NUMBER_ITEMS);
		
		List<Workout> workouts = db.getAllWorkoutNames();
		assertEquals(TESTS_NUMBER_ITEMS, workouts.size());
		for (int i = 0; i < TESTS_NUMBER_ITEMS; i++) {
			Workout w = workouts.get(i);
			checkSimpleWorkout(w, i + 1);
		}
	}
	
	private void insertWorkouts(int TESTS_NUMBER_ITEMS) {
		boolean isOK = true;
		for (int i = 0; i < TESTS_NUMBER_ITEMS; i++) {
			isOK = isOK && db.insertWorkout(prepareWorkout(i + 1)) != -1;
		}
		assertTrue(isOK);
	}
	
	private void checkSimpleWorkout(Workout w, int i) {
		assertEquals("Workout " + i, w.getName());
		assertEquals(i % 2 == 0, w.isWarmUp());
	}
	
	public void testGetWholeSingleWorkout() {
		final int TESTS_NUMBER_ITEMS = 20;
		insertWorkouts(TESTS_NUMBER_ITEMS);
		
		List<Workout> workouts = db.getAllWorkoutNames();
		
		for (Workout workout : workouts) {
			Workout w = db.getWholeSingleWorkout(workout.getID());
			int creationI = 1;// repeat count points the
			// creation i number
			checkSimpleWorkout(w, creationI);
			checkActions(w.getActions(), creationI);
		}
	}
	
	private void checkActions(List<WorkoutAction> actions, int creationI) {
		int previousOrder = actions.get(0).getOrderNumber() - 1;
		for (int i = 0; i < actions.size(); i++) {
			assertEquals(previousOrder + 1, actions.get(i).getOrderNumber());
			previousOrder++;
			
			if (i % 2 == 0) {
				assertTrue(actions.get(i) instanceof WorkoutActionSimple);
				checkSimpleAction(creationI, i, (WorkoutActionSimple) actions.get(i));
			}
			else {
				assertTrue(actions.get(i) instanceof WorkoutActionAdvanced);
				checkAdvancedAction(creationI, i, (WorkoutActionAdvanced) actions.get(i));
			}
		}
	}
	
	private void checkSimpleAction(int creationI, int i, WorkoutActionSimple simple) {
		assertEquals(i % 6 == 0 || i % 6 == 3 ? WorkoutAction.ACTION_SIMPLE_SPEED_FAST
			: i % 6 == 1 || i % 6 == 4 ? WorkoutAction.ACTION_SIMPLE_SPEED_STEADY
				: WorkoutAction.ACTION_SIMPLE_SPEED_SLOW, simple.getSpeedType());
		assertEquals(i % 6 == 3 || i % 6 == 4 || i % 6 == 5 ? WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE
			: WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME, simple.getValueType());
		assertEquals((double) creationI * i, simple.getValue());
	}
	
	private void checkAdvancedAction(int creationI, int i, WorkoutActionAdvanced advanced) {
		assertEquals(i % 3 == 0 ? WorkoutAction.ACTION_ADVANCED_TYPE_DISTANCE_PACE
			: i % 3 == 1 ? WorkoutAction.ACTION_ADVANCED_TYPE_PACE_TIME
				: WorkoutAction.ACTION_ADVANCED_TYPE_TIME_DISTANCE, advanced.getType());
		if (advanced.getType() == WorkoutAction.ACTION_ADVANCED_TYPE_DISTANCE_PACE) {
			assertEquals((double) creationI * i, advanced.getDistance());
			assertEquals((double) creationI * i, advanced.getPace());
		}
		else if (advanced.getType() == WorkoutAction.ACTION_ADVANCED_TYPE_PACE_TIME) {
			assertEquals(creationI * i, advanced.getTime());
			assertEquals((double) creationI * i, advanced.getPace());
		}
		else if (advanced.getType() == WorkoutAction.ACTION_ADVANCED_TYPE_TIME_DISTANCE) {
			assertEquals((double) creationI * i, advanced.getDistance());
			assertEquals(creationI * i, advanced.getTime());
		}
	}
}
