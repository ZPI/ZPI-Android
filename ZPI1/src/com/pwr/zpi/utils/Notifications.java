package com.pwr.zpi.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.pwr.zpi.R;

public class Notifications {
	
	public static Notification createNotification(Context context, Class<? extends Activity> cls, int contentTitle,
		int contentText) {
		// Make sure the launch mode of the activity is singleTask, otherwise it will create a new one
		Intent intent = new Intent(context, cls);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
		
		//		long[] vibrations = new long[] {1000L, 500L};
		
		// Build notification
		Notification note = new NotificationCompat.Builder(context)
		.setContentTitle(context.getResources().getString(contentTitle)).setSmallIcon(R.drawable.ic_launcher)
		.setContentText(context.getResources().getString(contentText))
		//		.setVibrate(vibrations)
		//.setLights(0xff0000ff, 1000, 3000)
		.setContentIntent(pIntent).build();
		return note;
	}
}
