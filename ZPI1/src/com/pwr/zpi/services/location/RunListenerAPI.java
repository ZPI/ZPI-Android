package com.pwr.zpi.services.location;

import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.os.RemoteException;

import com.pwr.zpi.RunListener;
import com.pwr.zpi.RunListenerApi;
import com.pwr.zpi.database.entity.Workout;

public class RunListenerAPI extends RunListenerApi.Stub {
	
	@Override
	public List<Location> getWholeRun() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public double getDistance() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public long getTime() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public Location getLatestLocation() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Intent getConnectionResult() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int getGPSStatus() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void setStarted(Workout workout, int countDownTime) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setPaused() throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setResumed() throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setStoped() throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void doSaveRun(boolean save, String name) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void prepareTextToSpeech() throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSoundSettingChange(boolean enabled) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addListener(RunListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void removeListener(RunListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
}
