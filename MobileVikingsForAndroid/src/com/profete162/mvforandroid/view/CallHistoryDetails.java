package com.profete162.mvforandroid.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.profete162.mvforandroid.R;
import com.profete162.mvforandroid.data.Call;
import com.profete162.mvforandroid.util.AndroidUtils;
import com.profete162.mvforandroid.util.DateParser;
import com.profete162.mvforandroid.util.MobileVikingsService;


public class CallHistoryDetails extends Activity {

	private MobileVikingsService service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.service = MobileVikingsService.getInstance(this
				.getApplicationContext());
		setContentView(R.layout.callog_detail);

		String type = getIntent().getStringExtra("type");

		if (type.equals("sms")) {
			showSmsDetails();
		} else if (type.equals("mms")) {
			showMmsDetails();
		} else if (type.equals("data")) {
			showDataDetails();
		} else if (type.equals("voice")) {
			showVoiceDetails();
		}

	}

	private void showVoiceDetails() {
		setTitle("Voice History for "
				+ DateParser.getDisplayStringDate(service.getCallHistory()
						.getDate().getTime()));

		ArrayList<Call> calls = service.getCallHistory().getVoiceCalls();

		ListView lv = (ListView) findViewById(R.id.callog_detail_listview);
		VoiceAdapter adapter = new VoiceAdapter(this.getApplicationContext(),
				calls);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(itemClickListener);

	}

	private void showDataDetails() {
		setTitle("Data History for "
				+ DateParser.getDisplayStringDate(service.getCallHistory()
						.getDate().getTime()));
		ArrayList<Call> calls = service.getCallHistory().getDataCalls();

		ListView lv = (ListView) findViewById(R.id.callog_detail_listview);
		DataAdapter adapter = new DataAdapter(this.getApplicationContext(),
				calls);
		lv.setAdapter(adapter);
	}

	private void showMmsDetails() {
		setTitle("MMS History for "
				+ DateParser.getDisplayStringDate(service.getCallHistory()
						.getDate().getTime()));
		ArrayList<Call> calls = service.getCallHistory().getMmsCalls();

		ListView lv = (ListView) findViewById(R.id.callog_detail_listview);
		SmsAdapter adapter = new SmsAdapter(this.getApplicationContext(), calls);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(itemClickListener);
	}

	private void showSmsDetails() {
		setTitle("SMS History for "
				+ DateParser.getDisplayStringDate(service.getCallHistory()
						.getDate().getTime()));
		ArrayList<Call> calls = service.getCallHistory().getSmsCalls();

		ListView lv = (ListView) findViewById(R.id.callog_detail_listview);
		SmsAdapter adapter = new SmsAdapter(this.getApplicationContext(), calls);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(itemClickListener);
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			Call call = (Call) arg0.getAdapter().getItem(arg2);
			String id = AndroidUtils.getContactIdFromNumber(
					CallHistoryDetails.this, call.getTo());

			if (!id.equals("")) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.withAppendedPath(
						Contacts.People.CONTENT_URI, Uri.encode(id)));
				startActivity(intent);
			} else {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" + call.getTo()));
				startActivity(intent);
			}

		}

	};
}
