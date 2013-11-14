package com.pwr.zpi;

import com.pwr.zpi.RunListener;
interface RunListenerApi {
 
	List<Location> getWholeRun();
	double getDistance();
	long getTime();
	Location getLatestLocation();
	Intent getConnectionResult();
	int getGPSStatus();
	void setStarted();
	void setPaused();
	void setResumed();
	void setStoped();
   
  	void addListener(RunListener listener);
 
 	void removeListener(RunListener listener);
}