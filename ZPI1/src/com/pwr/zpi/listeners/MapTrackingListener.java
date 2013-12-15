package com.pwr.zpi.listeners;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

public class MapTrackingListener implements OnTouchListener {
	
	private static final long MAX_USER_INACTIVITY = 5000;
	private long lastUserInteractionTime;
	private boolean isMapTracking;
	private final ImageButton centerMapButton;
	
	public MapTrackingListener(ImageButton centerMap)
	{
		centerMapButton = centerMap;
		setMapTracking(true);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		setMapTracking(false);
		return false;
	}
	
	public boolean isMapTracking() {
		return isMapTracking;
	}
	
	public void setMapTracking(boolean isMapTracking) {
		this.isMapTracking = isMapTracking;
		centerMapButton.setVisibility(isMapTracking ? View.GONE : View.VISIBLE);
		if (!isMapTracking) {
			lastUserInteractionTime = System.currentTimeMillis();
		}
	}
	
	public void checkUserInactivity()
	{
		if (System.currentTimeMillis() - lastUserInteractionTime > MAX_USER_INACTIVITY) {
			setMapTracking(true);
		}
	}
	
}
