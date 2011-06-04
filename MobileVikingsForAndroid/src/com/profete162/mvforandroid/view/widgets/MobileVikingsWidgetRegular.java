package com.profete162.mvforandroid.view.widgets;

import org.json.JSONException;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.profete162.mvforandroid.R;
import com.profete162.mvforandroid.data.Credit;
import com.profete162.mvforandroid.util.MobileVikingsService;
import com.profete162.mvforandroid.view.MobileVikingsForAndroid;
import com.profete162.mvforandroid.view.Settings;

public class MobileVikingsWidgetRegular extends AppWidgetProvider {

	public static final String MOBILEVIKINGS_WIDGET_UPDATE = "MOBILEVIKINGS_WIDGET_UPDATE";
	Bitmap bitmap;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		int amount = appWidgetIds.length;
		System.out.println("** OnUpdate 2**");
		

		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < amount; i++) {
			int appWidgetId = appWidgetIds[i];
			
			bitmap = BitmapFactory.decodeFile(Settings.ROOT + "/widget.png");
			if (bitmap == null)
				Toast.makeText(
						context,
						"Mobile Vikings 2x1:\n\nYou have choosen to use a custom Skin\nPlease create widget.png in"
						+ Settings.ROOT, Toast.LENGTH_LONG).show();

			// Create an Intent to launch main Activity
			Intent intent = new Intent(context, MobileVikingsForAndroid.class);
			intent.setAction("UPDATE_CREDIT");
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					intent, 0);
			
			Uri telUri = Uri.parse("tel:");
			intent = new Intent(Intent.ACTION_DIAL, telUri);
			PendingIntent pendingIntentPhone = PendingIntent.getActivity(context, 0,
					intent, 0);
			

			RemoteViews views = null;
			// Get the layout for the App Widget and attach an on-click listener
			views = new RemoteViews(context.getPackageName(),
					R.layout.widget_2x1);
			views.setOnClickPendingIntent(R.id.widgetFrame, pendingIntent);
			views.setOnClickPendingIntent(R.id.control_phone, pendingIntentPhone);

			updateTextViews(context, views);

			// Tell the AppWidgetManager to perform an update on the current App
			// Widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("** RECEIVE 2**");
		super.onReceive(context, intent);
		if (intent.getAction().equals(MOBILEVIKINGS_WIDGET_UPDATE)) {
						
			int[] ids = AppWidgetManager.getInstance(context)
					.getAppWidgetIds(
							new ComponentName(context,
									MobileVikingsWidgetRegular.class));
			onUpdate(context, AppWidgetManager.getInstance(context), ids);
		}
		this.getClass().getName();
	}

	public void updateTextViews(Context context, RemoteViews views) {
		// Get credit (if necessary, get it from the cache)
		
		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				"prefskin", false)) {
			if (bitmap != null)
				views.setImageViewBitmap(R.id.skinable, bitmap);
			else {
				views.setImageViewResource(R.id.skinable,
						R.drawable.appwidget_bg);

			}
		}

		else
			views.setImageViewResource(R.id.skinable, R.drawable.appwidget_bg);
		
		MobileVikingsService service = MobileVikingsService
				.getInstance(context);
		Credit credit = service.getCredit();
		if (credit == null) {
			try {
				service.updateCreditFromCache();
			} catch (JSONException e) {
				Log.e("MVFA", e.getMessage(), e);
			}
			credit = service.getCredit();
		}



		       
		// Update text views
		if (credit != null) {
			views.setTextViewText(R.id.widgetCredits, credit.getCredits()+" €");
			views.setTextViewText(R.id.widgetSms, ""+credit.getSms()+" SMS");
			views.setTextViewText(R.id.widgetData, ""+credit.getDataInMegabytes()+" MB");
		}
	}
}
