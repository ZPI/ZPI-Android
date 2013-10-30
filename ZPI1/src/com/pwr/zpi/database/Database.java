package com.pwr.zpi.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.database.entity.WorkoutActionAdvanced;
import com.pwr.zpi.database.entity.WorkoutActionSimple;
import com.pwr.zpi.utils.Pair;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.utils.Pair;

	
public class Database extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "Historia_biegacza";
	
	// Tables names
	private static final String DATES = "Daty";
	private static final String POINTS_WITH_TIME = "Pomiary_lokacji_i_czasu";
	private static final String WORKOUTS = "Plany_treningowe";
	private static final String WORKOUTS_ACTIONS = "Plany_podakcje";
	private static final String ACTIONS_SIMPLE = "Akcje";
	private static final String ACTIONS_ADVANCED = "Akcje_zaawansowane";

	// Tables column names
	// dates
	private static final String DATES_START_HOUR = "d_godzina_i_dzien_startu";
	private static final String DATES_END_HOUR = "d_godzina_i_dzien_konca";
	private static final String DATES_RUN_NUMBER = "d_id_biegu";
	private static final String DATES_RUN_DISTANCE = "d_dystans_biegu";
	private static final String DATES_RUN_TIME = "d_czas_biegu";
	// points_with_time
	private static final String PWT_ID = "pwt_id";
	private static final String PWT_RUN_NUMBER = "pwt_id_biegu";
	private static final String PWT_SUB_RUN_NUMBER = "pwt_sub_id_biegu";
	private static final String PWT_LONGITUDE = "pwt_longitude";
	private static final String PWT_LATITUDE = "pwt_latitude";
	private static final String PWT_ALTITUDE = "pwt_altitude";
	private static final String PWT_TIME_FROM_START = "pwr_time_from_start";
	// workouts
	private static final String WORKOUTS_ID = "workouts_id";
	private static final String WORKOUTS_NAME = "workouts_name";
	private static final String WORKOUTS_REPEATS = "workouts_repeats";
	private static final String WORKOUTS_WARM_UP = "workouts_warm_up";
	// workouts actions
	private static final String WA_ID = "wa_id";
	private static final String WA_WORKOUT_ID = "wa_wokrout_id";
	private static final String WA_ACTION_ID = "wa_action_id";
	private static final String WA_ACTION_TYPE = "wa_action_type";
	// action simple
	private static final String AS_ID = "as_id";
	private static final String AS_SPEED_TYPE = "as_speed_type";
	private static final String AS_VALUE_TYPE = "as_value_type";
	private static final String AS_VALUE = "as_value";
	private static final String AS_ORDER_NUMBER = "as_order_number";
	// action advanced
	private static final String AA_ID = "aa_id";
	private static final String AA_TYPE = "aa_type";
	private static final String AA_DISTANCE_VALUE = "aa_distance_value";
	private static final String AA_TIME_VALUE = "aa_time_value";
	private static final String AA_PACE_VALUE = "aa_pace_value";
	private static final String AA_ORDER_NUMBER = "aa_order_number";

	// Tables creation schemas
	private static final String CREATE_TABLE_DATES = "CREATE TABLE " + DATES
		+ "(" + DATES_RUN_NUMBER + " INTEGER PRIMARY KEY AUTOINCREMENT, "
		+ DATES_START_HOUR + " INTEGER, " + DATES_END_HOUR + " INTEGER ,"
		+ DATES_RUN_DISTANCE + " INTEGER, " + DATES_RUN_TIME + " INTEGER"
		+ ")";
	private static final String CREATE_TABLE_POINTS_WITH_TIME = "CREATE TABLE "
			+ POINTS_WITH_TIME + "(" + PWT_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + PWT_RUN_NUMBER
			+ " INTEGER, " + PWT_SUB_RUN_NUMBER + " INTEGER, " + PWT_LONGITUDE
			+ " DOUBLE, " + PWT_LATITUDE + " DOUBLE, " + PWT_ALTITUDE
			+ " DOUBLE, " + PWT_TIME_FROM_START + " INTEGER, "
			+ "FOREIGN KEY (" + PWT_RUN_NUMBER + ")" + " REFERENCES " + DATES
			+ " (" + DATES_RUN_NUMBER + ")" + ")";
	private static final String CREATE_TABLE_WORKOUTS = "CREATE TABLE "
			+ WORKOUTS + "(" + WORKOUTS_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + WORKOUTS_NAME
			+ " TEXT, " + WORKOUTS_REPEATS + " INTEGER, " + WORKOUTS_WARM_UP
			+ " INTEGER" + ")";
	private static final String CREATE_TABLE_WORKOUTS_ACTIONS = "CREATE TABLE "
			+ WORKOUTS_ACTIONS + "(" + WA_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + WA_WORKOUT_ID
			+ " INTEGER, " + WA_ACTION_ID + " INTEGER, " + WA_ACTION_TYPE
			+ " INTEGER" + ")";
	private static final String CREATE_TABLE_ACTIONS_SIMPLE = "CREATE TABLE "
			+ ACTIONS_SIMPLE + "(" + AS_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + AS_SPEED_TYPE
			+ " INTEGER, " + AS_VALUE_TYPE + " INTEGER, " + AS_VALUE
			+ " DOUBLE, " + AS_ORDER_NUMBER + " INTEGER" + ")";
	private static final String CREATE_TABLE_ACTIONS_ADVANCED = "CREATE TABLE "
			+ ACTIONS_ADVANCED + "(" + AA_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + AA_TYPE + " INTEGER, "
			+ AA_DISTANCE_VALUE + " DOUBLE, " + AA_TIME_VALUE + " DOUBLE, "
			+ AA_PACE_VALUE + " DOUBLE, " + AA_ORDER_NUMBER + " INTEGER" + ")";

	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_DATES);
		db.execSQL(CREATE_TABLE_POINTS_WITH_TIME);
		db.execSQL(CREATE_TABLE_WORKOUTS);
		db.execSQL(CREATE_TABLE_WORKOUTS_ACTIONS);
		db.execSQL(CREATE_TABLE_ACTIONS_SIMPLE);
		db.execSQL(CREATE_TABLE_ACTIONS_ADVANCED);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + ACTIONS_ADVANCED);
		db.execSQL("DROP TABLE IF EXISTS " + ACTIONS_SIMPLE);
		db.execSQL("DROP TABLE IF EXISTS " + WORKOUTS_ACTIONS);
		db.execSQL("DROP TABLE IF EXISTS " + WORKOUTS);
		db.execSQL("DROP TABLE IF EXISTS " + POINTS_WITH_TIME);
		db.execSQL("DROP TABLE IF EXISTS " + DATES);
		onCreate(db);
	}
	
	public boolean insertSingleRun(SingleRun singleRun) {
		SQLiteDatabase db = getWritableDatabase();
		
		long runID = insertDatePart(db, singleRun);
		boolean isInsertOk = runID != -1;
		if (isInsertOk) {
			db.beginTransaction();
			
			long subRunNumber = 1;
			LinkedList<LinkedList<Pair<Location, Long>>> traceWithTime = singleRun
				.getTraceWithTime();
			for (LinkedList<Pair<Location, Long>> subRun : traceWithTime) {
				for (Pair<Location, Long> singlePointWithTime : subRun) {
					isInsertOk = isInsertOk
						&& insertRunPart(db, singlePointWithTime, runID,
							subRunNumber);
				}
				subRunNumber++;
			}
			if (isInsertOk) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			
		}
		db.close();
		return isInsertOk;
	}
	
	/**
	 * @param db
	 * @param singleRun
	 * @return it returns -1 if insertion fails, else returns run_id
	 */
	private long insertDatePart(SQLiteDatabase db, SingleRun singleRun) {
		ContentValues cv = new ContentValues();
		cv.put(DATES_START_HOUR, singleRun.getStartDate().getTime());
		cv.put(DATES_END_HOUR, singleRun.getEndDate().getTime());
		cv.put(DATES_RUN_DISTANCE, singleRun.getDistance());
		cv.put(DATES_RUN_TIME, singleRun.getRunTime());
		return db.insert(DATES, null, cv);
	}
	
	private boolean insertRunPart(SQLiteDatabase db,
		Pair<Location, Long> singlePointWithTime, long runID,
		long subRunNumber) {
		ContentValues cv = new ContentValues();
		cv.put(PWT_RUN_NUMBER, runID);
		cv.put(PWT_SUB_RUN_NUMBER, subRunNumber);
		cv.put(PWT_LONGITUDE, singlePointWithTime.first.getLongitude());
		cv.put(PWT_LATITUDE, singlePointWithTime.first.getLatitude());
		cv.put(PWT_ALTITUDE, singlePointWithTime.first.getAltitude());
		cv.put(PWT_TIME_FROM_START, singlePointWithTime.second);
		
		return db.insert(POINTS_WITH_TIME, null, cv) != -1;
	}
	
	public SingleRun getRun(long runID) {
		SQLiteDatabase db = getReadableDatabase();
		
		String[] columns = { DATES_START_HOUR, DATES_END_HOUR,
			DATES_RUN_NUMBER, DATES_RUN_DISTANCE, DATES_RUN_TIME };
		
		Cursor cursor = db.query(DATES, columns, DATES_RUN_NUMBER + "=?",
			new String[] { runID + "" }, null, null, null);
		
		SingleRun run = null;
		if (cursor != null && cursor.moveToFirst()) {
			run = getSingleRunForCursor(cursor, db, true);
			cursor.close();
		}
		cursor.close();
		db.close();
		return run;
	}
	
	public List<SingleRun> getAllRuns() {
		SQLiteDatabase db = getReadableDatabase();
		String[] columns = { DATES_START_HOUR, DATES_END_HOUR,
			DATES_RUN_NUMBER, DATES_RUN_DISTANCE, DATES_RUN_TIME };
		ArrayList<SingleRun> runs = null;
		
		Cursor cursor = db.query(DATES, columns, null, null, null, null,
			DATES_START_HOUR + " ASC");
		if (cursor.moveToFirst()) {
			runs = new ArrayList<SingleRun>();
			do {
				SingleRun singleRun = getSingleRunForCursor(cursor, db, false);
				runs.add(singleRun);
			}
			while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return runs;
	}
	
	private SingleRun getSingleRunForCursor(Cursor cursor, SQLiteDatabase db,
		boolean needAllInfo) {
		
		SingleRun readSingleRun = new SingleRun();
		Date startDate = new Date(cursor.getLong(0));
		readSingleRun.setStartDate(startDate);
		
		Date endDate = new Date(cursor.getLong(1));
		readSingleRun.setEndDate(endDate);
		
		Log.e("db", cursor.getLong(2) + "");
		
		readSingleRun.setRunID(cursor.getLong(2));
		readSingleRun.setDistance(cursor.getDouble(3));
		readSingleRun.setRunTime(cursor.getLong(4));
		if (needAllInfo) {
			LinkedList<LinkedList<Pair<Location, Long>>> trace = getTraceForRunID(
				readSingleRun.getRunID(), db);
			readSingleRun.setTraceWithTime(trace);
		}
		
		return readSingleRun;
	}
	
	private LinkedList<LinkedList<Pair<Location, Long>>> getTraceForRunID(
		long runID, SQLiteDatabase db) {
		String[] columns = { PWT_ID, PWT_RUN_NUMBER, PWT_SUB_RUN_NUMBER,
			PWT_LONGITUDE, PWT_LATITUDE, PWT_ALTITUDE, PWT_TIME_FROM_START };
		LinkedList<LinkedList<Pair<Location, Long>>> trace = null;
		
		Cursor cursor = db.query(POINTS_WITH_TIME, columns, PWT_RUN_NUMBER
			+ " = ?", new String[] { runID + "" }, null, null,
			PWT_SUB_RUN_NUMBER);
		if (cursor.moveToFirst()) {
			trace = new LinkedList<LinkedList<Pair<Location, Long>>>();
			long lastSubRunID = -1;
			LinkedList<Pair<Location, Long>> subRun = null;
			do {
				long subrunID = cursor.getLong(2);
				long time = cursor.getLong(6);
				Location location = getLocationFromCursor(cursor);
				
				if (subrunID != lastSubRunID) {
					if (subRun != null) {
						trace.add(subRun);
					}
					lastSubRunID = subrunID;
					subRun = new LinkedList<Pair<Location, Long>>();
				}
				subRun.add(new Pair<Location, Long>(location, time));
			}
			while (cursor.moveToNext());
			trace.add(subRun); // adding last subrun
		}
		cursor.close();
		
		return trace;
	}
	
	private Location getLocationFromCursor(Cursor cursor) {
		double lon = cursor.getDouble(3);
		double lat = cursor.getDouble(4);
		double alt = cursor.getDouble(5);
		
		Location location = new Location("");
		location.setLongitude(lon);
		location.setLatitude(lat);
		location.setAltitude(alt);
		
		return location;
	}
	
	public String getDBName() {
		return DATABASE_NAME;
	}
	
	public boolean deleteRun(long runID) {
		SQLiteDatabase db = getWritableDatabase();
		String[] queryArgs = new String[] { runID + "" };
		int deletedNumber = db.delete(POINTS_WITH_TIME, PWT_RUN_NUMBER + "=?",
				queryArgs);
		deletedNumber += db.delete(DATES, DATES_RUN_NUMBER + "=?", queryArgs);
		db.close();
		return deletedNumber != 0;
	}

	public boolean insertWorkout(Workout workout) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		boolean isInsertOK = true;
		long workoutID = insertWorkoutPart(db, workout);
		if (isInsertOK = workoutID != -1) {
			List<WorkoutAction> actions = workout.getActions();
			int index = 0;
			for (WorkoutAction workoutAction : actions) {
				long actionID = -1;
				int workoutType = -1;
				if (workoutAction instanceof WorkoutActionSimple) {
					workoutType = WorkoutAction.ACTION_SIMPLE;
					actionID = insertSimpleWorkoutAction(db,
							(WorkoutActionSimple) workoutAction, index);
				} else if (workoutAction instanceof WorkoutActionAdvanced) {
					workoutType = WorkoutAction.ACTION_ADVANCED;
					actionID = insertAdvancedWorkoutAction(db,
							(WorkoutActionAdvanced) workoutAction, index);
				}
				isInsertOK = isInsertOK && actionID != -1;
				if (isInsertOK) {
					isInsertOK = insertWorkoutIDActionID(db, workoutID,
							actionID, workoutType);
				}
				index++;
			}
		}
		if (isInsertOK)
			db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return isInsertOK;
	}

	private long insertWorkoutPart(SQLiteDatabase db, Workout workout) {
		ContentValues cv = new ContentValues();
		cv.put(WORKOUTS_NAME, workout.getName());
		cv.put(WORKOUTS_REPEATS, workout.getRepeatCount());
		cv.put(WORKOUTS_WARM_UP, booleanToInt(workout.isWarmUp()));

		return db.insert(WORKOUTS, null, cv);
	}

	private int booleanToInt(boolean value) {
		return value ? 1 : 0;
	}

	private boolean intToBoolean(int value) {
		return value == 1;
	}

	private long insertSimpleWorkoutAction(SQLiteDatabase db,
			WorkoutActionSimple workoutAction, int index) {
		ContentValues cv = new ContentValues();
		cv.put(AS_SPEED_TYPE, workoutAction.getSpeedType());
		cv.put(AS_VALUE_TYPE, workoutAction.getValueType());
		cv.put(AS_VALUE, workoutAction.getValue());
		cv.put(AS_ORDER_NUMBER, index);

		return db.insert(ACTIONS_SIMPLE, null, cv);
	}

	private long insertAdvancedWorkoutAction(SQLiteDatabase db,
			WorkoutActionAdvanced workoutAction, int index) {
		ContentValues cv = new ContentValues();
		cv.put(AA_TYPE, workoutAction.getType());
		cv.put(AA_DISTANCE_VALUE, workoutAction.getDistance());
		cv.put(AA_TIME_VALUE, workoutAction.getTime());
		cv.put(AA_PACE_VALUE, workoutAction.getPace());
		cv.put(AA_ORDER_NUMBER, index);

		return db.insert(ACTIONS_ADVANCED, null, cv);
	}

	private boolean insertWorkoutIDActionID(SQLiteDatabase db, long workoutID,
			long actionID, int workoutType) {
		ContentValues cv = new ContentValues();
		cv.put(WA_WORKOUT_ID, workoutID);
		cv.put(WA_ACTION_ID, actionID);
		cv.put(WA_ACTION_TYPE, workoutType);

		return db.insert(WORKOUTS_ACTIONS, null, cv) != -1;
	}

	public List<Workout> getAllWorkoutNames() {
		SQLiteDatabase db = getReadableDatabase();
		String[] columns = new String[] { WORKOUTS_ID, WORKOUTS_NAME,
				WORKOUTS_REPEATS, WORKOUTS_WARM_UP };
		Cursor cursor = db.query(WORKOUTS, columns, null, null, null, null,
				null);

		List<Workout> result = null;
		if (cursor.moveToFirst()) {
			result = new ArrayList<Workout>();
			do {
				Workout workout = getWorkoutFromCursor(cursor);
				result.add(workout);
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		return result;
	}

	private Workout getWorkoutFromCursor(Cursor cursor) {
		Workout workout = new Workout();
		workout.setID(cursor.getLong(0));
		workout.setName(cursor.getString(1));
		workout.setRepeatCount(cursor.getInt(2));
		workout.setWarmUp(intToBoolean(cursor.getInt(3)));
		return workout;
	}

	public Workout getWholeSingleWorkout(long workoutID) {
		SQLiteDatabase db = getReadableDatabase();
		String[] columns = new String[] { WORKOUTS_ID, WORKOUTS_NAME,
				WORKOUTS_REPEATS, WORKOUTS_WARM_UP };
		Cursor cursor = db.query(WORKOUTS, columns, WORKOUTS_ID + "=?",
				new String[] { workoutID + "" }, null, null, null);
		Workout workout = null;
		List<WorkoutActionSimple> simpleActions = null;
		List<WorkoutActionAdvanced> advancedActions = null;
		if (cursor.moveToFirst()) {
			workout = getWorkoutFromCursor(cursor);
			simpleActions = getSimpleWorkoutActions(db, workout.getID());
			advancedActions = getAdvancedWorkoutActions(db, workout.getID());
		}
		cursor.close();
		db.close();

		if (workout != null
				&& (simpleActions != null || advancedActions != null)) {
			workout.setActions(mergeSimpleAdvancedActionsOrdered(simpleActions,
					advancedActions));
		}

		return workout;
	}

	private List<WorkoutAction> mergeSimpleAdvancedActionsOrdered(
			List<WorkoutActionSimple> simpleActions,
			List<WorkoutActionAdvanced> advancedActions) {
		List<WorkoutAction> result = new ArrayList<WorkoutAction>();
		if (simpleActions != null) {
			result.addAll(simpleActions);
		}
		if (advancedActions != null) {
			result.addAll(advancedActions);
		}
		Collections.sort(result);

		return result;
	}

	private List<WorkoutActionSimple> getSimpleWorkoutActions(
			SQLiteDatabase db, long workoutID) {
		String query = "SELECT " + AS_ID + ", " + AS_SPEED_TYPE + ", "
				+ AS_VALUE_TYPE + ", " + AS_VALUE + ", " + AS_ORDER_NUMBER
				+ " FROM " + ACTIONS_SIMPLE + " WHERE " + AS_ID + " IN " + "("
				+ "SELECT " + WA_ACTION_ID + " FROM " + WORKOUTS_ACTIONS
				+ " WHERE " + WA_WORKOUT_ID + "=?" + " AND " + WA_ACTION_TYPE
				+ "=?" + ")";
		Cursor cursor = db.rawQuery(query, new String[] { workoutID + "",
				WorkoutAction.ACTION_SIMPLE + "" });

		List<WorkoutActionSimple> simpleWorkouts = null;
		if (cursor.moveToFirst()) {
			simpleWorkouts = new ArrayList<WorkoutActionSimple>();
			do {
				WorkoutActionSimple simple = getSimpleWorkoutActionFromCursor(cursor);
				simpleWorkouts.add(simple);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return simpleWorkouts;
	}

	private WorkoutActionSimple getSimpleWorkoutActionFromCursor(Cursor cursor) {
		WorkoutActionSimple simple = new WorkoutActionSimple(cursor.getInt(1),
				cursor.getInt(2), cursor.getDouble(3));
		simple.setID(cursor.getLong(0));
		simple.setOrderNumber(cursor.getInt(4));
		return simple;
	}

	private List<WorkoutActionAdvanced> getAdvancedWorkoutActions(
			SQLiteDatabase db, long workoutID) {
		String query = "SELECT " + AA_ID + ", " + AA_TYPE + ", "
				+ AA_DISTANCE_VALUE + ", " + AA_TIME_VALUE + ", "
				+ AA_PACE_VALUE + ", " + AA_ORDER_NUMBER + " FROM "
				+ ACTIONS_ADVANCED + " WHERE " + AA_ID + " IN " + "("
				+ "SELECT " + WA_ACTION_ID + " FROM " + WORKOUTS_ACTIONS
				+ " WHERE " + WA_WORKOUT_ID + "=?" + " AND " + WA_ACTION_TYPE
				+ "=?" + ")";
		Cursor cursor = db.rawQuery(query, new String[] { workoutID + "",
				WorkoutAction.ACTION_ADVANCED + "" });

		List<WorkoutActionAdvanced> advancedWorkouts = null;
		if (cursor.moveToFirst()) {
			advancedWorkouts = new ArrayList<WorkoutActionAdvanced>();
			do {
				WorkoutActionAdvanced advanced = getAdvancedActionFromCursor(cursor);
				advancedWorkouts.add(advanced);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return advancedWorkouts;
	}

	private WorkoutActionAdvanced getAdvancedActionFromCursor(Cursor cursor) {
		WorkoutActionAdvanced advanced = new WorkoutActionAdvanced(
				cursor.getInt(1), cursor.getDouble(2), cursor.getDouble(4),
				cursor.getLong(3));
		advanced.setID(cursor.getLong(0));
		advanced.setOrderNumber(cursor.getInt(5));
		return advanced;
	}

	public boolean deleteWorkoutAction(long workoutID, long actionID) {
		SQLiteDatabase db = getReadableDatabase();
		String[] columns = new String[] { WA_ID, WA_WORKOUT_ID, WA_ACTION_ID,
				WA_ACTION_TYPE };
		boolean isDeleted = false;
		Cursor cursor = db.query(WORKOUTS_ACTIONS, columns, WA_WORKOUT_ID
				+ "=? AND " + WA_ACTION_ID + "=?", new String[] {
				workoutID + "", actionID + "" }, null, null, null);
		if (cursor.moveToFirst()) {
			int actionType = cursor.getInt(3);
			String[] queryString = new String[] { actionID + "" };
			if (actionType == WorkoutAction.ACTION_ADVANCED) {
				isDeleted = db.delete(ACTIONS_ADVANCED, AA_ID + "=?",
						queryString) != 0;
			} else if (actionType == WorkoutAction.ACTION_SIMPLE) {
				isDeleted = db
						.delete(ACTIONS_SIMPLE, AS_ID + "=?", queryString) != 0;
			}
			isDeleted = isDeleted
					&& db.delete(WORKOUTS_ACTIONS, WA_WORKOUT_ID + "=? AND "
							+ WA_ACTION_ID + "=?", new String[] {
							workoutID + "", actionID + "" }) != 0;
		}
		cursor.close();
		db.close();
		return isDeleted;
	}

	public boolean deleteWorkout(long workoutID) {
		SQLiteDatabase db = getWritableDatabase();
		boolean isOK;
		String queryAdvanced = "DELETE * FROM " + ACTIONS_ADVANCED + " WHERE "
				+ AA_ID + " IN " + "(SELECT " + WA_ACTION_ID + " FROM "
				+ WORKOUTS_ACTIONS + " WHERE " + WA_WORKOUT_ID + "=? AND "
				+ WA_ACTION_TYPE + "=?" + ")";
		db.rawQuery(queryAdvanced, new String[] { workoutID + "",
				WorkoutAction.ACTION_ADVANCED + "" }).getCount();
		String querySimple = "DELETE * FROM " + ACTIONS_SIMPLE + " WHERE "
				+ AS_ID + " IN " + "(SELECT " + WA_ACTION_ID + " FROM "
				+ WORKOUTS_ACTIONS + " WHERE " + WA_WORKOUT_ID + "=? AND "
				+ WA_ACTION_TYPE + "=?" + ")";
		db.rawQuery(querySimple, new String[] { workoutID + "",
				WorkoutAction.ACTION_SIMPLE + "" });

		db.delete(WORKOUTS_ACTIONS, WA_WORKOUT_ID + "=?",
				new String[] { workoutID + "" });
		isOK = db.delete(WORKOUTS, WORKOUTS_ID + "=?", new String[] { workoutID + "" }) != 0;
		db.close();
		return isOK;
	}
}
