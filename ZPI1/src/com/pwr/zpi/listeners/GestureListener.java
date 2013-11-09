package com.pwr.zpi.listeners;

import android.view.MotionEvent;

public interface GestureListener {
	
	public void onLeftToRightSwipe();
	
	public void onRightToLeftSwipe();
	
	public void onUpToDownSwipe();
	
	public void onDownToUpSwipe();
	
	public void onSingleTapConfirmed(MotionEvent e);
}