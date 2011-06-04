package com.profete162.mvforandroid.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;

/**
 * Contains various methods that interface with the Android platform.
 */
public class AndroidUtils {

	public static String getContactNameFromNumber(Context context, String number) {
		// define the columns I want the query to return
		String[] projection = new String[] { Contacts.Phones.DISPLAY_NAME,
				Contacts.Phones.NUMBER };

		// encode the phone number and build the filter URI
		Uri contactUri = Uri.withAppendedPath(
				Contacts.Phones.CONTENT_FILTER_URL, Uri.encode(number));

		// query time
		Cursor c = context.getContentResolver().query(contactUri, projection,
				null, null, null);

		// if the query returns 1 or more results
		// return the first result
		if (c.moveToFirst()) {
			String name = c.getString(c
					.getColumnIndex(Contacts.Phones.DISPLAY_NAME));
			return name;
		}

		// return the original number if no match was found
		return number;
	}

	public static String getContactIdFromNumber(Context context, String number) {
		// define the columns I want the query to return
		String[] projection = new String[] { Contacts.People._ID };

		// encode the phone number and build the filter URI
		Uri contactUri = Uri.withAppendedPath(
				Contacts.People.CONTENT_FILTER_URI, Uri.encode(AndroidUtils
						.getContactNameFromNumber(context, number)));

		// query time
		Cursor c = context.getContentResolver().query(contactUri, projection,
				null, null, null);

		// if the query returns 1 or more results
		// return the first result
		if (c.moveToFirst()) {
			String id = c.getString(c.getColumnIndex(Contacts.People._ID));
			return id;
		}

		// return empty string if not found
		return "";
	}

}
