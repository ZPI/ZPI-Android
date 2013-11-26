package com.pwr.zpi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pwr.zpi.utils.TimeFormatter;

public class AfterActivityActivity extends Activity implements OnClickListener {
	
	private Button buttonSave;
	private Button buttonDiscard;
	private EditText runName;
	private TextView avgPaceTV;
	private TextView avgSpeedTV;
	private TextView distanceTV;
	private TextView durationTV;
	private Button reminderBt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.after_activity_activity);
		initFields();
		addListeners();
		super.onCreate(savedInstanceState);
	}
	
	private void initFields()
	{
		buttonDiscard = (Button) findViewById(R.id.buttonAfterActivityDiscard);
		buttonSave = (Button) findViewById(R.id.buttonAfterActivitySave);
		runName = (EditText) findViewById(R.id.editTextAfterActivityRunName);
		avgPaceTV = (TextView) findViewById(R.id.textViewAfterActivityAvgPaceValue);
		avgSpeedTV = (TextView) findViewById(R.id.textViewAfterActivityAvgSpeedValue);
		distanceTV = (TextView) findViewById(R.id.textViewAfterActivityDistanceValue);
		durationTV = (TextView) findViewById(R.id.textViewAfterActivityDurationValue);
		reminderBt = (Button) findViewById(R.id.buttonAfterActivityRemind);
		
		Intent intent = getIntent();
		double pace = intent.getDoubleExtra(ActivityActivity.AVG_PACE_TAG, 0);
		double speed = 60 / pace;
		long duration = intent.getLongExtra(ActivityActivity.DURATION_TAG, 0);
		double distance = intent.getDoubleExtra(ActivityActivity.DISTANCE_TAG, 0) / 1000;
		int runNumber = intent.getIntExtra(ActivityActivity.RUN_NUMBER_TAG, 0);
		avgPaceTV.setText((pace > 300) ? getResources().getString(R.string.dashes) : TimeFormatter
			.formatTimeMMSSorHHMMSS(pace) + "min/km");
		avgSpeedTV.setText(String.format("%.2fkm/h", speed));
		distanceTV.setText(String.format("%.2fkm", distance));
		durationTV.setText(TimeFormatter.formatTimeHHMMSS(duration));
		runName.setText(getResources().getString(R.string.run) + (runNumber + 1));
	}
	
	private void addListeners()
	{
		buttonDiscard.setOnClickListener(this);
		buttonSave.setOnClickListener(this);
		reminderBt.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		Intent returnIntent = new Intent();
		switch (v.getId())
		{
			case R.id.buttonAfterActivitySave:
				returnIntent.putExtra(ActivityActivity.SAVE_TAG, true);
				setResult(RESULT_OK, returnIntent);
				returnIntent.putExtra(ActivityActivity.NAME_TAG, runName.getText().toString());
				finish();
				break;
			case R.id.buttonAfterActivityDiscard:
				returnIntent.putExtra(ActivityActivity.SAVE_TAG, false);
				setResult(RESULT_OK, returnIntent);
				finish();
				break;
			case R.id.buttonAfterActivityRemind:
				//TODO
				break;
		}
		
	}
}
