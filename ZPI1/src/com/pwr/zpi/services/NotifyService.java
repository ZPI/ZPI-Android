package com.pwr.zpi.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;

import com.pwr.zpi.MainScreenActivity;
import com.pwr.zpi.R;
import com.pwr.zpi.utils.Notifications;

public class NotifyService extends IntentService {
	
	private static int ID = 0x2;
	
	public NotifyService(){
		super("NotifyService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Notification notification = Notifications.createNotification(getApplicationContext(),
			MainScreenActivity.class,
			R.string.its_time_to_run_notification_title,
			R.string.its_time_to_run_notification_message);
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(ID, notification);
	}
}
