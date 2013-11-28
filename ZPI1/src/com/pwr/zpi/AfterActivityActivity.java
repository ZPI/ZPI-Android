package com.pwr.zpi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pwr.zpi.dialogs.MyDialog;
import com.pwr.zpi.utils.Reminders;
import com.pwr.zpi.utils.Time;
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
	
	private void initFields() {
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
	
	private void addListeners() {
		buttonDiscard.setOnClickListener(this);
		buttonSave.setOnClickListener(this);
		reminderBt.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		Intent returnIntent = new Intent();
		switch (v.getId()) {
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
				showReminderDialog();
				break;
		}
		
	}
	
	AlertDialog dayDialog;
	AlertDialog hourDialog;
	int plusDay;
	int hour;
	
	private void showReminderDialog() {
		CharSequence[] items = getResources().getStringArray(R.array.reminder_days);
		MyDialog dialog = new MyDialog();
		DialogInterface.OnClickListener itemsHandler = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						Reminders.cancelAllReminders(AfterActivityActivity.this.getApplicationContext());
						Toast.makeText(AfterActivityActivity.this, getString(R.string.reminder_disable),
							Toast.LENGTH_SHORT).show();
						break;
					case 1:
					case 2:
					case 3:
						plusDay = which;
						startHourDialog();
						break;
					default:
						break;
				}
			}
		};
		dayDialog = dialog.showAlertDialog(this, R.string.reminder_dialog_title, R.string.empty_string,
			R.string.empty_string, android.R.string.cancel, null, null, items, itemsHandler);
	}
	
	private void startHourDialog() {
		CharSequence[] items = getResources().getStringArray(R.array.reminder_hours);
		MyDialog dialog = new MyDialog();
		DialogInterface.OnClickListener itemsHandler = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						hour = 6;
						break;
					case 1:
						hour = 8;
						break;
					case 2:
						hour = 16;
						break;
					case 3:
						hour = 20;
						break;
					default:
						Reminders.cancelAllReminders(getApplicationContext());
						return; //by intention
				}
				Date reminderDate = setReminderForSelectedDay(plusDay, hour);
				Toast.makeText(
					AfterActivityActivity.this,
					getString(R.string.reminder_set) + " "
						+ new SimpleDateFormat("HH:mm dd-MM-yyyy").format(reminderDate), Toast.LENGTH_LONG).show();
			}
			
		};
		dayDialog = dialog.showAlertDialog(this, R.string.reminder_dialog_title, R.string.empty_string,
			R.string.empty_string, android.R.string.cancel, null, null, items, itemsHandler);
	}
	
	private Date setReminderForSelectedDay(int plusDay, int hour) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, plusDay);
		Date reminderDate = Time.getDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
			cal.get(Calendar.DAY_OF_MONTH), hour, 0, 0);
		Reminders.setRemainder(getApplicationContext(), reminderDate);
		return reminderDate;
	}
}
