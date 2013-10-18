package com.pwr.zpi.services;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;


public class MyServiceConnection implements ServiceConnection{

	private Messenger mService = null;
	
	public MyServiceConnection()
	{
		
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mService = new Messenger(service);
		
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mService = null;
	}

	public Messenger getmService() {
		return mService;
	}

	

}
