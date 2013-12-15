package com.pwr.zpi.listeners;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;

import com.pwr.zpi.R;

public class ActityButtonStateChangeListener implements OnTouchListener, OnFocusChangeListener {
	
	Context context;
	
	public ActityButtonStateChangeListener(Context context)
	{
		this.context = context;
	}
	
	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				v.setBackgroundColor(context.getResources().getColor(R.color.activity_blue_pressed));
				break;
			case MotionEvent.ACTION_UP:
				v.setBackgroundColor(context.getResources().getColor(R.color.activity_blue));
				break;
		}
		
		return false;
	}
	
}
