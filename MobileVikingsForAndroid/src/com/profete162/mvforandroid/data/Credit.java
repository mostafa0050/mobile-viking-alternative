package com.profete162.mvforandroid.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.profete162.mvforandroid.util.DateParser;


/**
 * This class parses a JSON object from the MobileVikingsService, and stores the
 * credit information contained in it.
 */
public class Credit {

	private int sms;
	private double credits;
	private long data;
	private Date validUntil;

	/**
	 * Create a new Credit object based on a JSON object. If the JSON isn't
	 * valid, a {@link JSONException} is thrown. For a valid format, see the
	 * Mobile Vikings API.
	 * 
	 * @throws JSONException
	 */
	public Credit(JSONObject json) throws JSONException {
		sms = Integer.parseInt(json.getString("sms"));
		credits = Double.parseDouble(json.getString("credits"));
		data = Long.parseLong(json.getString("data"));
		String dateString = json.getString("valid_until");
		validUntil = DateParser.getDate(dateString);
	}

	public int getSms() {
		return sms;
	}

	public double getCredits() {
		return credits;
	}

	public long getData() {
		return data;
	}

	public int getDataInMegabytes() {
		return (int) (data / 1048576);
	}

	public Date getValidUntil() {
		return validUntil;
	}

	public String getValidUntilString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		return sdf.format(validUntil);
	}
}
