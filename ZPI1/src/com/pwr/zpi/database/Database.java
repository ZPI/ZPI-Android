package com.pwr.zpi.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.android.gms.internal.cu;
import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.utils.Pair;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Historia_biegacza";

	// Tables names
	private static final String DATES = "Daty";
	private static final String POINTS_WITH_TIME = "Pomiary_lokacji_i_czasu";

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

	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_DATES);
		db.execSQL(CREATE_TABLE_POINTS_WITH_TIME);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + POINTS_WITH_TIME);
		db.execSQL("DROP TABLE IF EXISTS " + DATES);
		onCreate(db);
	}

	public boolean insertSingleRun(SingleRun singleRun) {
		SQLiteDatabase db = getWritableDatabase();

		long runID = insertDatePart(db, singleRun);
		boolean isInsertOk = runID != -1;
		if (isInsertOk) {
			long subRunNumber = 1;
			for (LinkedList<Pair<Location, Long>> subRun : singleRun
					.getTraceWithTime()) {
				for (Pair<Location, Long> singlePointWithTime : subRun) {
					isInsertOk = isInsertOk
							&& insertRunPart(db, singlePointWithTime, runID,
									subRunNumber);
				}
				subRunNumber++;
			}
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

	public SingleRun getRun(long runID)
	{
		SQLiteDatabase db = getReadableDatabase();
		
		String[] columns = { DATES_START_HOUR, DATES_END_HOUR,
				DATES_RUN_NUMBER, DATES_RUN_DISTANCE, DATES_RUN_TIME };

        
        Cursor cursor = db.query(DATES, columns, DATES_RUN_NUMBER + "=?", new String[] { runID
                + "" }, null, null, null);
        
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
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return runs;
	}

	private SingleRun getSingleRunForCursor(Cursor cursor, SQLiteDatabase db, boolean needAllInfo) {
		
		SingleRun readSingleRun = new SingleRun();
		Date startDate = new Date(cursor.getLong(0));
		readSingleRun.setStartDate(startDate);

		Date endDate = new Date(cursor.getLong(1));
		readSingleRun.setEndDate(endDate);

		Log.e("db", cursor.getLong(2) + "");

		readSingleRun.setRunID(cursor.getLong(2));
		readSingleRun.setDistance(cursor.getDouble(3));
		readSingleRun.setRunTime(cursor.getLong(4));
		if (needAllInfo)
		{
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
			} while (cursor.moveToNext());
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

}
