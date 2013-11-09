package com.pwr.zpi.services;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.pwr.zpi.listeners.MyLocationListener;

public class MyServiceConnection implements ServiceConnection {
	
	private Messenger mService = null;
	public static final int ACTIVITY = 1;
	public static final int MAIN_SCREEN = 2;
	public static final int WORKOUT = 3;
	private final int activityType;
	Activity activity;
	
	public MyServiceConnection(Activity activity, int type)
	{
		this.activity = activity;
		activityType = type;
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mService = new Messenger(service);
		switch (activityType)
		{
			case ACTIVITY:
				sendMessage(MyLocationListener.MSG_START);
				break;
			case MAIN_SCREEN:
				break;
			case WORKOUT:
				break;
		}
		
	}
	
	@Override
	public void onServiceDisconnected(ComponentName name) {
		mService = null;
	}
	
	public Messenger getmService() {
		return mService;
	}
	
	public void sendMessage(int messageType)
	{
		try {
			Message msg = Message.obtain(null,
				messageType);
			getmService().send(msg);
			
		}
		catch (RemoteException e) {
			
		}
	}
	
}
