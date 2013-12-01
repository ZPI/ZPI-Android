package com.pwr.zpi;

import java.util.Calendar;
import java.util.Date;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.pwr.zpi.listeners.GestureListener;
import com.pwr.zpi.listeners.MyGestureDetector;
import com.pwr.zpi.mock.TreningPlans;
import com.pwr.zpi.utils.Reminders;
import com.pwr.zpi.utils.Time;

public class SettingsActivity extends PreferenceActivity implements GestureListener, OnSharedPreferenceChangeListener {
	
	GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		
		addPreferencesFromResource(R.xml.settings);
		
		prepareGestureListener();
		addListeners();
		
		disableReminderIfPlanSet();
	}
	
	private void disableReminderIfPlanSet() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isTreningPlan = prefs.getBoolean(TreningPlans.TRENING_PLANS_IS_ENABLED_KEY, false);
		if (isTreningPlan) {
			findPreference(getString(R.string.reminder_day_key)).setEnabled(false);
			findPreference(getString(R.string.reminder_hour_key)).setEnabled(false);
		}
		else {
			findPreference(getString(R.string.reminder_day_key)).setEnabled(true);
			setHourPreferencesIfDayIsSet(prefs, getString(R.string.reminder_day_key));
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_down_anim, R.anim.out_down_anim);
	}
	
	private void prepareGestureListener() {
		// Gesture detection
		gestureDetector = new GestureDetector(this, new MyGestureDetector(this, false, true, false, false));
		gestureListener = new View.OnTouchListener() {
			@Override
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
	public void onLeftToRightSwipe() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onRightToLeftSwipe() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onUpToDownSwipe() {
		
		finish();
		overridePendingTransition(R.anim.in_down_anim, R.anim.out_down_anim);
	}
	
	@Override
	public void onDownToUpSwipe() {
		// TODO Auto-generated method stub
		
	}
	
	private void addListeners() {
		getListView().setOnTouchListener(gestureListener);
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSingleTapConfirmed(MotionEvent e) {
		
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		Log.e("DDD", "changed");
		if (key.equals(getString(R.string.reminder_day_key))) {
			setHourPreferencesIfDayIsSet(preferences, key);
			setReminder(preferences);
		}
		else if (key.equals(getString(R.string.reminder_hour_key))) {
			setReminder(preferences);
		}
	}
	
	private void setHourPreferencesIfDayIsSet(SharedPreferences preferences, String key) {
		if (preferences.getString(key, "0").equals("0")) {
			Log.e("DDD", "disable");
			findPreference(getString(R.string.reminder_hour_key)).setEnabled(false);
		}
		else {
			Log.e("DDD", "enable");
			findPreference(getString(R.string.reminder_hour_key)).setEnabled(true);
		}
	}
	
	private void setReminder(SharedPreferences preferences) {
		int days = Integer.parseInt(preferences.getString(getString(R.string.reminder_day_key), "0"));
		int hour = Integer.parseInt(preferences.getString(getString(R.string.reminder_hour_key), "0"));
		
		Reminders.cancelAllReminders(getApplicationContext());
		if (days != 0) {
			Reminders.cancelAllReminders(getApplicationContext());
			Calendar cal = Calendar.getInstance();
			Date startDate = Time.getDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
				cal.get(Calendar.DAY_OF_MONTH) + days, hour, 0, 0);
			long dayInMilis = 24 * 60 * 60 * 1000; // one day
			Reminders.setRemainderEvery(getApplicationContext(), startDate, dayInMilis);
		}
	}
}