package com.pwr.zpi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.database.entity.WorkoutActionAdvanced;
import com.pwr.zpi.utils.TimeFormatter;
import com.pwr.zpi.views.CustomPicker;

public class NewWorkoutActionAdvancedActivity extends Activity implements OnClickListener, TextWatcher {
	
	private static final String TAB_SPEC_1_TAG = "TabSpec1";
	private static final String TAB_SPEC_2_TAG = "TabSpec2";
	private static final String TAB_SPEC_3_TAG = "TabSpec3";
	private static final double MINUTES_IN_HOUR = 60;
	private static final double SECOND_IN_MINUTE = 60;
	private static final long MILINSECONDS_IN_HOUR = 3600000;
	private static final long MILINSECONDS_IN_MINUTE = 60000;
	private static final long MILINSECONDS_IN_SECOND = 1000;
	private TabHost tabHost;
	private final int TAB1 = 0;
	private final int TAB2 = 1;
	private final int TAB3 = 2;
	
	//Virtual Partner
	
	ScrollView scrollViewDistanceTime;
	ScrollView scrollViewDistancePace;
	ScrollView scrollViewTimePace;
	ScrollView scrollViewActual;
	
	//VP Action Tab1
	Button buttonTab1Add;
	CustomPicker pickerTab1DistanceKm;
	CustomPicker pickerTab1DistanceM;
	CustomPicker pickerTab1TimeHours;
	CustomPicker pickerTab1TimeMin;
	CustomPicker pickerTab1TimeSec;
	EditText editTextTab1Pace;
	double distanceTab1 = 0;
	long timeTab1 = 0;
	double paceTab1 = -1;
	
