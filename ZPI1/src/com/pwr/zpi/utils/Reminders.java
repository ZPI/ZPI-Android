package com.pwr.zpi.utils;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.pwr.zpi.services.NotifyService;

public class Reminders {
	
	private static final int DEFAULT_REQUEST_CODE = 0;
	
	private static PendingIntent getPendingIntent(Context context) {
		Intent intent = new Intent(context, NotifyService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, DEFAULT_REQUEST_CODE, intent, 0);
		return pendingIntent;
	}
	
	public static void setRemainder(Context context, Date when) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, when.getTime(), getPendingIntent(context));
	}
	
	public static void setRemainderEvery(Context context, Date startDate, long repeatTime) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC, startDate.getTime(), repeatTime, getPendingIntent(context));
	}
	
	public static void cancelAllReminders(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(getPendingIntent(context));
	}
}
