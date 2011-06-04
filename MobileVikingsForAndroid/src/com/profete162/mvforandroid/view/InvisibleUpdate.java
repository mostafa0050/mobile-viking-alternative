package com.profete162.mvforandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.profete162.mvforandroid.R;
import com.profete162.mvforandroid.util.MobileVikingsService;
import com.profete162.mvforandroid.view.widgets.MobileVikingsWidgetRegular;

public class InvisibleUpdate extends Activity {
	
	private MobileVikingsService service = MobileVikingsService
	.getInstance(this);
	
	private Dialog dialog;
	
	private SharedPreferences prefs;
	private Handler handler;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("DEBUG162","dans la boucle");
		System.out.println("dans la boucle");
		reinitService();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		handler = new Handler();
		showUpdateDialog();
		Log.i("DEBUG162","dans la boucle");
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
	}
	
	private void reinitService() {
		service.setCredentials(prefs.getString("username", ""), prefs
				.getString("password", ""));
	}
	
	private Runnable onUpdateCreditThreadFinishedRunnable = new Runnable() {
		
		public void run() {
			hideUpdateDialog();
			updateWidgets();
			scheduleAutoUpdate();
		}
	};
	
	private void hideUpdateDialog() {
		if (dialog != null)
			dialog.hide();
	}
	
	private void showUpdateDialog() {
		// hide existing dialog
		if (dialog != null)
			dialog.hide();

		// show new dialog
		dialog = ProgressDialog.show(this, "Updating", "Please wait...", true);
	}
		
	private Runnable onThreadFailedRunnable = new Runnable() {
		
		public void run() {
			hideUpdateDialog();
			Toast.makeText(InvisibleUpdate.this,
					R.string.update_failed_msg, Toast.LENGTH_LONG).show();
		}
	};
	
	private void updateWidgets() {
		// Action string is the same for all widget sizes, so one broadcast is
		// enough.
		Intent intent = new Intent(
				MobileVikingsWidgetRegular.MOBILEVIKINGS_WIDGET_UPDATE);
		sendBroadcast(intent);
	}
	
	private void scheduleAutoUpdate() {
		if (prefs.getBoolean("autoupdate", false)) {
			handler.removeCallbacks(autoUpdateRunnable);
			String interval = prefs.getString("update_interval", "86400");
			long intervalMillis = Long.parseLong(interval) * 1000;
			handler.postDelayed(autoUpdateRunnable, intervalMillis);
		}
	}
	
	private Runnable autoUpdateRunnable = new Runnable() {
		public void run() {
			updateCreditBackground();
		}
	};
	
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
	
	
	private Runnable onUpdateCreditBackgroundThreadFinishedRunnable = new Runnable() {
		
		public void run() {
			updateWidgets();
			scheduleAutoUpdate();
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i("DEBUG162","dans la boucle");

			
		}
}
	

