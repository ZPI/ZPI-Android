package com.pwr.zpi;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pwr.zpi.adapters.WorkoutActionsAdapter;
import com.pwr.zpi.database.entity.TreningPlan;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.mock.TreningPlans;
import com.pwr.zpi.utils.Pair;
import com.pwr.zpi.utils.Reminders;
import com.pwr.zpi.utils.Time;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

public class PlansActivity extends FragmentActivity implements OnClickListener {
	
	public static final String START_DATE_KEY = "start_date";
	public static final String ID_KEY = "plan_id";
	
	private CaldroidFragment calendar;
	private ProgressBar progressLayout;
	private ListView listViewPlanDayActions;
	private TextView textViewIsWarmUp;
	private TextView textViewPlanName;
	private TextView textViewNoWorkoutActions;
	private RelativeLayout noActionInDay;
	private RelativeLayout actionInDay;
	private TreningPlan plan;
	private Date startDateForPlan;
	private ImageView arrowImageView;
	private Button leftBarButton;
	private Button rightBarButton;
	private boolean isFromMainScreen;
	
	private HashMap<Date, Workout> workoutDays;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plans_activity);
		
		init();
		
		isFromMainScreen = getIntent().hasExtra(START_DATE_KEY);
		if (isFromMainScreen) { //started from main screen - change labels and functionality
			leftBarButton.setText(R.string.end_training);
			rightBarButton.setText(R.string.dismiss);
			arrowImageView.setVisibility(View.GONE);
		}
		
		Pair<Long, Bundle> pair = new Pair<Long, Bundle>(getPlanIDFromIntent(), savedInstanceState);
		new LoadCalendar().execute(pair);
		
		addListeners();
	}
	
	private void addListeners() {
		leftBarButton.setOnClickListener(this);
		rightBarButton.setOnClickListener(this);
		arrowImageView.setOnClickListener(this);
	}
	
	private void init() {
		progressLayout = (ProgressBar) findViewById(R.id.progressBarLayout);
		noActionInDay = (RelativeLayout) findViewById(R.id.relativeLayoutNoActivityInCurrentDay);
		actionInDay = (RelativeLayout) findViewById(R.id.relativeLayoutActivityInCurrentDay);
		listViewPlanDayActions = (ListView) findViewById(R.id.listViewActions);
		textViewIsWarmUp = (TextView) findViewById(R.id.textViewIsWarmUpSet);
		textViewPlanName = (TextView) findViewById(R.id.textViewTreningPlanName);
		textViewNoWorkoutActions = (TextView) findViewById(R.id.textViewNoWorkoutActions);
		
		arrowImageView = (ImageView) findViewById(R.id.imageViewArrow);
		leftBarButton = (Button) findViewById(R.id.buttonLeftBarLabel);
		rightBarButton = (Button) findViewById(R.id.buttonRightBarLabel);
		
		workoutDays = new HashMap<Date, Workout>();
	}
	
	private Long getPlanIDFromIntent() {
		Intent intent = getIntent();
		Calendar cal = Calendar.getInstance();
		long defaultTodayValue = cal.getTimeInMillis();
		long milisForPlanStartDate = intent.getLongExtra(START_DATE_KEY, defaultTodayValue);
		cal.setTimeInMillis(milisForPlanStartDate);
		startDateForPlan = cal.getTime();
		return intent.getLongExtra(PlansActivity.ID_KEY, -1);
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
				calendar.clearSelectedDates();
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				Date selected = cal.getTime();
				calendar.setSelectedDates(selected, selected);
				calendar.refreshView();
				
				Workout workoutForDay = workoutDays.get(selected);
				setViewForWorkout(workoutForDay);
			}
			
		});
	}
	
	private void setViewForWorkout(Workout workoutForDay) {
		if (workoutForDay == null) {
			noActionInDay.setVisibility(View.VISIBLE);
			actionInDay.setVisibility(View.GONE);
		}
		else {
			noActionInDay.setVisibility(View.GONE);
			actionInDay.setVisibility(View.VISIBLE);
			
			textViewIsWarmUp.setText(workoutForDay.isWarmUp() ? getString(R.string.yes) : getString(R.string.no));
			if (workoutForDay.getActions() != null) {
				Log.i(PlansActivity.class.getSimpleName(), "has actions");
				textViewNoWorkoutActions.setVisibility(View.GONE);
				listViewPlanDayActions.setVisibility(View.VISIBLE);
				listViewPlanDayActions.setAdapter(new WorkoutActionsAdapter(this, R.layout.workouts_action_list_item,
					workoutForDay.getActions()));
			}
			else {
				Log.i(PlansActivity.class.getSimpleName(), "no actions");
				listViewPlanDayActions.setVisibility(View.GONE);
				textViewNoWorkoutActions.setVisibility(View.VISIBLE);
			}
		}
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
				cal.setTime(startDateForPlan);
				cal.add(Calendar.DATE, plusDays);
				
				cal = Time.zeroTimeInDate(cal);
				
				Date workoutDate = cal.getTime();
				workoutDays.put(workoutDate, plan.getWorkouts().get(plusDays));
				
				calendar.setBackgroundResourceForDate(R.color.calendar_event_color, workoutDate);
				calendar.setTextColorForDate(R.color.calendar_event_text_color, workoutDate);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			textViewPlanName.setText(plan.getName());
			calendar.refreshView();
			progressLayout.setVisibility(View.GONE);
			Calendar cal = Calendar.getInstance();
			cal = Time.zeroTimeInDate(cal);
			Workout workoutForDay = workoutDays.get(cal.getTime());
			setViewForWorkout(workoutForDay);
		}
	}
	
	@Override
	public void onClick(View view) {
		if (view == leftBarButton) {
			if (isFromMainScreen) {
				endTraining();
			}
			else {
				finish();
			}
		}
		else if (view == rightBarButton) {
			if (isFromMainScreen) {
				finish();
			}
			else {
				selectTraining();
			}
		}
		else if (view == arrowImageView) {
			finish();
		}
	}
	
	private void selectTraining() {
		//FIXME show dialog are you sure
		Log.i("TAG", "selecting this training");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(TreningPlans.TRENING_PLANS_IS_ENABLED_KEY, true);
		editor.putLong(TreningPlans.TRENING_PLANS_ID_KEY, plan.getID());
		editor.putLong(TreningPlans.TRENING_PLANS_START_DATE_KEY, Calendar.getInstance().getTimeInMillis());
		editor.commit();
		setReminders();
		//FIXME show dialog confirmation
		startMainScreenActivity();
	}
	
	private void startMainScreenActivity() {
		Intent i = new Intent(PlansActivity.this, MainScreenActivity.class);
		startActivity(i);
	}
	
	private void setReminders() {
		int hour = 12; // FIXME change to settings in future version
		Calendar cal = Calendar.getInstance();
		
		for (Date date : workoutDays.keySet()) {
			cal.setTime(date);
			cal = Time.zeroTimeInDate(cal);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			Reminders.setRemainder(getApplicationContext(), cal.getTime());
		}
	}
	
	private void endTraining() {
		//FIXME show dialog are you sure
		Log.i("TAG", "ending this training");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(TreningPlans.TRENING_PLANS_IS_ENABLED_KEY, false);
		editor.commit();
		disableReminders();
		//FIXME show dialog confirmation
		finish();
	}
	
	private void disableReminders() {
		Reminders.cancelAllReminders(getApplicationContext());
	}
}
