package com.pwr.zpi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.pwr.zpi.listeners.GestureListener;
import com.pwr.zpi.listeners.MyGestureDetector;

public class PlaningActivity extends Activity implements GestureListener, OnClickListener, OnItemClickListener {
	
	GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private static final String TAB_SPEC_1_TAG = "TabSpec1";
	private static final String TAB_SPEC_2_TAG = "TabSpec2";
	private Button newWorkoutButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.planing_activity);
		prepareGestureListener();
		addListeners();
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		TabSpec tabSpecs = tabHost.newTabSpec(TAB_SPEC_1_TAG);
		tabSpecs.setContent(R.id.tab1);
		tabSpecs.setIndicator("Workouts");
		tabHost.addTab(tabSpecs);
		tabSpecs = tabHost.newTabSpec(TAB_SPEC_2_TAG);
		tabSpecs.setContent(R.id.tab2);
		tabSpecs.setIndicator("Workouts");
		tabHost.addTab(tabSpecs);
		
		newWorkoutButton = (Button) findViewById(R.id.buttonNewWorkout);
		newWorkoutButton.setOnClickListener(this);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_left_anim, R.anim.out_left_anim);
	}
	
	private void prepareGestureListener() {
		// Gesture detection
		gestureDetector = new GestureDetector(this, new MyGestureDetector(this, false, false, false, true));
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
		finish();
		overridePendingTransition(R.anim.in_left_anim, R.anim.out_left_anim);
	}
	
	@Override
	public void onUpToDownSwipe() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDownToUpSwipe() {
		// TODO Auto-generated method stub
		
	}
	
	private void addListeners() {
		
	}
	
	@Override
	public void onClick(View v) {
		if (v == newWorkoutButton)
		{
			startActivity(new Intent(PlaningActivity.this, WorkoutsActivity.class));
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int positon, long id) {
		// TODO Auto-generated method stub
		
	}
}