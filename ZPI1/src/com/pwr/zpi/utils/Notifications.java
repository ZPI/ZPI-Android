package com.pwr.zpi.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.pwr.zpi.ActivityActivity;
import com.pwr.zpi.R;

public class Notifications {
	
	private static final int NOTIFICATION_ID = 1;
	
	public static Notification createNotification(Context context, Class<? extends Activity> cls, int contentTitle,
		int contentText)
	{
		// Make sure the launch mode of the activity is singleTask, otherwise it will create a new one
		Intent intent = new Intent(context, ActivityActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
		
		// Build notification
		Notification note = new NotificationCompat.Builder(context)
			.setContentTitle(context.getResources().getString(contentTitle))
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentText(context.getResources().getString(contentText))
			.setContentIntent(pIntent).build();
		return note;
	}
	
	public static void destroyNotification(Activity activity)
	{
		NotificationManager mNotificationManager =
			(NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(NOTIFICATION_ID);
	}
}
