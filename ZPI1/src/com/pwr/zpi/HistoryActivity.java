package com.pwr.zpi;

import com.pwr.zpi.listeners.GestureListener;
import com.pwr.zpi.listeners.MyGestureDetector;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class HistoryActivity extends Activity implements GestureListener{

	GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_activity);
		prepareGestureListener();
		addListeners();
	}

	private void prepareGestureListener() {
		// Gesture detection
		gestureDetector = new GestureDetector(this, new MyGestureDetector(this, false, false, true, false));
		gestureListener = new View.OnTouchListener() {
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_right_anim, R.anim.out_right_anim);

    }

	@Override
	public void onLeftToRightSwipe() {
		
		finish();
		overridePendingTransition(R.anim.in_right_anim, R.anim.out_right_anim);
	}

	@Override
	public void onRightToLeftSwipe() {
		// TODO Auto-generated method stub
		
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
	
}