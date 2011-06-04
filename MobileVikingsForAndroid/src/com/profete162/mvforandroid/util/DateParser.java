package com.profete162.mvforandroid.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParser {

	private static final String API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private static final String DISPLAY_FORMAT_DATE = "dd-MM-yyyy";
	private static final String DISPLAY_FORMAT_TIME = "HH:mm";

	public static Date getDate(String apiString) {
		SimpleDateFormat sdf = new SimpleDateFormat(API_DATE_FORMAT);
		try {
			return sdf.parse(apiString);
		} catch (ParseException e) {
			return null;
		}
	}

	public static String getAPI(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(API_DATE_FORMAT);
		return sdf.format(date);
	}

	public static String getDisplayStringDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_FORMAT_DATE);
		return sdf.format(date);
	}

	public static String getDisplayStringTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_FORMAT_TIME);
		return sdf.format(date);
	}
}