	//VP Action Tab2
	Button buttonTab2Add;
	CustomPicker pickerTab2DistanceKm;
	CustomPicker pickerTab2DistanceM;
	CustomPicker pickerTab2PaceMin;
	CustomPicker pickerTab2PaceSec;
	EditText editTextTab2Time;
	double distanceTab2 = 0;
	long timeTab2 = 0;
	double paceTab2 = 0;
	//VP Action Tab3
	Button buttonTab3Add;
	CustomPicker pickerTab3TimeHours;
	CustomPicker pickerTab3TimeMin;
	CustomPicker pickerTab3TimeSec;
	CustomPicker pickerTab3PaceMin;
	CustomPicker pickerTab3PaceSec;
	EditText editTextTab3Distance;
	double distanceTab3 = -1;
	long timeTab3 = 0;
	double paceTab3 = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_advanced_activity);
		
		initTabs();
		initFields();
		addListeners();
		setDataIfEdit();
	}
	
	private void initTabs()
	{
		tabHost = (TabHost) findViewById(R.id.tabhostActions);
		tabHost.setup();
		TabSpec tabSpecs = tabHost.newTabSpec(TAB_SPEC_1_TAG);
		tabSpecs.setContent(R.id.tab1DistanceTime);
		tabSpecs.setIndicator(getResources().getString(R.string.distansAndTime));
		tabHost.addTab(tabSpecs);
		tabSpecs = tabHost.newTabSpec(TAB_SPEC_2_TAG);
		tabSpecs.setContent(R.id.tab2DistancePace);
		tabSpecs.setIndicator(getResources().getString(R.string.distansAndPace));
		tabHost.addTab(tabSpecs);
		tabSpecs = tabHost.newTabSpec(TAB_SPEC_3_TAG);
		tabSpecs.setContent(R.id.tab3TimePace);
		tabSpecs.setIndicator(getResources().getString(R.string.timeAndPace));
		tabHost.addTab(tabSpecs);
		
	}
	
	private void initFields()
	{
		
		scrollViewDistanceTime = (ScrollView) findViewById(R.id.tab1DistanceTime);;
		scrollViewDistancePace = (ScrollView) findViewById(R.id.tab2DistancePace);
		scrollViewTimePace = (ScrollView) findViewById(R.id.tab3TimePace);
		scrollViewActual = scrollViewDistanceTime;
		
		//VP tab1
		pickerTab1DistanceKm = (CustomPicker) findViewById(R.id.customPickerTab1Km);
		pickerTab1DistanceM = (CustomPicker) findViewById(R.id.customPickerTab1M);
		pickerTab1TimeHours = (CustomPicker) findViewById(R.id.customPickerTab1TimeHour);
		pickerTab1TimeMin = (CustomPicker) findViewById(R.id.customPickerTab1TimeMin);
		pickerTab1TimeSec = (CustomPicker) findViewById(R.id.customPickerTab1TimeSec);
		buttonTab1Add = (Button) findViewById(R.id.buttonTab1Add);
		buttonTab1Add.setEnabled(false);
		editTextTab1Pace = (EditText) findViewById(R.id.editTextTab1Pace);
		//VP tab2
		pickerTab2DistanceKm = (CustomPicker) findViewById(R.id.customPickerTab2Km);
		pickerTab2DistanceM = (CustomPicker) findViewById(R.id.customPickerTab2M);
		pickerTab2PaceMin = (CustomPicker) findViewById(R.id.customPickerTab2PaceMin);
		pickerTab2PaceSec = (CustomPicker) findViewById(R.id.customPickerTab2PaceSec);
		buttonTab2Add = (Button) findViewById(R.id.buttonTab2Add);
		editTextTab2Time = (EditText) findViewById(R.id.editTextTab2Time);
		//VP tab3
		pickerTab3TimeHours = (CustomPicker) findViewById(R.id.customPickerTab3TimeHour);
		pickerTab3TimeMin = (CustomPicker) findViewById(R.id.customPickerTab3TimeMin);
		pickerTab3TimeSec = (CustomPicker) findViewById(R.id.customPickerTab3TimeSec);
		pickerTab3PaceMin = (CustomPicker) findViewById(R.id.customPickerTab3PaceMin);
		pickerTab3PaceSec = (CustomPicker) findViewById(R.id.customPickerTab3PaceSec);
		buttonTab3Add = (Button) findViewById(R.id.buttonTab3Add);
		buttonTab3Add.setEnabled(false);
		editTextTab3Distance = (EditText) findViewById(R.id.editTextTab3Distance);
	}
	
	private void addListeners()
	{
		buttonTab1Add.setOnClickListener(this);
		buttonTab2Add.setOnClickListener(this);
		buttonTab3Add.setOnClickListener(this);
		
		pickerTab1DistanceKm.getDisplayEditText().addTextChangedListener(this);
		pickerTab1DistanceM.getDisplayEditText().addTextChangedListener(this);
		pickerTab1TimeHours.getDisplayEditText().addTextChangedListener(this);
		pickerTab1TimeMin.getDisplayEditText().addTextChangedListener(this);
		pickerTab1TimeSec.getDisplayEditText().addTextChangedListener(this);
		pickerTab2DistanceKm.getDisplayEditText().addTextChangedListener(this);
		pickerTab2DistanceM.getDisplayEditText().addTextChangedListener(this);
		pickerTab2PaceMin.getDisplayEditText().addTextChangedListener(this);
		pickerTab2PaceSec.getDisplayEditText().addTextChangedListener(this);
		pickerTab3PaceMin.getDisplayEditText().addTextChangedListener(this);
		pickerTab3PaceSec.getDisplayEditText().addTextChangedListener(this);
		pickerTab3TimeHours.getDisplayEditText().addTextChangedListener(this);
		pickerTab3TimeMin.getDisplayEditText().addTextChangedListener(this);
		pickerTab3TimeSec.getDisplayEditText().addTextChangedListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		//if tab has changed
		switch (v.getId())
		{
		
			case R.id.buttonTab1Add:
				WorkoutActionAdvanced action = new WorkoutActionAdvanced(timeTab1, distanceTab1 * 1000);
				Intent returnIntent = new Intent();
				returnIntent.putExtra(WorkoutAction.TAG, action);
				setResult(RESULT_OK, returnIntent);
				finish();
				break;
			case R.id.buttonTab2Add:
				action = new WorkoutActionAdvanced(distanceTab2 * 1000, paceTab2);
				returnIntent = new Intent();
				returnIntent.putExtra(WorkoutAction.TAG, action);
				setResult(RESULT_OK, returnIntent);
				finish();
				break;
			case R.id.buttonTab3Add:
				action = new WorkoutActionAdvanced(paceTab3, timeTab3);
				returnIntent = new Intent();
				returnIntent.putExtra(WorkoutAction.TAG, action);
				setResult(RESULT_OK, returnIntent);
				finish();
				break;
		}
		
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		
		switch (tabHost.getCurrentTab())
		{
			case TAB1:
				double seconds = pickerTab1TimeSec.getValue();
				double distanceInKm = pickerTab1DistanceKm.getValue() + (double) (pickerTab1DistanceM.getValue())
					/ 1000;
				distanceTab1 = distanceInKm;
				double timeInMinutes = pickerTab1TimeHours.getValue() * MINUTES_IN_HOUR
					+ pickerTab1TimeMin.getValue() +
					seconds / SECOND_IN_MINUTE;
				timeTab1 = (long) (timeInMinutes * MILINSECONDS_IN_MINUTE);
				
				if (distanceInKm != 0)
				{
					buttonTab1Add.setEnabled(true);
					double pace = timeInMinutes / distanceInKm;
					//paceTab1 = pace;
					editTextTab1Pace.setText(TimeFormatter.formatTimeMMSSorHHMMSS(pace) + " min/km");
					
				}
				else {
					buttonTab1Add.setEnabled(false);
					editTextTab1Pace.setText(getResources().getString(R.string.dashes) + " min/km");
				}
				break;
			case TAB2:
				distanceInKm = pickerTab2DistanceKm.getValue() + (double) (pickerTab2DistanceM.getValue()) / 1000;
				distanceTab2 = distanceInKm;
				long paceInMilisecondsPerKm = pickerTab2PaceMin.getValue() * MILINSECONDS_IN_MINUTE
					+ pickerTab2PaceSec.getValue() * MILINSECONDS_IN_SECOND;
				paceTab2 = (double) paceInMilisecondsPerKm / MILINSECONDS_IN_MINUTE;
				long timeInMiliseconds = (long) (paceInMilisecondsPerKm * distanceInKm);
				//timeTab2 = timeInMiliseconds;
				editTextTab2Time.setText(TimeFormatter.formatTimeHHMMSS(timeInMiliseconds));
				break;
			case TAB3:
				seconds = pickerTab3PaceSec.getValue();
				double paceInMinutesPerKm = pickerTab3PaceMin.getValue() + seconds
					/ SECOND_IN_MINUTE;
				paceTab3 = paceInMinutesPerKm;
				seconds = pickerTab3TimeSec.getValue();
				timeInMinutes = pickerTab3TimeHours.getValue() * MINUTES_IN_HOUR
					+ pickerTab3TimeMin.getValue() +
					(seconds) / SECOND_IN_MINUTE;
				timeTab3 = (long) (timeInMinutes * MILINSECONDS_IN_MINUTE);
				if (paceInMinutesPerKm != 0)
				{
					buttonTab3Add.setEnabled(true);
					double distance = timeInMinutes / paceInMinutesPerKm;
					//distanceTab3 = distance;
					editTextTab3Distance.setText(String.format("%.3fkm", distance));
				}
				else {
					buttonTab3Add.setEnabled(false);
					editTextTab3Distance.setText(getResources().getString(R.string.dashes2));
				}
				break;
		}
	}
	
	//set privious data if we are editing view
	private void setDataIfEdit()
	{
		Intent intent = getIntent();
		if (intent.hasExtra(WorkoutAction.TAG))
		{
			WorkoutAction workoutAction = intent.getParcelableExtra(WorkoutAction.TAG);
			setTab(workoutAction);
		}
	}
	
	private void setTab(WorkoutAction action)
	{
		if (action.isAdvanced())
		{
			setVirtualPartnerTab((WorkoutActionAdvanced) action);
		}
		
	}
	
	private void setVirtualPartnerTab(WorkoutActionAdvanced actionAdvanced)
	{
		tabHost.setCurrentTab(1);
		switch (actionAdvanced.getType())
		{
		
			case WorkoutAction.ACTION_ADVANCED_TYPE_TIME_DISTANCE:
				tabHost.setCurrentTab(TAB1);
				setDistance(actionAdvanced.getDistance(), pickerTab1DistanceKm, pickerTab1DistanceM);
				setTime(actionAdvanced.getTime(), pickerTab1TimeHours, pickerTab1TimeMin, pickerTab1TimeSec);
				editTextTab1Pace.setText(TimeFormatter.formatTimeMMSSorHHMMSS(actionAdvanced.getPace()));
				break;
			case WorkoutAction.ACTION_ADVANCED_TYPE_DISTANCE_PACE:
				tabHost.setCurrentTab(TAB2);
				setDistance(actionAdvanced.getDistance(), pickerTab2DistanceKm, pickerTab2DistanceM);
				setPace(actionAdvanced.getPace(), pickerTab2PaceMin, pickerTab2PaceSec);
				editTextTab2Time.setText(TimeFormatter.formatTimeHHMMSS(actionAdvanced.getTime()));
				break;
			case WorkoutAction.ACTION_ADVANCED_TYPE_PACE_TIME:
				tabHost.setCurrentTab(TAB3);
				setTime(actionAdvanced.getTime(), pickerTab3TimeHours, pickerTab3TimeMin, pickerTab3TimeSec);
				setPace(actionAdvanced.getPace(), pickerTab3PaceMin, pickerTab3PaceSec);
				editTextTab3Distance.setText(String.format("%.3f", actionAdvanced.getDistance() / 1000));
				break;
		
		}
		
	}
	
	private void setTime(long time, CustomPicker pickerHour, CustomPicker pickerMinute, CustomPicker pickerSecond)
	{
		int hours = (int) (time / MILINSECONDS_IN_HOUR);
		int min = (int) ((time % MILINSECONDS_IN_HOUR) / MILINSECONDS_IN_MINUTE);
		int sec = (int) Math.round(((double) (time % MILINSECONDS_IN_MINUTE) / MILINSECONDS_IN_SECOND));
		pickerHour.setValue(hours);
		pickerMinute.setValue(min);
		pickerSecond.setValue(sec);
	}
	
	private void setDistance(double distance, CustomPicker pickerKm, CustomPicker pickerM)
	{
		int km = (int) (distance / 1000);
		int m = (int) Math.round(distance % 1000);
		pickerKm.setValue(km);
		pickerM.setValue(m);
	}
	
	private void setPace(double pace, CustomPicker pickerMinute, CustomPicker pickerSecond)
	{
		
		int min = (int) (pace);
		int sec = (int) Math.round(((pace - (int) pace) * 60));
		pickerMinute.setValue(min);
		pickerSecond.setValue(sec);
		
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
}
