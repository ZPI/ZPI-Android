package com.pwr.zpi;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pwr.zpi.dialogs.ErrorDialogFragment;
import com.pwr.zpi.listeners.MyLocationListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityActivity extends FragmentActivity implements
		OnClickListener{

	private GoogleMap mMap;
	private Button stopButton;
	private Button pauseButton;
	private Button resumeButton;

	private TextView DataTextView1;
	private TextView DataTextView2;
	private TextView LabelTextView1;
	private TextView LabelTextView2;
	private boolean isPaused;
	MyLocationListener myLocationListener;
	
	//private LinkedList<LinkedList<Location>> trace;
	//private PolylineOptions traceOnMap;
	//private LocationRequest mLocationRequest;
	//private static final long LOCATION_UPDATE_FREQUENCY = 1000;

	// measured values
	double pace;
	double avgPace;
	double distance;
	long time;
	long startTime;
	long pauseTime;
	long pauseStartTime;

	private int dataTextView1Content;
	private int dataTextView2Content;

	// measured values IDs
	private static final int distanceID = 1;
	private static final int paceID = 2;
	private static final int avgPaceID = 3;
	private static final int timeID = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);

		stopButton = (Button) findViewById(R.id.stopButton);
		pauseButton = (Button) findViewById(R.id.pauseButton);
		resumeButton = (Button) findViewById(R.id.resumeButton);
		
		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		//trace = new LinkedList<LinkedList<Location>>();
		stopButton.setOnClickListener(this);
		resumeButton.setOnClickListener(this);
		pauseButton.setOnClickListener(this);
		pauseTime = 0;
		//traceOnMap = new PolylineOptions();

		DataTextView1 = (TextView) findViewById(R.id.dataTextView1);
		DataTextView2 = (TextView) findViewById(R.id.dataTextView2);

		LabelTextView1 = (TextView) findViewById(R.id.dataTextView1Discription);
		LabelTextView2 = (TextView) findViewById(R.id.dataTextView2Discription);
		
		//to change displayed info, change dataTextViewContent and start initLabelsMethod
		dataTextView1Content = distanceID;
		dataTextView2Content = timeID;
		
		initLabels(DataTextView1,LabelTextView1, dataTextView1Content);
		initLabels(DataTextView2,LabelTextView2, dataTextView2Content);
		
		//TODO pobraæ z intencji zamiast tak
		myLocationListener = MainScreenActivity.locationListener;
		myLocationListener.start(this);
		startTime = System.currentTimeMillis();
	}

	private void initLabels(TextView textViewInitialValue, TextView textView, int meassuredValue)
	{
		if (meassuredValue == distanceID) {
			textView.setText(R.string.distance);
			textViewInitialValue.setText("0.000");
		} else if (meassuredValue == paceID) {
			textView.setText(R.string.pace);
			textViewInitialValue.setText("0:00");
		} else if (meassuredValue == avgPaceID) {
			textView.setText(R.string.pace_avrage);
			textViewInitialValue.setText("0:00");
		}
		else if (meassuredValue == timeID) {
			textView.setText(R.string.time);
			textViewInitialValue.setText("00:00:00");
		}
	}
	



	@Override
	public void onBackPressed() {
		super.onBackPressed();
		showAlertDialog();
	}
	
	private void showAlertDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Add the buttons
		builder.setMessage(R.string.dialog_message_on_stop);
		builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   finish();
		   			overridePendingTransition(R.anim.in_up_anim, R.anim.out_up_anim);
		           }
		       });
		builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
		           }
		       });
		// Set other dialog properties

		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();
		
	}

	@Override
	public void onClick(View v) {
		if (v == stopButton) {
			// TODO finish and save activity
			showAlertDialog();
		}
		else if (v == pauseButton)
		{
			myLocationListener.setPaused(!myLocationListener.isPaused());
			isPaused = myLocationListener.isPaused();
			if (isPaused)
			{
				stopButton.setVisibility(View.GONE);
				pauseButton.setVisibility(View.GONE);
				resumeButton.setVisibility(View.VISIBLE);
			}
			else
			{
				stopButton.setVisibility(View.VISIBLE);
				pauseButton.setVisibility(View.VISIBLE);
				resumeButton.setVisibility(View.GONE);
			}
			pauseStartTime = System.currentTimeMillis();
		}
		else if (v == resumeButton)
		{
			myLocationListener.setPaused(!myLocationListener.isPaused());
			isPaused = myLocationListener.isPaused();
			if (isPaused)
			{
				stopButton.setVisibility(View.GONE);
				pauseButton.setVisibility(View.GONE);
				resumeButton.setVisibility(View.VISIBLE);
			}
			else
			{
				stopButton.setVisibility(View.VISIBLE);
				pauseButton.setVisibility(View.VISIBLE);
				resumeButton.setVisibility(View.GONE);
			}
			pauseTime += System.currentTimeMillis()-pauseStartTime;
			//trace.add(new LinkedList<Location>());
		}

	}

	private void updateData(TextView textBox, int meassuredValue) {
		if (meassuredValue == distanceID) {
			textBox.setText(String.format("%.2f", distance / 1000));
		} else if (meassuredValue == paceID) {
			if (pace < 30) {
				// convert pace to show second
				double rest = pace - (int) pace;
				rest = rest * 60;

				String secondsZero = (rest < 10) ? "0" : "";

				textBox.setText(String.format("%.0f:%s%.0f", pace, secondsZero,
						rest));
			} else {
				textBox.setText("--------");
			}
		} else if (meassuredValue == avgPaceID) {
			if (avgPace < 30) {
				// convert pace to show second
				double rest = avgPace - (int) avgPace;
				rest = rest * 60;

				String secondsZero = (rest < 10) ? "0" : "";

				textBox.setText(String.format("%.0f:%s%.0f", avgPace,
						secondsZero, rest));
			} else {
				textBox.setText("--------");
			}
		}
		// TODO a thread to update time every second
		else if (meassuredValue == timeID) {
			long hours = time / 3600000;
			long minutes = (time / 60000) - hours * 60;
			long seconds = (time / 1000) - hours * 3600 - minutes * 60;
			String hourZero = (hours < 10) ? "0" : "";
			String minutesZero = (minutes < 10) ? "0" : "";
			String secondsZero = (seconds < 10) ? "0" : "";

			textBox.setText(String.format("%s%d:%s%d:%s%d", hourZero, hours,
					minutesZero, minutes, secondsZero, seconds));
		}
	}

	public void countData(Location location, Location lastLocation) {

		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		mMap.addPolyline(new PolylineOptions().add(
				new LatLng(lastLocation.getLatitude(), lastLocation
						.getLongitude())).add(latLng));
		
		mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
		
		float speed = location.getSpeed();
		Toast.makeText(this, location.getAccuracy() + "", Toast.LENGTH_SHORT)
				.show();

		pace = (double) 1 / (speed * 60 / 1000);
		// DataTextView3.setText(pace + " min/km");

		distance += lastLocation.distanceTo(location);
		// DataTextView1.setText(distance / 1000 + " km");

		//TODO - zmieniæ
		time = System.currentTimeMillis() - startTime-pauseTime;

		avgPace = ((double) time / 60) / distance;

		updateData(DataTextView1, dataTextView1Content);
		updateData(DataTextView2, dataTextView2Content);

		
		
	}
	@Override
	protected void onStop() {
		LocationClient locationClient = myLocationListener.getmLocationClient();
		// If the client is connected
		if (locationClient.isConnected()) {
			/*
			 * Remove location updates for a listener. The current Activity is
			 * the listener, so the argument is "this".
			 */
			locationClient.removeLocationUpdates(myLocationListener);
		}
		/*
		 * After disconnect() is called, the client is considered "dead".
		 */
		locationClient.disconnect();
		super.onStop();
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		myLocationListener.getmLocationClient().connect();
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        showAlertDialog();
	    }
	    return super.onKeyDown(keyCode, event);
	}

}
