package com.pwr.zpi;

import android.app.Activity;
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

import com.pwr.zpi.utils.TimeFormatter;
import com.pwr.zpi.views.CustomPicker;

public class NewWorkoutActionActivity extends Activity implements OnClickListener, TextWatcher {
	
	private static final String TAB_SPEC_1_TAG = "TabSpec1";
	private static final String TAB_SPEC_2_TAG = "TabSpec2";
	private static final double MINUTES_IN_HOUR = 60;
	private static final double SECOND_IN_MINUTE = 60;
	private static final long MILINSECONDS_IN_MINUTE = 60000;
	private static final long MILINSECONDS_IN_SECOND = 1000;
	
	//Interval Action
	
	//Virtual Partner
	Button buttonDistanceTime;
	Button buttonDistancePace;
	Button buttonTimePace;
	Button buttonActual;	//contains an instance of one of the 3 other buttons
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
	
	//VP Action Tab2
	Button buttonTab2Add;
	CustomPicker pickerTab2DistanceKm;
	CustomPicker pickerTab2DistanceM;
	CustomPicker pickerTab2PaceMin;
	CustomPicker pickerTab2PaceSec;
	EditText editTextTab2Time;
	//VP Action Tab3
	Button buttonTab3Add;
	CustomPicker pickerTab3TimeHours;
	CustomPicker pickerTab3TimeMin;
	CustomPicker pickerTab3TimeSec;
	CustomPicker pickerTab3PaceMin;
	CustomPicker pickerTab3PaceSec;
	EditText editTextTab3Distance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_activity);
		
		initTabs();
		initFields();
		addListeners();
	}
	
	private void initTabs()
	{
		TabHost tabHost = (TabHost) findViewById(R.id.tabhostActions);
		tabHost.setup();
		TabSpec tabSpecs = tabHost.newTabSpec(TAB_SPEC_1_TAG);
		tabSpecs.setContent(R.id.tab1IntervalAction);
		tabSpecs.setIndicator(getResources().getString(R.string.interval));
		tabHost.addTab(tabSpecs);
		tabSpecs = tabHost.newTabSpec(TAB_SPEC_2_TAG);
		tabSpecs.setContent(R.id.tab2VirtualPartner);
		tabSpecs.setIndicator(getResources().getString(R.string.virtual_partner));
		tabHost.addTab(tabSpecs);
	}
	
	private void initFields()
	{
		
		//Virtual partner
		buttonDistanceTime = (Button) findViewById(R.id.tabButtonDistanceTime);
		buttonDistancePace = (Button) findViewById(R.id.tabButtonDistancePace);
		buttonTimePace = (Button) findViewById(R.id.tabButtonTimePace);
		buttonActual = buttonDistanceTime;
		
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
		editTextTab3Distance = (EditText) findViewById(R.id.editTextTab3Distance);
	}
	
	private void addListeners()
	{
		buttonDistanceTime.setOnClickListener(this);
		buttonDistancePace.setOnClickListener(this);
		buttonTimePace.setOnClickListener(this);
		
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
		if (buttonActual != v)
		{
			
			if (v == buttonDistanceTime)
			{
				buttonActual.setBackgroundColor(getResources().getColor(R.color.gray_color));
				scrollViewActual.setVisibility(View.GONE);
				buttonDistanceTime.setBackgroundColor(getResources().getColor(R.color.list_transparent_color));
				scrollViewDistanceTime.setVisibility(View.VISIBLE);
				buttonActual = buttonDistanceTime;
				scrollViewActual = scrollViewDistanceTime;
			}
			else if (v == buttonDistancePace)
			{
				buttonActual.setBackgroundColor(getResources().getColor(R.color.gray_color));
				scrollViewActual.setVisibility(View.GONE);
				buttonDistancePace.setBackgroundColor(getResources().getColor(R.color.list_transparent_color));
				scrollViewDistancePace.setVisibility(View.VISIBLE);
				buttonActual = buttonDistancePace;
				scrollViewActual = scrollViewDistancePace;
			}
			else if (v == buttonTimePace)
			{
				buttonActual.setBackgroundColor(getResources().getColor(R.color.gray_color));
				scrollViewActual.setVisibility(View.GONE);
				buttonTimePace.setBackgroundColor(getResources().getColor(R.color.list_transparent_color));
				scrollViewTimePace.setVisibility(View.VISIBLE);
				buttonActual = buttonTimePace;
				scrollViewActual = scrollViewTimePace;
			}
		}
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		//tab1
		if (buttonActual == buttonDistanceTime)
		{
			double seconds = pickerTab1TimeSec.getValue();
			double distanceInKm = pickerTab1DistanceKm.getValue() + (double) (pickerTab1DistanceM.getValue()) / 1000;
			double timeInMinutes = pickerTab1TimeHours.getValue() * MINUTES_IN_HOUR
				+ pickerTab1TimeMin.getValue() +
				seconds / SECOND_IN_MINUTE;
			
			if (distanceInKm != 0)
			{
				double pace = timeInMinutes / distanceInKm;
				
				editTextTab1Pace.setText(TimeFormatter.formatTimeMMSSorHHMMSS(pace) + " min/km");
				
			}
			else {
				editTextTab1Pace.setText(getResources().getString(R.string.dashes) + " min/km");
			}
			
		}
		//tab2
		else if (buttonActual == buttonDistancePace)
		{
			double distanceInKm = pickerTab2DistanceKm.getValue() + (double) (pickerTab2DistanceM.getValue()) / 1000;
			long paceInMilisecondsPerKm = pickerTab2PaceMin.getValue() * MILINSECONDS_IN_MINUTE
				+ pickerTab2PaceSec.getValue() * MILINSECONDS_IN_SECOND;
			long timeInMiliseconds = (long) (paceInMilisecondsPerKm * distanceInKm);
			
			editTextTab2Time.setText(TimeFormatter.formatTimeHHMMSS(timeInMiliseconds));
		}
		//tab3
		else if (buttonActual == buttonTimePace)
		{
			double seconds = pickerTab3PaceSec.getValue();
			double paceInMinutesPerKm = pickerTab3PaceMin.getValue() + seconds
				/ SECOND_IN_MINUTE;
			seconds = pickerTab3TimeSec.getValue();
			double timeInMinutes = pickerTab3TimeHours.getValue() * MINUTES_IN_HOUR
				+ pickerTab3TimeMin.getValue() +
				(seconds) / SECOND_IN_MINUTE;
			
			if (paceInMinutesPerKm != 0)
			{
				double distance = timeInMinutes / paceInMinutesPerKm;
				editTextTab3Distance.setText(String.format("%.3fkm", distance));
			}
			else {
				editTextTab3Distance.setText(getResources().getString(R.string.dashes2));
			}
		}
		
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
