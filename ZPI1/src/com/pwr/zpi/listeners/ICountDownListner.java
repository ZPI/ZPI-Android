package com.pwr.zpi.listeners;

public interface ICountDownListner {
	
	public void onCountDownDone(int howMuchLeft);
	public void onCountDownUpadte(int howMuchLeft);
	public void onCountDownPostAction(int howMuchLeft);
}
