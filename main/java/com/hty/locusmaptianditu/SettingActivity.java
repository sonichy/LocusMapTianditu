package com.hty.locusmaptianditu;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;

public class SettingActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	private EditTextPreference editText_uploadServer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
        editText_uploadServer = (EditTextPreference) findPreference("uploadServer");
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        editText_uploadServer.setSummary(sharedPreferences.getString("uploadServer", "http://sonichy.96.lt/locusmap/add.php"));
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("uploadServer")) {
            editText_uploadServer.setSummary(sharedPreferences.getString(key, "http://sonichy.96.lt/locusmap/add.php"));
		}
	}
}
