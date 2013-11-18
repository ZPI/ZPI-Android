package com.pwr.zpi.listeners;

public interface ICountDownListner {
	
	public void onCountDownDone(int counterID, int howMuchLeft);
	public void onCountDownUpadte(int counterID, int howMuchLeft);
	public void onCountDownPostAction(int counterID, int howMuchLeft);
}
