package com.hty.locusmaptianditu;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;

public class SettingActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private EditTextPreference ETP_uploadServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ETP_uploadServer = (EditTextPreference) findPreference("uploadServer");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        String uploadServer = sharedPreferences.getString("uploadServer", MainApplication.getInstance().uploadServer);
        if (uploadServer.equals(""))
            uploadServer = MainApplication.getInstance().uploadServer;
        ETP_uploadServer.setSummary(uploadServer);
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
            ETP_uploadServer.setSummary(sharedPreferences.getString(key, MainApplication.getInstance().uploadServer));
        }
    }
}
