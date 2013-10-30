package com.pwr.zpi.utils;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

public class BeepPlayer {
	
	private static final String BEEP_FILE_NAME = "beep.mp3";
	
	private final MediaPlayer player;
	private AssetFileDescriptor afd;
	
	public BeepPlayer(Context context) {
		player = new MediaPlayer();
		try {
			afd = context.getAssets().openFd(BEEP_FILE_NAME);
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			afd.close();
			player.prepare();
			player.setVolume(1f, 1f);
		}
		catch (IOException e) {
			Log.e(context.getClass().getSimpleName(), "error reading beep file from assets");
			e.printStackTrace();
		}
		
	}
	
	public void playBeep() {
		player.start();
	}
	
	public void stopPlayer() {
		if (player != null && player.isPlaying()) {
			player.stop();
			player.release();
		}
	}
}
