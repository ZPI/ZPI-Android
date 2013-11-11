package com.pwr.zpi;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.pwr.zpi.listeners.GestureListener;
import com.pwr.zpi.listeners.MyGestureDetector;

public class SettingsActivity extends PreferenceActivity implements GestureListener {
	
	GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		setContentView(R.layout.settings_activity);
		
		addPreferencesFromResource(R.xml.settings);
		
		prepareGestureListener();
		addListeners();
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
	}
	
}