package com.arm332.seguros2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.content.SharedPreferences;
import android.os.Bundle;

// https://stackoverflow.com/q/7275015
// https://stackoverflow.com/q/18509369

public class SettingsActivity extends AppCompatActivity {
    public static final String PASSWORD_KEY = "password_key";
    public static final String SPREADSHEET_KEY = "spreadsheet_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new MySettingsFragment())
                .commit();
    }

    public static class MySettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        private SharedPreferences mPref;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);
        }

        @Override
        public void onResume() {
            super.onResume();

            mPref = getPreferenceManager().getSharedPreferences();
            mPref.registerOnSharedPreferenceChangeListener(this);

            updateSummary(SPREADSHEET_KEY);
        }

        @Override
        public void onPause() {
            mPref.unregisterOnSharedPreferenceChangeListener(this);

            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (SPREADSHEET_KEY.equals(key)) {
                updateSummary(key);
            }
        }

        private void updateSummary(String key) {
            Preference p = findPreference(key);

            if (p != null) {
                p.setSummary(mPref.getString(key, ""));
            }
        }
    }
}
