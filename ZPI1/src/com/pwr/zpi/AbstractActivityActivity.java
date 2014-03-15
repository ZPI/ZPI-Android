package com.pwr.zpi;

import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.dialogs.MyDialog;
import com.pwr.zpi.utils.TimeFormatter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

public abstract class AbstractActivityActivity extends FragmentActivity {

	public static final float MIN_SPEED_FOR_AUTO_PAUSE = 0.7f;
	// measured values IDs
	protected static final int distanceID = 0;
	protected static final int paceID = 1;
	protected static final int avgPaceID = 2;
	protected static final int timeID = 3;
	protected static final int lastKmPaceID = 4;
	protected static final String TAG = null;

	// views
	protected TextView clickedContentTextView;
	protected TextView clickedLabelTextView;
	protected TextView clickedUnitTextView;

	// diplay data changing
	protected int dataTextView1Content;
	protected int dataTextView2Content;
	protected int clickedField;

	protected boolean isPaused;
	protected boolean pausedManually;

	protected void showMeassuredValuesMenu(final double distance,
			final long time, final double pace, final double avgPace) {
		// chcia�em zrobi� tablice w stringach, ale potem zobaczy�em, �e
		// ju� mam
		// te wszystkie nazwy i teraz nie wiem czy tamto zmienia� w tablic�
		// czy
		// nie ma sensu
		// kolejno�� w tablicy musi odpowiada� nr ID, tzn 0 - dystans itp.

		final CharSequence[] items = { getMyString(R.string.distance),
				getMyString(R.string.pace), getMyString(R.string.pace_avrage),
				getMyString(R.string.time) };
		DialogInterface.OnClickListener itemsHandler = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				updateLabels(item, clickedLabelTextView, clickedUnitTextView,
						clickedContentTextView, distance, time, pace, avgPace);
				if (clickedField == 1) {
					dataTextView1Content = item;
				} else {
					dataTextView2Content = item;
				}
			}
		};
		MyDialog.showAlertDialog(this, R.string.dialog_choose_what_to_display,
				R.string.empty_string, R.string.empty_string,
				R.string.empty_string, null, null, items, itemsHandler);
	}

	protected String getMyString(int stringId) {
		return getResources().getString(stringId);
	}

	protected void initLabels(TextView textViewInitialValue, TextView textView,
			int meassuredValue) {
		switch (meassuredValue) {
		case distanceID:
			textView.setText(R.string.distance);
			textViewInitialValue.setText("0.000");
			break;
		case paceID:
			textView.setText(R.string.pace);
			textViewInitialValue.setText("0:00");
			break;
		case avgPaceID:
			textView.setText(R.string.pace_avrage);
			textViewInitialValue.setText("0:00");
			break;
		case timeID:
			textView.setText(R.string.time);
			textViewInitialValue.setText("00:00:00");
			break;
		}

	}

	private void updateLabels(int meassuredValue, TextView labelTextView,
			TextView unitTextView, TextView contentTextView, double distance,
			long time, double pace, double avgPace) {
		switch (meassuredValue) {
		case distanceID:
			labelTextView.setText(R.string.distance);
			unitTextView.setText(R.string.km);
			contentTextView.setText(String.format("%.3f", distance / 1000));
			break;
		case paceID:
			labelTextView.setText(R.string.pace);
			unitTextView.setText(R.string.minutes_per_km);
			if (pace < 30) {
				contentTextView.setText(TimeFormatter
						.formatTimeMMSSorHHMMSS(pace));
			} else {
				contentTextView.setText(getResources().getString(
						R.string.dashes));
			}
			break;
		case avgPaceID:
			labelTextView.setText(R.string.pace_avrage);
			unitTextView.setText(R.string.minutes_per_km);
			if (avgPace < 30) {
				contentTextView.setText(TimeFormatter
						.formatTimeMMSSorHHMMSS(avgPace));
			} else {
				contentTextView.setText(getResources().getString(
						R.string.dashes));
			}
			break;
		case timeID:
			labelTextView.setText(R.string.time);
			unitTextView.setText(R.string.empty_string);
			contentTextView.setText(TimeFormatter.formatTimeHHMMSS(time));
			break;
		}

	}

	// update display
	protected void updateData(final TextView textBox, final int meassuredValue,
			double distance, double pace, long time, double avgPace) {

		switch (meassuredValue) {
		case distanceID:
			textBox.setText(String.format("%.3f", distance / 1000));
			break;
		case paceID:
			if (pace < 30) {
				textBox.setText(TimeFormatter.formatTimeMMSSorHHMMSS(pace));
			} else {
				textBox.setText(getResources().getString(R.string.dashes));
			}
			break;
		case avgPaceID:
			if (avgPace < 30) {
				textBox.setText(TimeFormatter.formatTimeMMSSorHHMMSS(avgPace));
			} else {
				textBox.setText(getResources().getString(R.string.dashes));
			}
			break;
		case timeID:
			textBox.setText(TimeFormatter.formatTimeHHMMSS(time));
			break;
		}
	}

	protected abstract void pauseRun();

	protected abstract void resumeRun();

	protected abstract void updateGpsInfo(final Location newLocation);

	protected abstract void handleTimeUpdates();

	protected Workout getWorkoutData(Intent i) {

		Workout workout;
		workout = i.getParcelableExtra(Workout.TAG);
		return workout;
	}

	protected void showAlertDialog(final boolean isServiceConnected,
			final RunListenerApi api, final Context context,
			final double distance, final long time, final int runNumber) {
		DialogInterface.OnClickListener positiveButtonHandler = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				try {
					if (isServiceConnected) {
						api.setStoped();
					}
				} catch (RemoteException e) {
					Log.e(TAG, "Failed to tell that activity is stoped", e);
				}
				Intent intent = new Intent(context, AfterActivityActivity.class);
				intent.putExtra(ActivityActivity.DURATION_TAG, time);
				intent.putExtra(ActivityActivity.DISTANCE_TAG, distance);
				double pace = ((double) time / 60) / distance;
				intent.putExtra(ActivityActivity.AVG_PACE_TAG, pace);
				intent.putExtra(ActivityActivity.RUN_NUMBER_TAG, runNumber);

				startActivityForResult(intent, ActivityActivity.MY_REQUEST_CODE);
			}
		};
		MyDialog.showAlertDialog(this, R.string.dialog_message_on_stop,
				R.string.empty_string, android.R.string.yes,
				android.R.string.no, positiveButtonHandler, null);
	}

}
