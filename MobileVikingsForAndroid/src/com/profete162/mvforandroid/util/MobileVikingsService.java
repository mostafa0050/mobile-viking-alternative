package com.profete162.mvforandroid.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.profete162.mvforandroid.data.Cache;
import com.profete162.mvforandroid.data.CallHistory;
import com.profete162.mvforandroid.data.Credit;
import com.profete162.mvforandroid.data.Topup;
import com.profete162.mvforandroid.exceptions.CallHistoryEmptyException;

/**
 * Manages the retrieval of data from the Mobile Vikings API and parses it into
 * an application-usable format. This class implements the Singleton pattern.
 * Get an instance using getInstance().
 */
public class MobileVikingsService {

	private Cache cache;

	private String username;
	private String password;

	private final String URL_CALL_HISTORY = "https://mobilevikings.com/api/1.0/rest/mobilevikings/call_history.json";
	private final String URL_CREDIT = "https://mobilevikings.com/api/1.0/rest/mobilevikings/sim_balance.json";
	private final String URL_TOPUPS = "https://mobilevikings.com/api/1.0/rest/mobilevikings/top_up_history.json";

	private Credit credit;
	private CallHistory callHistory;
	private ArrayList<Topup> topups;

	private static MobileVikingsService instance;

	/**
	 * Create a new MV Service object. Password and username will need to be set
	 * with setCredentials(). The given context is used to create a cache.
	 */
	private MobileVikingsService(Context context) {
		this.cache = new Cache(context);
	}

	public static MobileVikingsService getInstance(Context context) {
		if (instance == null)
			instance = new MobileVikingsService(context);
		return instance;
	}

	public void setCredentials(String username, String password) {
		setUsername(username);
		setPassword(password);
	}

	public String getUsername() {
		return username;
	}

	private void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	private void setPassword(String password) {
		this.password = password;
	}

	public Credit getCredit() {
		return credit;
	}

	private void setCredit(Credit credit) {
		this.credit = credit;
	}

	public CallHistory getCallHistory() {
		return callHistory;
	}

	private void setCallHistory(CallHistory callHistory) {
		this.callHistory = callHistory;
	}

	public ArrayList<Topup> getTopups() {
		return topups;
	}

	private void setTopups(ArrayList<Topup> topups) {
		this.topups = topups;
	}

	/**
	 * Returns the GET response of the given url. Will use this.username and
	 * this.password.
	 * 
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	private String getResponse(String url) throws ClientProtocolException,
			IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getCredentialsProvider().setCredentials(
				new AuthScope(null, -1),
				new UsernamePasswordCredentials(username + ":" + password));
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);

		if (response.getEntity() != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();

			return sb.toString();
		}

		return "error";
	}

	/**
	 * Retrieves credit-related information.
	 * 
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws JSONException
	 */
	public void updateCredit() throws ClientProtocolException, IOException,
			JSONException {
		// Get data from MV API
		String response = getResponse(URL_CREDIT);
		parseCredit(response);
	}

	/**
	 * If possible, update the credit info from the cache.
	 * 
	 * @throws JSONException
	 */
	public void updateCreditFromCache() throws JSONException {
		String credit = cache.getCredit();
		if (credit != null)
			parseCredit(credit);
	}

	/**
	 * Parse a json-string (format: MV API).
	 * 
	 * @throws JSONException
	 */
	public void parseCredit(String json) throws JSONException {
		// Create JSONObject
		JSONObject jsonObject = new JSONObject(json);

		// Parse JSON
		Credit credit = new Credit(jsonObject);

		cache.putCredit(json);
		setCredit(credit);
	}

	/**
	 * Retrieves the call history of the day specified by the given calendar.
	 * 
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws JSONException
	 * @throws CallHistoryEmptyException
	 */
	public void updateCallHistory(Calendar calendar)
			throws ClientProtocolException, IOException, JSONException,
			CallHistoryEmptyException {
		// Construct URL
		String from = "from_date=" + DateParser.getAPI(calendar.getTime());

		Calendar toCalendar = (Calendar) calendar.clone();
		toCalendar.add(Calendar.DATE, 1);
		String to = "until_date=" + DateParser.getAPI(toCalendar.getTime());

		String url = URL_CALL_HISTORY + "?" + from + "&" + to
				+ "&page_size=200";

		// Get data
		String response = getResponse(url);

		parseCallHistory(response);
	}

	public void updateCallHistoryFromCache() throws JSONException,
			CallHistoryEmptyException {
		String callHistory = cache.getCallHistory();
		if (callHistory != null)
			parseCallHistory(callHistory);
	}

	public void parseCallHistory(String json) throws JSONException,
			CallHistoryEmptyException {
		JSONArray jsonCalls = new JSONArray(json);
		CallHistory history = new CallHistory(jsonCalls);

		cache.putCallHistory(json);
		setCallHistory(history);
	}

	/**
	 * Retrieves topup log.
	 * 
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws JSONException
	 */
	public void updateTopups() throws ClientProtocolException, IOException,
			JSONException {
		String response = getResponse(URL_TOPUPS);
		parseTopups(response);
	}

	public void updateTopupsFromCache() throws JSONException {
		String topups = cache.getTopups();
		if (topups != null)
			parseTopups(topups);
	}

	public void parseTopups(String json) throws JSONException {
		ArrayList<Topup> topups = new ArrayList<Topup>();
		JSONArray array = new JSONArray(json);
		for (int i = 0; i < array.length(); i++) {
			JSONObject t = array.getJSONObject(i);
			Topup topup = new Topup(t);
			topups.add(topup);
		}

		cache.putTopups(json);
		setTopups(topups);
	}

	public boolean hasCache() {
		return cache.hasCache();
	}

	public boolean isCreditCached() {
		return cache.hasCredit();
	}

	public boolean isCallHistoryCached() {
		return cache.hasCallHistory();
	}

	public boolean isTopupsCached() {
		return cache.hasTopups();
	}

}
