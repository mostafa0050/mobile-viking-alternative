package com.profete162.mvforandroid.data;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.profete162.mvforandroid.util.DateParser;


/**
 * This class parses a JSON object from the MobileVikingsService, and stores its
 * call information.
 */
public class Call {

	private Date begin, end;
	private String duration;
	private double price;
	private boolean isData, isSms, isIncoming, isMms;
	private boolean isVoice;
	private long data;
	private String to;

	public Call(JSONObject json) throws JSONException {
		begin = DateParser.getDate(json.getString("start_timestamp"));
		end = DateParser.getDate(json.getString("end_timestamp"));
		duration = json.getString("duration_human");
		price = Double.parseDouble(json.getString("price"));
		to = json.getString("to");
		isData = json.getBoolean("is_data");
		isSms = json.getBoolean("is_sms");
		isIncoming = json.getBoolean("is_incoming");
		isMms = json.getBoolean("is_mms");
		isVoice = json.getBoolean("is_voice");
		data = json.getLong("duration_connection");
	}

	public Date getBegin() {
		return begin;
	}

	public Date getEnd() {
		return end;
	}

	public String getDuration() {
		return duration;
	}

	public double getPrice() {
		return price;
	}

	public boolean isData() {
		return isData;
	}

	public boolean isSms() {
		return isSms;
	}

	public boolean isIncoming() {
		return isIncoming;
	}

	public boolean isMms() {
		return isMms;
	}

	public boolean isVoice() {
		return isVoice;
	}

	public long getData() {
		return data;
	}

	public String getTo() {
		return to;
	}

}
