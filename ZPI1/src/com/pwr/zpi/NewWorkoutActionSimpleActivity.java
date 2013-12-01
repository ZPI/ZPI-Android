package com.pwr.zpi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.database.entity.WorkoutActionSimple;
import com.pwr.zpi.views.CustomPicker;
import com.pwr.zpi.views.TopBar;

public class NewWorkoutActionSimpleActivity extends Activity implements OnClickListener {
	
	private static final long MILINSECONDS_IN_HOUR = 3600000;
	private static final long MILINSECONDS_IN_MINUTE = 60000;
	private static final long MILINSECONDS_IN_SECOND = 1000;
	private TabHost tabHost;
	
	//Interval Action
	Button buttonActionSpeedTypeSlow;
	Button buttonActionSpeedTypeSteady;
	Button buttonActionSpeedTypeFast;
	Button buttonActionTypeTime;
	Button buttonActionTypeDistance;
	Button buttonActionTypeActual;
	Button buttonSpeedTypeActual;
	Button buttonAddInterval;
	CustomPicker pickerHours;
	CustomPicker pickerMinutes;
	CustomPicker pickerSec;
	CustomPicker pickerKm;
	CustomPicker pickerM;
	LinearLayout timeChooser;
	LinearLayout distanceChooser;
	private RelativeLayout leftButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_simple_activity);
		
		initFields();
		addListeners();
		setDataIfEdit();
	}
	
	private void initFields()
	{
		//interval action
		buttonActionSpeedTypeSteady = (Button) findViewById(R.id.buttonSteady);
		buttonActionSpeedTypeFast = (Button) findViewById(R.id.buttonFast);
		buttonActionSpeedTypeSlow = (Button) findViewById(R.id.buttonSlow);
		buttonActionTypeDistance = (Button) findViewById(R.id.buttonDistance);
		buttonActionTypeTime = (Button) findViewById(R.id.buttonTime);
		buttonSpeedTypeActual = buttonActionSpeedTypeSlow;
		buttonActionTypeActual = buttonActionTypeTime;
		buttonActionTypeActual.setSelected(true);
		buttonSpeedTypeActual.setSelected(true);
		buttonAddInterval = (Button) findViewById(R.id.buttonAddIntervalWorkout);
		pickerHours = (CustomPicker) findViewById(R.id.customPickerIntervalHourPicker);
		pickerMinutes = (CustomPicker) findViewById(R.id.customPickerIntervalMinutePicker);
		pickerSec = (CustomPicker) findViewById(R.id.customPickerIntervalSecPicker);
		pickerKm = (CustomPicker) findViewById(R.id.customPickerIntervalKmPicker);
		pickerM = (CustomPicker) findViewById(R.id.customPickerIntervalMPicker);
		distanceChooser = (LinearLayout) findViewById(R.id.linearLayoutIntervalDistanceChooser);
		timeChooser = (LinearLayout) findViewById(R.id.linearLayoutIntervalTimeChooser);
		TopBar topBar = (TopBar) findViewById(R.id.topBarActionSimple);
		leftButton = topBar.getLeftButton();
	}
	
	private void addListeners()
	{
		
		buttonActionSpeedTypeSlow.setOnClickListener(this);
		buttonActionSpeedTypeSteady.setOnClickListener(this);
		buttonActionSpeedTypeFast.setOnClickListener(this);
		buttonActionTypeDistance.setOnClickListener(this);
		buttonActionTypeTime.setOnClickListener(this);
		buttonAddInterval.setOnClickListener(this);
		leftButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		//if tab has changed
		if (buttonActionTypeActual != v && buttonSpeedTypeActual != v)
		{
			switch (v.getId())
			{
				case R.id.buttonSlow:
					buttonSpeedTypeActual.setSelected(false);
					buttonActionSpeedTypeSlow.setSelected(true);
					buttonSpeedTypeActual = buttonActionSpeedTypeSlow;
					break;
				case R.id.buttonSteady:
					buttonSpeedTypeActual.setSelected(false);
					buttonActionSpeedTypeSteady.setSelected(true);
					buttonSpeedTypeActual = buttonActionSpeedTypeSteady;
					break;
				case R.id.buttonFast:
					buttonSpeedTypeActual.setSelected(false);
					buttonActionSpeedTypeFast.setSelected(true);
					buttonSpeedTypeActual = buttonActionSpeedTypeFast;
					break;
				case R.id.buttonDistance:
					setIntervalValueType(WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE);
					break;
				case R.id.buttonTime:
					setIntervalValueType(WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME);
					break;
				case R.id.buttonAddIntervalWorkout:
					int speedType = 0;
					int valueType = 0;
					double value = 0;
					switch (buttonActionTypeActual.getId())
					{
						case R.id.buttonDistance:
							valueType = WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE;
							value = pickerKm.getValue() * 1000 + (pickerM.getValue());
							break;
						case R.id.buttonTime:
							valueType = WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME;
							value = pickerHours.getValue() * MILINSECONDS_IN_HOUR + pickerMinutes.getValue()
								* MILINSECONDS_IN_MINUTE + pickerSec.getValue() * MILINSECONDS_IN_SECOND;
							
							break;
					}
					
					switch (buttonSpeedTypeActual.getId())
					{
						case R.id.buttonSlow:
							speedType = WorkoutAction.ACTION_SIMPLE_SPEED_SLOW;
							break;
						case R.id.buttonSteady:
							speedType = WorkoutAction.ACTION_SIMPLE_SPEED_STEADY;
							break;
						case R.id.buttonFast:
							speedType = WorkoutAction.ACTION_SIMPLE_SPEED_FAST;
							break;
					}
					
					WorkoutActionSimple actionSimple = new WorkoutActionSimple(speedType, valueType, value);
					Intent returnIntent = new Intent();
					returnIntent.putExtra(WorkoutAction.TAG, actionSimple);
					setResult(RESULT_OK, returnIntent);
					finish();
					break;
				default:
					if (v == leftButton) {
						finish();
					}
					break;
			}
			
		}
	}
	
	private void setIntervalValueType(int type)
	{
		switch (type)
		{
			case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE:
				buttonActionTypeActual.setSelected(false);
				buttonActionTypeDistance.setSelected(true);
				buttonActionTypeActual = buttonActionTypeDistance;
				distanceChooser.setVisibility(View.VISIBLE);
				timeChooser.setVisibility(View.GONE);
				break;
			case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME:
				buttonActionTypeActual.setSelected(false);
				buttonActionTypeTime.setSelected(true);
				buttonActionTypeActual = buttonActionTypeTime;
				distanceChooser.setVisibility(View.GONE);
				timeChooser.setVisibility(View.VISIBLE);
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
		if (action.isSimple())
		{
			setIntervalTab((WorkoutActionSimple) action);
		}
		
	}
	
	private void setIntervalTab(WorkoutActionSimple actionSimple)
	{
		buttonSpeedTypeActual.setSelected(false);
		switch (actionSimple.getSpeedType())
		{
			case WorkoutAction.ACTION_SIMPLE_SPEED_SLOW:
				buttonSpeedTypeActual = buttonActionSpeedTypeSlow;
				break;
			case WorkoutAction.ACTION_SIMPLE_SPEED_STEADY:
				buttonSpeedTypeActual = buttonActionSpeedTypeSteady;
				break;
			case WorkoutAction.ACTION_SIMPLE_SPEED_FAST:
				buttonSpeedTypeActual = buttonActionSpeedTypeFast;
				break;
		}
		buttonSpeedTypeActual.setSelected(true);
		switch (actionSimple.getValueType())
		{
			case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE:
				setIntervalValueType(WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE);
				double distance = actionSimple.getValue();
				setDistance(distance, pickerKm, pickerM);
				break;
			case WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME:
				setIntervalValueType(WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME);
				long time = (long) actionSimple.getValue();
				setTime(time, pickerHours, pickerMinutes, pickerSec);
				break;
		}
		buttonAddInterval.setText(R.string.edit_action);
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
}
