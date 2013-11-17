package com.pwr.zpi.utils;

import com.pwr.zpi.listeners.ICountDownListner;

public class CounterRunnable implements Runnable {
	
	final int x;
	private final ICountDownListner listener;
	
	public CounterRunnable(int x, ICountDownListner listener) {
		this.x = x;
		this.listener = listener;
	}
	
	@Override
	public void run() {
		if (x == -1) {
			listener.onCountDownPostAction(x);
		} else if (x == 0) {
			listener.onCountDownDone(x);
		}
		else {
			listener.onCountDownUpadte(x);
		}
	}
}