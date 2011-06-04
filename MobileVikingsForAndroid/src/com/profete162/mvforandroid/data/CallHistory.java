package com.profete162.mvforandroid.data;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.profete162.mvforandroid.exceptions.CallHistoryEmptyException;


/**
 * This class manages a list of calls that form the history of a certain date.
 */
public class CallHistory {

	private ArrayList<Call> calls;
	private Calendar date;

	public CallHistory(JSONArray json) throws JSONException,
			CallHistoryEmptyException {
		if (json.length() == 0)
			throw new CallHistoryEmptyException();
		// Fill arraylist
		ArrayList<Call> history = new ArrayList<Call>();
		for (int i = 0; i < json.length(); i++) {
			JSONObject o = json.getJSONObject(i);
			history.add(new Call(o));
		}
		setCalls(history);

		// The date of a call history is the same as the start date of all its
		// calls.
		Calendar c = Calendar.getInstance();
		c.setTime(calls.get(0).getBegin());
		setDate(c);
	}

	public ArrayList<Call> getCalls() {
		return calls;
	}

	public void setCalls(ArrayList<Call> calls) {
		this.calls = calls;
	}

	public Calendar getDate() {
		return (Calendar) date.clone();
	}

	public void setDate(Calendar date) {
		this.date = (Calendar) date.clone();
	}

	public ArrayList<Call> getSmsCalls() {
		ArrayList<Call> result = new ArrayList<Call>();
		for (Call c : calls)
			if (c.isSms())
				result.add(c);
		return result;
	}

	public ArrayList<Call> getMmsCalls() {
		ArrayList<Call> result = new ArrayList<Call>();
		for (Call c : calls)
			if (c.isMms())
				result.add(c);
		return result;
	}

	public ArrayList<Call> getVoiceCalls() {
		ArrayList<Call> result = new ArrayList<Call>();
		for (Call c : calls)
			if (c.isVoice())
				result.add(c);
		return result;
	}

	public ArrayList<Call> getDataCalls() {
		ArrayList<Call> result = new ArrayList<Call>();
		for (Call c : calls)
			if (c.isData())
				result.add(c);
		return result;
	}

}
