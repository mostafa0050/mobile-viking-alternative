package com.profete162.mvforandroid.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.profete162.mvforandroid.R;
import com.profete162.mvforandroid.data.CallHistory;
import com.profete162.mvforandroid.data.Topup;
import com.profete162.mvforandroid.exceptions.CallHistoryEmptyException;
import com.profete162.mvforandroid.util.Codes;
import com.profete162.mvforandroid.util.DateParser;
import com.profete162.mvforandroid.util.MobileVikingsService;
import com.profete162.mvforandroid.view.widgets.MobileVikingsWidgetRegular;

public class MobileVikingsForAndroid extends TabActivity {

	String action;
	private Handler handler;
	private Dialog dialog;
	private MobileVikingsService service = MobileVikingsService
			.getInstance(this);
	private SharedPreferences prefs;

	SharedPreferences.Editor editor;
	static SharedPreferences settings;

	private Calendar callHistoryDate;

	Context context;

	private final int FIRST_TIME_SETTINGS = 112358;
	private final int SETTINGS = 12345;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setNoTitle();
		setContentView(R.layout.main);

		context = this;

		settings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		editor = settings.edit();
		final Intent intent = getIntent();
		action = intent.getAction();

		// General setup
		handler = new Handler();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		callHistoryDate = GregorianCalendar.getInstance();

		// Setup tabs
		TabHost tabhost = getTabHost();
		tabhost.addTab(tabhost.newTabSpec("tab_overview").setIndicator(
				getString(R.string.tab_credit)).setContent(R.id.tab_credit));
		tabhost.addTab(tabhost.newTabSpec("tab_callhistory").setIndicator(
				getString(R.string.tab_history)).setContent(R.id.tab_history));
		tabhost.addTab(tabhost.newTabSpec("tab_topuphistory").setIndicator(
				getString(R.string.tab_topups)).setContent(R.id.tab_topups));
		tabhost.setCurrentTab(0);

