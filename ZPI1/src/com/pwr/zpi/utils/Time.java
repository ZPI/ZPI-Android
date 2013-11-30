package com.pwr.zpi.utils;

import java.util.Calendar;
import java.util.Date;

public class Time {
	
	public static Date getDate(int hour, int minute, int seconds) {
		Calendar c = Calendar.getInstance();
		return getDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), hour, minute, seconds);
	}
	
	public static Date getDate(int day, int hour, int minute, int seconds) {
		Calendar c = Calendar.getInstance();
		return getDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, day, hour, minute, seconds);
	}
	
	public static Date getDate(int month, int day, int hour, int minute, int seconds) {
		Calendar c = Calendar.getInstance();
		return getDate(c.get(Calendar.YEAR), month, day, hour, minute, seconds);
	}
	
	public static Date getDate(int year, int month, int day, int hour, int minute, int seconds) {
		return getDate(year, month, day, hour, minute, seconds, 0);
	}
	
	public static Date getDate(int year, int month, int day, int hour, int minute, int seconds, int miliseconds) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, seconds);
		c.set(Calendar.MILLISECOND, miliseconds);
		return c.getTime();
	}
	
	public static Calendar zeroTimeInDate(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
}
