package com.profete162.mvforandroid.data;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.profete162.mvforandroid.util.DateParser;


public class Topup {
	private String status;
	private Date date;
	private String method;
	private double amount;

	public Topup(Date date, double amount, String method, String status) {
		this.date = date;
		this.amount = amount;
		this.method = method;
		this.status = status;
	}

	public Topup(JSONObject o) {
		try {
			this.date = DateParser.getDate(o.getString("on"));
			this.amount = Double.parseDouble(o.getString("amount"));
			this.method = o.getJSONObject("method").getString("clean");
			this.status = o.getJSONObject("status").getString("clean");
		} catch (JSONException e) {
			throw new RuntimeException("JSONException");
		}
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
}
