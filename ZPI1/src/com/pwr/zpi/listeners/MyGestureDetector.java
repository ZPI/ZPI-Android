package com.pwr.zpi.listeners;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class MyGestureDetector extends SimpleOnGestureListener {
	
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = Integer.MAX_VALUE;
	private static final int SWIPE_THRESHOLD_VELOCITY = 50;
	
	private final boolean enableDownToUpSwipe;
	private final boolean enableUpToDownSwipe;
	private final boolean enableLeftToRightSwipe;
	private final boolean enableRightToLeftSwipe;
	
	private final GestureListener listener;
	
	public boolean flingDetected;
	
	public MyGestureDetector(GestureListener listener, boolean enableDownToUpSwipe, boolean enableUpToDownSwipe,
		boolean enableLeftToRightSwipe, boolean enableRightToLeftSwipe) {
		this.enableDownToUpSwipe = enableDownToUpSwipe;
		this.enableUpToDownSwipe = enableUpToDownSwipe;
		this.enableLeftToRightSwipe = enableLeftToRightSwipe;
		this.enableRightToLeftSwipe = enableRightToLeftSwipe;
		this.listener = listener;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
		float velocityY) {
		try {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
				flingDetected = false;
				return false;
			}
			// right to left swipe
			if (enableRightToLeftSwipe && e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				flingDetected = true;
				listener.onRightToLeftSwipe();
				// left to right swipe
			}
			else if (enableLeftToRightSwipe && e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				flingDetected = true;
				listener.onLeftToRightSwipe();
				// down to up swipe
			}
			else if (enableDownToUpSwipe && e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				flingDetected = true;
				listener.onDownToUpSwipe();
				// up to down swipe
			}
			else if (enableUpToDownSwipe && e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				flingDetected = true;
				listener.onUpToDownSwipe();
			}
			else {
				flingDetected = false;
			}
		}
		catch (Exception e) {}
		return false;
	}
	
	public boolean isFlingDetected()
	{
		return flingDetected;
	}
	
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		listener.onSingleTapConfirmed(e);
		return true;
	}
	
}