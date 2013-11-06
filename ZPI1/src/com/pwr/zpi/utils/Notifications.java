package com.pwr.zpi.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.pwr.zpi.R;


public class Notifications {
	
	private static final int NOTIFICATION_ID = 1;
	
	public static void createNotification(Activity activity, Class<? extends Activity> cls, int contentTitle, int contentText)
	{
		NotificationCompat.Builder mBuilder =
			new NotificationCompat.Builder(activity)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle(activity.getResources().getString(contentTitle))
		.setContentText(activity.getResources().getString(contentText));
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(activity, cls);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(
			activity.getApplicationContext(),
			0,
			resultIntent,
			PendingIntent.FLAG_UPDATE_CURRENT);
		
		mBuilder.setContentIntent(contentIntent);
		mBuilder.setOngoing(true);
		NotificationManager mNotificationManager =
			(NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
	
	public static void destroyNotification(Activity activity)
	{
		NotificationManager mNotificationManager =
			(NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(NOTIFICATION_ID);
	}
}
