package com.pwr.zpi;

import android.app.Application;

import com.pwr.zpi.utils.SpeechSynthezator;

public class ZPIApplication extends Application {
	
	private SpeechSynthezator syntezator;
	
	public SpeechSynthezator getSyntezator() {
		return syntezator;
	}
	
	public void setSyntezator(SpeechSynthezator syntezator) {
		this.syntezator = syntezator;
	}
}
