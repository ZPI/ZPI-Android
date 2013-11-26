package com.pwr.zpi;

import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pwr.zpi.database.entity.TreningPlan;
import com.pwr.zpi.mock.TreningPlans;
import com.pwr.zpi.utils.Pair;
import com.pwr.zpi.utils.TimeFormatter;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

public class PlansActivity extends FragmentActivity {
	
	private CaldroidFragment calendar;
	private ProgressBar progressLayout;
	private ListView listViewPlanDayActions;
	private TextView textViewIsWarmUp;
	private RelativeLayout noActionInDay;
	private RelativeLayout actionInDay;
	private TreningPlan plan;
	
	private TreeSet<Long> workoutDays;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plans_activity);
		
		init();
		
		Pair<Long, Bundle> pair = new Pair<Long, Bundle>(getPlanIDFromIntent(), savedInstanceState);
		new LoadCalendar().execute(pair);
	}
	
	private void init() {
		progressLayout = (ProgressBar) findViewById(R.id.progressBarLayout);
		listViewPlanDayActions = (ListView) findViewById(R.id.listViewActions);
		noActionInDay = (RelativeLayout) findViewById(R.id.relativeLayoutNoActivityInCurrentDay);
		actionInDay = (RelativeLayout) findViewById(R.id.relativeLayoutActivityInCurrentDay);
		textViewIsWarmUp = (TextView) findViewById(R.id.textViewIsWarmUpSet);
		
		workoutDays = new TreeSet<Long>();
	}
	
	private Long getPlanIDFromIntent() {
		Intent intent = getIntent();
		return intent.getLongExtra(PlaningActivity.ID_TAG, 0);
	}
	
	private void prepareCalendar(Bundle savedInstanceState) {
		calendar = new CaldroidFragment();
		
		// If Activity is created after rotation
		if (savedInstanceState != null) {
			calendar.restoreStatesFromKey(savedInstanceState, "CALDROID_SAVED_STATE");
		}
		// If activity is created from fresh
		else {
			Bundle args = new Bundle();
			Calendar cal = Calendar.getInstance();
			args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
			args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
			args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
			args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
			
			// Uncomment this to customize startDayOfWeek
			// args.putInt(CaldroidFragment.START_DAY_OF_WEEK,
			// CaldroidFragment.TUESDAY); // Tuesday
			calendar.setArguments(args);
		}
		
		// Attach to the activity
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.calendarFragmentPlace, calendar);
		t.commit();
		
		addCalendarListener();
	}
	
	private void addCalendarListener() {
		calendar.setCaldroidListener(new CaldroidListener() {
			
			@Override
			public void onSelectDate(Date date, View view) {
				calendar.setSelectedDates(date, date);
				Log.i(PlansActivity.class.getSimpleName(), date.toString());
				calendar.refreshView();
			}
		});
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if (calendar != null) {
			calendar.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
		}
		
		if (calendar != null) {
			calendar.saveStatesToKey(outState, "DIALOG_CALDROID_SAVED_STATE");
		}
	}
	
	private class LoadCalendar extends AsyncTask<Pair<Long, Bundle>, Void, Void> {
		
		@Override
		protected Void doInBackground(Pair<Long, Bundle>... params) {
			//FIXME mock - change to read form db in future versions
			plan = TreningPlans.getTreningPlan(params[0].first);
			prepareCalendar(params[0].second);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			new LoadEvents().execute(plan);
		}
	}
	
	private class LoadEvents extends AsyncTask<TreningPlan, Void, Void> {
		@Override
		protected Void doInBackground(TreningPlan... params) {
			TreningPlan plan = params[0];
			Calendar cal;
			for (Integer plusDays : plan.getWorkouts().keySet()) {
				cal = Calendar.getInstance();
				cal.add(Calendar.DATE, plusDays);
				Date workoutDate = cal.getTime();
				Log.i(PlansActivity.class.getSimpleName(), TimeFormatter.getDayOnly(workoutDate.getTime()) + "");
				workoutDays.add(TimeFormatter.getDayOnly(workoutDate.getTime()));
				calendar.setBackgroundResourceForDate(R.color.calendar_event_color, workoutDate);
				calendar.setTextColorForDate(R.color.calendar_event_text_color, workoutDate);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			calendar.refreshView();
			progressLayout.setVisibility(View.GONE);
		}
	}
}
