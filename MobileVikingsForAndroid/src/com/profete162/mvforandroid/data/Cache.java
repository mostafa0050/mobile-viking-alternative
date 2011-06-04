package com.profete162.mvforandroid.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;

/**
 * Manages a cache for the different kinds of data (credit info, call history,
 * topup log).
 */
public class Cache {

	private Context context;

	private final String CREDIT_FILE = "credit.json";
	private final String CALL_HISTORY_FILE = "callhistory.json";
	private final String TOPUPS_FILE = "topups.json";

	public Cache(Context context) {
		this.context = context;
	}

	/**
	 * Return whether something is stored in the cache.
	 */
	public boolean hasCache() {
		return hasCredit() || hasCallHistory() || hasTopups();
	}

	/**
	 * Returns whether credit information is stored in the cache.
	 */
	public boolean hasCredit() {
		boolean result = false;
		for (String file : context.fileList())
			if (file.equals(CREDIT_FILE))
				return true;
		return result;
	}

	/**
	 * Stores the given credit information in the cache. (Should be in MV API
	 * format.)
	 */
	public void putCredit(String json) {
		put(json, CREDIT_FILE);
	}

	/**
	 * Gets the credit information from the cache if present. Returns null
	 * otherwise.
	 */
	public String getCredit() {
		if (hasCredit())
			return get(CREDIT_FILE);
		return null;
	}

	/**
	 * Returns whether call history information is stored in the cache.
	 */
	public boolean hasCallHistory() {
		boolean result = false;
		for (String file : context.fileList())
			if (file.equals(CALL_HISTORY_FILE))
				result = true;
		return result;
	}

	/**
	 * Stores the given call history information in the cache. (Should be in MV
	 * API format.)
	 */
	public void putCallHistory(String json) {
		put(json, CALL_HISTORY_FILE);
	}

	/**
	 * Gets the call history information from the cache if present. Returns null
	 * otherwise.
	 */
	public String getCallHistory() {
		if (hasCallHistory())
			return get(CALL_HISTORY_FILE);
		return null;
	}

	/**
	 * Returns whether topup information is stored in the cache.
	 */
	public boolean hasTopups() {
		boolean result = false;
		for (String file : context.fileList())
			if (file.equals(TOPUPS_FILE))
				result = true;
		return result;
	}

	/**
	 * Stores the given topups information in the cache. (Should be in MV API
	 * format.)
	 */
	public void putTopups(String json) {
		put(json, TOPUPS_FILE);
	}

	/**
	 * Gets the topups information from the cache if present. Returns null
	 * otherwise.
	 */
	public String getTopups() {
		if (hasTopups())
			return get(TOPUPS_FILE);
		return null;
	}

	/**
	 * Stores a string in a file in the cache.
	 */
	private void put(String string, String file) {
		try {
			FileOutputStream fos = context.openFileOutput(file,
					Context.MODE_WORLD_READABLE);
			OutputStreamWriter out = new OutputStreamWriter(fos);
			out.write(string);
			out.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets a file from the cache.
	 */
	private String get(String file) {
		try {
			StringBuffer result = new StringBuffer();
			FileInputStream fis = context.openFileInput(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader in = new BufferedReader(isr, 8192);

			String text;
			while ((text = in.readLine()) != null) {
				result.append(text);
			}
			in.close();
			return result.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
