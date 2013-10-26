package com.pwr.zpi;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import com.pwr.zpi.adapters.RunAdapter;
import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.listeners.GestureListener;
import com.pwr.zpi.listeners.MyGestureDetector;
import com.pwr.zpi.utils.Pair;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class HistoryActivity extends Activity implements GestureListener,
		OnItemClickListener {

	GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private ListView listViewThisWeek;
	private ListView listViewThisMonth;
	private ListView listViewAll;

	private static final String TAB_SPEC_1_TAG = "TabSpec1";
	private static final String TAB_SPEC_2_TAG = "TabSpec2";
	private static final String TAB_SPEC_3_TAG = "TabSpec3";
	public static final String ID_TAG = "id";
	SingleRun run_data[];

	/*
	 * MOCK DATA
	 */

	private void addMockData() {

		Database db = new Database(this);
		for (int i = 0; i < 11; i++) {
			boolean isOK = db.insertSingleRun(prepareData((i + 3) * 3));
			;
		}
		db.close();
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
		LinkedList<LinkedList<Pair<Location, Long>>> trace = new LinkedList<LinkedList<Pair<Location, Long>>>();
		for (int i = 0; i < 5; i++) {
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
		Log.i("Database", "run added");
		return trace;
	}

	/*
	 * END MOCK DATA
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_activity);

		addListeners();

		TabHost tabHost = (TabHost) findViewById(R.id.tabhostHistory);
		tabHost.setup();
		TabSpec tabSpecs = tabHost.newTabSpec(TAB_SPEC_1_TAG);
		tabSpecs.setContent(R.id.tabThisWeek);
		tabSpecs.setIndicator(getResources().getString(R.string.this_week));
		tabHost.addTab(tabSpecs);
		tabSpecs = tabHost.newTabSpec(TAB_SPEC_2_TAG);
		tabSpecs.setContent(R.id.tabThisMonth);
		tabSpecs.setIndicator(getResources().getString(R.string.this_month));
		tabHost.addTab(tabSpecs);
		tabSpecs = tabHost.newTabSpec(TAB_SPEC_3_TAG);
		tabSpecs.setContent(R.id.tabAll);
		tabSpecs.setIndicator(getResources().getString(R.string.all));
		tabHost.addTab(tabSpecs);

		// TODO get from database
		// addMockData();
		// run_data = new SingleRun[11];

		listViewThisWeek = (ListView) findViewById(R.id.listViewThisWeek);
		listViewThisMonth = (ListView) findViewById(R.id.listViewThisMonth);
		listViewAll = (ListView) findViewById(R.id.listViewAll);

		run_data = readfromDB();

		RunAdapter adapter = new RunAdapter(this,
				R.layout.history_run_list_item, run_data);
		listViewThisWeek.setAdapter(adapter);
		listViewThisMonth.setAdapter(adapter);
		listViewAll.setAdapter(adapter);
		listViewThisWeek.setOnItemClickListener(this);
		listViewThisMonth.setOnItemClickListener(this);
		listViewAll.setOnItemClickListener(this);
	}

	private SingleRun[] readfromDB() {
		Database db = new Database(this);
		ArrayList<SingleRun> runs;
		runs = (ArrayList<SingleRun>) db.getAllRuns();
		SingleRun[] run_data = new SingleRun[0];
		if (runs != null)
			run_data = runs.toArray(run_data);

		return run_data;
	}

	private void prepareGestureListener() {
		// Gesture detection
		gestureDetector = new GestureDetector(this, new MyGestureDetector(this,
				false, false, true, false));
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureListener.onTouch(null, event);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_right_anim, R.anim.out_right_anim);

	}

	@Override
	public void onLeftToRightSwipe() {

		finish();
		overridePendingTransition(R.anim.in_right_anim, R.anim.out_right_anim);
	}

	@Override
	public void onRightToLeftSwipe() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpToDownSwipe() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDownToUpSwipe() {
		// TODO Auto-generated method stub

	}

	private void addListeners() {

	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long id) {
		Intent intent = new Intent(HistoryActivity.this,
				SingleRunHistoryActivity.class);
		SingleRun selectedValue = (SingleRun) adapter
				.getItemAtPosition(position);
		intent.putExtra(ID_TAG, selectedValue.getRunID());
		startActivity(intent);
		overridePendingTransition(R.anim.in_left_anim, R.anim.out_left_anim);
		Log.e("S", "not started");
	}

}