		// Setup update buttons
		Button updateCreditButton = (Button) findViewById(R.id.credit_update_button);
		updateCreditButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				updateCredit();
			}
		});

		Button updateHistoryButton = (Button) findViewById(R.id.history_update_button);
		updateHistoryButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				new DatePickerDialog(MobileVikingsForAndroid.this,
						dateSetListener, callHistoryDate.get(Calendar.YEAR),
						callHistoryDate.get(Calendar.MONTH), callHistoryDate
								.get(Calendar.DATE)).show();
			}
		});

		Button updateTopupsButton = (Button) findViewById(R.id.topups_update_button);
		updateTopupsButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				updateTopups();
			}
		});

		// Setup call history listview
		ListView lv = (ListView) findViewById(R.id.listview_callhistory);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long itemId) {

				Intent intent = new Intent(MobileVikingsForAndroid.this,
						CallHistoryDetails.class);

				switch ((int) itemId) {
				case 0:
					intent.putExtra("type", "sms");
					break;
				case 1:
					intent.putExtra("type", "mms");
					break;
				case 2:
					intent.putExtra("type", "data");
					break;
				case 3:
					intent.putExtra("type", "voice");
					break;
				}

				startActivity(intent);
			}
		});

		// Start new thread to update the views for which data was cached, but
		// only if username and password are filled in. If not, open Preferences
		// activity.
		String username = prefs.getString("username", "");
		String password = prefs.getString("password", "");

		if (username.equals("") || password.equals("")) {
			// No account details filled in yet, so show Settings.
			startActivityForResult(new Intent(this.getApplicationContext(),
					com.profete162.mvforandroid.view.Settings.class),
					FIRST_TIME_SETTINGS);
			if (checkForUpdate(false))
				Toast.makeText(this.getApplicationContext(),
						"Please Insert your Username and Password.",
						Toast.LENGTH_LONG).show();
		} else {
			if (!checkForUpdate(true))
				Codes.WhatsNew(MobileVikingsForAndroid.this, this);

			if (service.hasCache()) {
				// API data has been cached, so use it.

				updateAllFromCache();
			}
		}

	}

	/**
	 * Callback when an activity launched from this activity is finished. Is
	 * only used to update MV data when settings have been changed the first
	 * time.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// See which child activity is calling back.
		switch (requestCode) {
		case FIRST_TIME_SETTINGS:
			// At the first time the user returns from the Settings screen to
			// the main Activity, we assume he wants an update. Also we will
			// schedule an autoupdate.
			// updateCredit();
			scheduleAutoUpdate();
			break;
		case SETTINGS:
			// Return from settings, so reschedule autoupdate.
			scheduleAutoUpdate();
			break;
		default:
			break;
		}
	}

	protected boolean checkForUpdate(boolean writeSettings) {

		// Toast.makeText(this, action, Toast.LENGTH_SHORT).show();
		PackageManager manager = this.getPackageManager();
		String maVersion = "";
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			// String packageName = info.packageName;
			// int versionCode = info.versionCode;
			// String versionName = info.versionName;
			maVersion = (info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		String laVersion = settings.getString("pVersion", "X");
		
		System.out.println("Version App= " + laVersion
				+ " - Version actuelle= " + maVersion);
		
		if (writeSettings) {
			editor.putString("pVersion", maVersion);
			editor.commit();
		}

		return maVersion.equals(laVersion);
	}

	/**
	 * Used when opening this activity from the widget. (Or any intent with
	 * action "UPDATE_CREDIT".)
	 */
	@Override
	protected void onResume() {
		super.onResume();

		if (prefs.getBoolean("update_on_widget_open", false)) {
			Intent i = getIntent();
			if (i.getAction() != null && i.getAction().equals("UPDATE_CREDIT")) {
			//if (true) {
				i.setAction("CREDIT_UPDATED");

				if (prefs.getBoolean("show_windows", true)) {
					Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT)
							.show();
					updateCredit();
					finish();
				} else
					updateCredit();

			}
		}

	}

	/**
	 * Called when the menu is opened.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	/**
	 * Called when a menu item is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_menu_settings:
			menuSettingsSelected();
			break;
		case R.id.main_menu_udpateall:
			updateAll();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * The callback received when the user chooses a date in the date picker
	 * dialog.
	 */
	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Calendar c = new GregorianCalendar(year, monthOfYear, dayOfMonth);
			MobileVikingsForAndroid.this.callHistoryDate = c;
			updateCallHistory();
		}
	};

	/**
	 * Called when Settings is selected in the menu.
	 */
	private void menuSettingsSelected() {
		startActivityForResult(new Intent(this.getApplicationContext(),
				com.profete162.mvforandroid.view.Settings.class), SETTINGS);
	}

	/**
	 * Called when the Update button on the credits tab is clicked.
	 */
	private void updateCredit() {
		reinitService();
		showUpdateDialog();
		Thread thread = new Thread() {
			public void run() {
				try {
					service.updateCredit();
					handler.post(onUpdateCreditThreadFinishedRunnable);
				} catch (Exception e) {
					e.printStackTrace();
					handler.post(onThreadFailedRunnable);
				}
			};
		};
		thread.start();
		// finish();
	}

	/**
	 * Called when the Update button on the credits tab is clicked.
	 */
	private void updateCreditBackground() {
		reinitService();
		Thread thread = new Thread() {
			public void run() {
				try {
					service.updateCredit();
					handler
							.post(onUpdateCreditBackgroundThreadFinishedRunnable);
				} catch (Exception e) {
					e.printStackTrace();
					handler.post(onThreadFailedRunnable);
				}
			};
		};
		thread.start();
	}

	/**
	 * Updates the textview and progressbars with new credit data from the
	 * service.
	 */
	private void updateCreditView() {
		TextView credit = (TextView) findViewById(R.id.text_credit);
		credit.setText(service.getCredit().getCredits() + " €");

		ProgressBar sms = (ProgressBar) findViewById(R.id.progress_sms);
		sms.setMax(1000);
		sms.setProgress(service.getCredit().getSms());
		TextView smsText = (TextView) findViewById(R.id.text_sms);
		smsText.setText(service.getCredit().getSms() + " / 1000");

		ProgressBar data = (ProgressBar) findViewById(R.id.progress_data);
		data.setMax(2048);
		data.setProgress(service.getCredit().getDataInMegabytes());
		TextView dataText = (TextView) findViewById(R.id.text_data);
		dataText.setText(service.getCredit().getDataInMegabytes() + " / 2048");

		TextView valid = (TextView) findViewById(R.id.text_valid);
		valid.setText(service.getCredit().getValidUntilString());
	}

	/**
	 * Called when the Update button on the history tab is clicked.
	 */
	private void updateCallHistory() {
		reinitService();
		showUpdateDialog();
		Thread thread = new Thread() {
			public void run() {
				try {
					service.updateCallHistory(callHistoryDate);
					handler.post(onUpdateCallHistoryThreadFinishedRunnable);
				} catch (CallHistoryEmptyException ce) {
					handler.post(onEmptyCallHistoryRunnable);
				} catch (Exception e) {
					e.printStackTrace();
					handler.post(onThreadFailedRunnable);
				}
			};
		};
		thread.start();
	}

	/**
	 * Updates the listview with the new call history.
	 */
	private void updateCallHistoryView() {
		CallHistory history = service.getCallHistory();
		Button updateButton = (Button) findViewById(R.id.history_update_button);
		updateButton.setText("History for "
				+ DateParser.getDisplayStringDate(history.getDate().getTime()));

		ArrayList<String> categories = new ArrayList<String>();
		categories.add("SMS (" + history.getSmsCalls().size() + ")");
		categories.add("MMS (" + history.getMmsCalls().size() + ")");
		categories.add("Data (" + history.getDataCalls().size() + ")");
		categories.add("Voice (" + history.getVoiceCalls().size() + ")");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.simple_list_item_1, categories);

		ListView lv = (ListView) findViewById(R.id.listview_callhistory);
		lv.setAdapter(adapter);
		lv.invalidate();
	}

	/**
	 * Called when the Update button on the topups tab is clicked.
	 */
	private void updateTopups() {
		reinitService();
		showUpdateDialog();
		Thread thread = new Thread() {
			public void run() {
				try {
					service.updateTopups();
					handler.post(onUpdateTopupsThreadFinishedRunnable);
				} catch (Exception e) {
					e.printStackTrace();
					handler.post(onThreadFailedRunnable);
				}
			};
		};
		thread.start();
	}

	/**
	 * Updates the listview for the topups.
	 */
	private void updateTopupsView() {
		ArrayList<Topup> topups = service.getTopups();

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (Topup t : topups) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("amount", "€ " + t.getAmount());
			map.put("date", DateParser.getDisplayStringDate(t.getDate()));
			map.put("method", "method : " + t.getMethod());
			map.put("status", "status : " + t.getStatus());
			list.add(map);
		}

		String[] from = { "amount", "date", "method", "status" };
		int[] to = { R.id.topup_detail_amount, R.id.topup_detail_date,
				R.id.topup_detail_method, R.id.topup_detail_status };

		ListView lv = (ListView) findViewById(R.id.listview_topups);
		SimpleAdapter adapter = new SimpleAdapter(this.getApplicationContext(),
				list, R.layout.topup_detail, from, to);
		lv.setAdapter(adapter);
	}

	/**
	 * Updates credit, call history (for the currently set date) and topup
	 * history.
	 */
	private void updateAll() {
		reinitService();
		showUpdateDialog();
		Thread thread = new Thread() {
			public void run() {
				try {
					service.updateCredit();
					service.updateCallHistory(callHistoryDate);
					service.updateTopups();
					handler.post(onUpdateAllThreadFinishedRunnable);
				} catch (Exception e) {
					e.printStackTrace();
					handler.post(onThreadFailedRunnable);
				}
			};
		};
		thread.start();
	}

	/**
	 * Update view with all data that has been cached.
	 */
	private void updateAllFromCache() {
		Thread thread = new Thread() {
			public void run() {
				try {
					service.updateCreditFromCache();
					service.updateCallHistoryFromCache();
					service.updateTopupsFromCache();
					handler.post(onUpdateAllFromCacheThreadFinishedRunnable);
				} catch (Exception e) {
					e.printStackTrace();
					handler.post(onThreadFailedRunnable);
				}
			};
		};
		thread.start();
	}

	/**
	 * Updates username and password to account for possible changes in the
	 * Preferences.
	 */
	private void reinitService() {
		service.setCredentials(prefs.getString("username", ""), prefs
				.getString("password", ""));
	}

	/**
	 * Shows a dialog informing the user that an update is in progress. Should
	 * be hidden by calling hideUpdateDialog() from a thread callback.
	 */
	private void showUpdateDialog() {
		// hide existing dialog
		if (dialog != null)
			dialog.hide();

		// show new dialog
		dialog = ProgressDialog.show(this, "Updating", "Please wait...", true);
	}

	/**
	 * Hides the update dialog. Should be called from a thread callback.
	 */
	private void hideUpdateDialog() {
		if (dialog != null)
			dialog.hide();
	}

	/**
	 * Called when any of the update threads has failed, i.e. credit, call
	 * history or topups.
	 */
	private Runnable onThreadFailedRunnable = new Runnable() {
		
		public void run() {
			hideUpdateDialog();

			AlertDialog.Builder ad;
			ad = new AlertDialog.Builder(context);
			ad.setTitle("Warning");
			ad.setMessage(context.getString(R.string.update_failed_msg));
			ad.setPositiveButton(android.R.string.ok,
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {

							startActivity(new Intent(
									MobileVikingsForAndroid.this,
									com.profete162.mvforandroid.view.Settings.class));

						}
					});

			ad.setNegativeButton(android.R.string.no,
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
						}
					});
			ad.show();

		}
	};

	/**
	 * Called when the call history is empty (specifically, when a
	 * {@link CallHistoryEmptyException} is thrown/
	 */
	private Runnable onEmptyCallHistoryRunnable = new Runnable() {
		
		public void run() {
			hideUpdateDialog();
			Toast.makeText(MobileVikingsForAndroid.this,
					R.string.no_callhistory_msg, Toast.LENGTH_LONG).show();
		}
	};

	/**
	 * Called when the thread that updates the credit is finished.
	 */
	private Runnable onUpdateCreditThreadFinishedRunnable = new Runnable() {
		
		public void run() {
			hideUpdateDialog();
			updateCreditView();
			updateWidgets();
			scheduleAutoUpdate();
		}
	};

	/**
	 * Called when the thread that updates the credit is finished.
	 */
	private Runnable onUpdateCreditBackgroundThreadFinishedRunnable = new Runnable() {
		
		public void run() {
			updateCreditView();
			updateWidgets();
			scheduleAutoUpdate();
		}
	};

	/**
	 * Called when the thread that updates the call history is finished.
	 */
	private Runnable onUpdateCallHistoryThreadFinishedRunnable = new Runnable() {
		
		public void run() {
			hideUpdateDialog();
			updateCallHistoryView();
		}
	};

	/**
	 * Called when the thread that updates the topups is finished.
	 */
	private Runnable onUpdateTopupsThreadFinishedRunnable = new Runnable() {
		
		public void run() {
			hideUpdateDialog();
			updateTopupsView();
		}
	};

	/**
	 * Called when the thread that updates everything is finished.
	 */
	private Runnable onUpdateAllThreadFinishedRunnable = new Runnable() {
		
		public void run() {
			hideUpdateDialog();
			if (service.getCredit() != null) {
				updateCreditView();
				updateWidgets();
				scheduleAutoUpdate();
			}
			if (service.getCallHistory() != null) {
				updateCallHistoryView();
			}
			if (service.getTopups() != null) {
				updateTopupsView();
			}
		}
	};

	/**
	 * Called when the thread that updates everything from cache is finished.
	 * The only difference between this and its non-cached version
	 * (onUpdateAllThreadFinishedRunnable) is that this one doesn't schedule a
	 * new auto update.
	 */
	private Runnable onUpdateAllFromCacheThreadFinishedRunnable = new Runnable() {
		
		public void run() {
			if (service.getCredit() != null) {
				updateCreditView();
				//updateWidgets();
			}
			if (service.getCallHistory() != null) {
				updateCallHistoryView();
			}
			if (service.getTopups() != null) {
				updateTopupsView();
			}
		}
	};

	/**
	 * Broadcast intents to update all widgets.
	 */
	private void updateWidgets() {
		// Action string is the same for all widget sizes, so one broadcast is
		// enough.
		
		Toast.makeText(MobileVikingsForAndroid.this,
				"Update OK", Toast.LENGTH_SHORT).show();
		
		Intent intent = new Intent(
				MobileVikingsWidgetRegular.MOBILEVIKINGS_WIDGET_UPDATE);
		sendBroadcast(intent);
		
	}

	/**
	 * Schedules an autoupdate, if allowed in the preferences.
	 */
	private void scheduleAutoUpdate() {
		if (prefs.getBoolean("autoupdate", false)) {
			handler.removeCallbacks(autoUpdateRunnable);
			String interval = prefs.getString("update_interval", "86400");
			long intervalMillis = Long.parseLong(interval) * 1000;
			handler.postDelayed(autoUpdateRunnable, intervalMillis);
		}
	}

	/**
	 * Does not schedule a new auto update, that is handled by the callbacks
	 * onUpdateCreditThreadFinishedRunnable and
	 * onUpdateAllThreadFinishedRunnable. In other words, a new auto update is
	 * only scheduled when the credits have been successfully updated (auto or
	 * manual).
	 */
	private Runnable autoUpdateRunnable = new Runnable() {
		public void run() {
			updateCreditBackground();
		}
	};

	public void setFullscreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	public void setNoTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
}