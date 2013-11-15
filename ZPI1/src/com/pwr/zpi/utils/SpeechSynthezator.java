package com.pwr.zpi.utils;

import java.util.Locale;

import android.content.Context;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import com.pwr.zpi.R;

/**
 * this class should be initializated only once and then referenced by static method getSyntezator();
 */
public class SpeechSynthezator implements OnInitListener {
	
	private static final String TAG = SpeechSynthezator.class.getSimpleName();
	
	public TextToSpeech mTts;
	private boolean initialized = false;
	private boolean canSpeak = true;
	
	
	public SpeechSynthezator(Context context) {
		
		canSpeak = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getResources().getString(R.string.key_aplication_sound), false);
		mTts = new TextToSpeech(context, this);
	}
	
	@Override
	public void onInit(int status) {
		
		if (status == TextToSpeech.SUCCESS) {
			Locale loc = Locale.getDefault();
			if (mTts.isLanguageAvailable(loc) == TextToSpeech.LANG_AVAILABLE
				|| mTts.isLanguageAvailable(loc) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
				mTts.setLanguage(loc);
			}
			
			String text = "cześć jestem Twoją superową aplikacją do biegania. Trenuj wytrwale, może kiedyś zostaniesz mistrzem świata! Wierzę w Twoje umiejętności biegaczu! Razem zwyciężymy wszystkie zawody! Wciśniij start, by zacząć Naszą przygodę z bieganiem.";
			text = " test ";
			if (canSpeak) {
				mTts.speak(text, TextToSpeech.QUEUE_ADD, null);
			}
			initialized = true;
		} else {
			initialized = false;
		}
	}
	
	public void say(String textToSay) {
		Log.i(TAG, "realy almost there to say");
		if (canSpeak()) {
			Log.i(TAG, "Should say now!");
			mTts.speak(textToSay, TextToSpeech.QUEUE_ADD, null);
		}
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public boolean canSpeak() {
		return canSpeak && isInitialized();
	}
	
	public void shutdown() {
		mTts.shutdown();
	}
}
