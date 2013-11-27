package com.pwr.zpi.utils;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.pwr.zpi.services.NotifyService;

public class Reminders {
	
	private static final int DEFAULT_REQUEST_CODE = 0;
	
	public static void setRemainder(Context activity, Date when) {
		AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(activity, NotifyService.class);
		PendingIntent pendingIntent = PendingIntent.getService(activity, DEFAULT_REQUEST_CODE, intent, 0);
		alarmManager.set(AlarmManager.RTC, when.getTime(), pendingIntent);
	}
	
	public static void cancelAllReminders(Context activity) {
		AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(activity, NotifyService.class);
		PendingIntent pendingIntent = PendingIntent.getService(activity, DEFAULT_REQUEST_CODE, intent, 0);
		alarmManager.cancel(pendingIntent);
	}
}
