package com.pwr.zpi.utils;

import java.util.TimerTask;

import com.pwr.zpi.services.LocationService;

public class CheckForLostGPS extends TimerTask {
	
	private final LocationService service; //service to notify about lost GPS
	private long beginTime;
	
	public CheckForLostGPS(LocationService service)
	{
		this.service = service;
		beginTime = System.currentTimeMillis();
	}
	
	public void startFromBeginning()
	{
		beginTime = System.currentTimeMillis();
	}
	
	@Override
	public void run() {
		if (System.currentTimeMillis() - beginTime > LocationService.MAX_UPDATE_TIME)
		{
			service.onLostGPSSignal();
			cancel();
		}
		
	}
	
}
