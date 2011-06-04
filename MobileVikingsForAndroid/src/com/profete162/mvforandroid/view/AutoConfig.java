package com.profete162.mvforandroid.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class AutoConfig extends Activity {

	/*
	 * Information of all APNs Details can be found in
	 * com.android.providers.telephony.TelephonyProvider
	 */
	public static final Uri APN_TABLE_URI = Uri
			.parse("content://telephony/carriers");
	/*
	 * Information of the preferred APN
	 */
	public static final Uri PREFERRED_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");
	/*
	 * Enumerate all APN data
	 */

	Context context;
	String TAG = "BLA";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		 //EnumerateAPNs();

		AlertDialog.Builder ad;
		ad = new AlertDialog.Builder(context);
		ad.setTitle("Warning");
		ad
				.setMessage("Add a new APN with Mobile Vikings settings?\n\nDon't forget to delete them if you clicked too much on this button ;-)");
		ad.setPositiveButton(android.R.string.ok,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						int id = InsertMVAPN();
						SetDefaultAPN(id);
						InsertMVAPNMMS();
						startActivity(new Intent(Settings.ACTION_APN_SETTINGS));
						finish();

					}
				});

		ad.setNegativeButton(android.R.string.no,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						finish();
					}
				});
		ad.show();

	}

	/*
	 * Insert a new APN entry into the system APN table Require an apn name, and
	 * the apn address. More can be added. Return an id (_id) that is
	 * automatically generated for the new apn entry.
	 */
	public int InsertMVAPN() {
		int id = -1;
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put("name", "Mobile Vikings");
		values.put("apn", "web.be");
		values.put("user", "web");
		values.put("password", "web");
		values.put("mcc", "206");
		values.put("mnc", "20");
		values.put("numeric", "20620");
		values.put("type", "default");

		Cursor c = null;
		try {
			Uri newRow = resolver.insert(APN_TABLE_URI, values);
			if (newRow != null) {
				c = resolver.query(newRow, null, null, null, null);
				Log.d(TAG, "Newly added APN:");
				printAllData(c); // Print the entire result set

				// Obtain the apn id
				int idindex = c.getColumnIndex("_id");
				c.moveToFirst();
				id = c.getShort(idindex);
				Log.d(TAG, "New ID: " + id + ": Inserting new APN succeeded!");
			}
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage());
		}

		if (c != null)
			c.close();
		return id;
	}
	
	public int InsertMVAPNMMS() {
		int id = -1;
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put("name", "Mobile Vikings MMS");
		values.put("apn", "mms.be");
		values.put("user", "mms");
		values.put("password", "mms");
		values.put("mmsc", "hhtp://mmsc.be");
		values.put("mmsproxy", "217.72.235.1");
		values.put("mmsport", "8080");
		values.put("authtype", "PAP");
		values.put("mcc", "206");
		values.put("mnc", "20");
		values.put("numeric", "20620");
		values.put("type", "mms");

		Cursor c = null;
		try {
			Uri newRow = resolver.insert(APN_TABLE_URI, values);
			if (newRow != null) {
				c = resolver.query(newRow, null, null, null, null);
				Log.d(TAG, "Newly added APN:");
				printAllData(c); // Print the entire result set

				// Obtain the apn id
				int idindex = c.getColumnIndex("_id");
				c.moveToFirst();
				id = c.getShort(idindex);
				Log.d(TAG, "New ID: " + id + ": Inserting new APN succeeded!");
			}
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage());
		}

		if (c != null)
			c.close();
		return id;
	}

	/*
	 * Set an apn to be the default apn for web traffic Require an input of the
	 * apn id to be set
	 */
	public boolean SetDefaultAPN(int id) {
		boolean res = false;
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();

		// See /etc/apns-conf.xml. The TelephonyProvider uses this file to
		// provide
		// content://telephony/carriers/preferapn URI mapping
		values.put("apn_id", id);
		try {
			resolver.update(PREFERRED_APN_URI, values, null, null);
			Cursor c = resolver.query(PREFERRED_APN_URI, new String[] { "name",
					"apn" }, "_id=" + id, null, null);
			if (c != null) {
				res = true;
				c.close();
			}
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage());
		}
		return res;
	}

	private void EnumerateAPNs() {
		Cursor c = context.getContentResolver().query(APN_TABLE_URI, null,
				null, null, null);
		if (c != null) {
			/*
			 * Fields you can retrieve can be found in
			 * com.android.providers.telephony.TelephonyProvider :
			 * 
			 * db.execSQL("CREATE TABLE " + CARRIERS_TABLE +
			 * "(_id INTEGER PRIMARY KEY," + "name TEXT," + "numeric TEXT," +
			 * "mcc TEXT," + "mnc TEXT," + "apn TEXT," + "user TEXT," +
			 * "server TEXT," + "password TEXT," + "proxy TEXT," + "port TEXT,"
			 * + "mmsproxy TEXT," + "mmsport TEXT," + "mmsc TEXT," +
			 * "type TEXT," + "current INTEGER);");
			 */

			String s = "All APNs:\n";
			Log.d(TAG, s);
			try {
				s += printAllData(c); // Print the entire result set
			} catch (SQLException e) {
				Log.d(TAG, e.getMessage());
			}

			// Log.d(TAG, s + "\n\n");
			c.close();
		}

	}

	private String printAllData(Cursor c) {
		if (c == null)
			return null;
		String s = "";
		int record_cnt = c.getColumnCount();
		Log.d(TAG, "Total # of records: " + record_cnt);

		if (c.moveToFirst()) {
			String[] columnNames = c.getColumnNames();
			Log.d(TAG, getAllColumnNames(columnNames));
			s += getAllColumnNames(columnNames);
			do {
				String row = "";
				for (String columnIndex : columnNames) {
					int i = c.getColumnIndex(columnIndex);
					row += c.getString(i) + ":\t";
				}
				row += "\n";
				Log.d(TAG, row);
				s += row;
			} while (c.moveToNext());
			Log.d(TAG, "End Of Records");
		}
		return s;
	}

	/*
	 * Return all column names stored in the string array
	 */
	private String getAllColumnNames(String[] columnNames) {
		String s = "Column Names:\n";
		for (String t : columnNames) {
			s += t + ":\t";
		}
		return s + "\n";
	}

}
