package com.pwr.zpi.utils;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;

import com.pwr.zpi.R;

public class AssetsPlayer {
	
	private static final String BEEP_FILE_NAME = "beep.mp3";
	private static final String GO_FILE_NAME = "go.mp3";
	
	private MediaPlayer player;
	private AssetFileDescriptor afd;
	private boolean canPlay;
	private final Context context;
	
	public enum AssetsMp3Files {
		Beep(BEEP_FILE_NAME), Go(GO_FILE_NAME);
		
		String fileName;
		
		private AssetsMp3Files(String fileName) {
			this.fileName = fileName;
		}
		
		public String getFileName() {
			return fileName;
		}
	}
	
	public AssetsPlayer(Context context, AssetsMp3Files file) {
		this.context = context;
		
		changePlayer(file);
		canPlay = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
			context.getString(R.string.key_aplication_sound), false);
	}
	
	public void changePlayer(AssetsMp3Files file) {
		player = new MediaPlayer();
		try {
			afd = context.getAssets().openFd(file.getFileName());
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			afd.close();
			player.prepare();
			player.setVolume(1f, 1f);
		}
		catch (IOException e) {
			Log.e(context.getClass().getSimpleName(), "error reading sound file from assets");
			e.printStackTrace();
		}
	}
	
	public void play() {
		if (canPlay) {
			player.start();
		}
	}
	
	public void setCanPlay(boolean canPlay) {
		this.canPlay = canPlay;
	}
	
	public void stopPlayer() {
		if (player != null && player.isPlaying()) {
			player.stop();
			player.release();
		}
	}
}
