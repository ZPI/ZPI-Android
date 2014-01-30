package com.pwr.zpi.utils;

import com.pwr.zpi.listeners.ICountDownListner;

public class CounterRunnable implements Runnable {
	
	final int x;
	private final ICountDownListner listener;
	private final int counterID;
	
	public CounterRunnable(int counterID, int x, ICountDownListner listener) {
		this.counterID = counterID;
		this.x = x;
		this.listener = listener;
	}
	
	@Override
	public void run() {
		if (x == -1) {
			listener.onCountDownPostAction(counterID, x);
		}
		else if (x == 0) {
			listener.onCountDownDone(counterID, x);
		}
		else {
			listener.onCountDownUpadte(counterID, x);
		}
	}
	
	public int getCounterID() {
		return counterID;
	}
}