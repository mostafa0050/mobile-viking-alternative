package com.profete162.mvforandroid.view;

import java.io.File;
import java.io.FileOutputStream;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.widget.Toast;

import com.profete162.mvforandroid.R;
import com.profete162.mvforandroid.util.Codes;

public class Settings extends PreferenceActivity implements
		OnPreferenceChangeListener {

	SharedPreferences.Editor editor;
	static SharedPreferences settings;
	public static final String ROOT = "/sdcard/data/MobileVikings";

	private CheckBoxPreference skin;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		PackageManager manager = this.getPackageManager();
		String maVersion = "";
		settings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		editor = settings.edit();

		skin = (CheckBoxPreference) findPreference("prefskin");
		skin.setOnPreferenceChangeListener(this);

		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			maVersion = (info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		String laVersion = settings.getString("pVersion", "X");

		System.out.println("Version App= " + laVersion
				+ " - Version actuelle= " + maVersion);

		if (!maVersion.equals(laVersion)) {
			Codes.WhatsNew(Settings.this, this);

		}

		editor.putString("pVersion", maVersion);
		// Don't forget to commit your edits!!!
		editor.commit();
	}

	
	public boolean onPreferenceChange(Preference preference, Object arg1) {
		// TODO Auto-generated method stub

		new File(ROOT).mkdirs();

		if (preference == skin)
			if (!skin.isChecked()) {
				Bitmap bitmap = BitmapFactory.decodeFile(ROOT + "/widget.png");
				if (bitmap == null) {

					try {
						Bitmap mBitmap = BitmapFactory.decodeResource(
								getResources(), R.drawable.appwidget_bg_glass);
						FileOutputStream out = new FileOutputStream(ROOT
								+ "/widget.png");
						mBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
						skin.setChecked(true);
						Toast.makeText(this,
								"You may now edit widget.png in" + ROOT,
								Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(this,
								"Please copy widget.png in" + ROOT,
								Toast.LENGTH_SHORT).show();
					}
				}

				else
					skin.setChecked(true);

			} else
				skin.setChecked(false);
		return false;
	}
}
