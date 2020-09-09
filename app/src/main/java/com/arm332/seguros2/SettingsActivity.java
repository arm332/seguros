package com.arm332.seguros2;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

// https://stackoverflow.com/q/7275015
// https://stackoverflow.com/q/18509369
// https://stackoverflow.com/q/5298370
// https://stackoverflow.com/a/5457349

public class SettingsActivity extends AppCompatActivity {
//    private static final String TAG = "SettingsActivity";
    public static final String PASSWORD_KEY = "password_key";
    public static final String EMAIL_KEY = "email_key";
    public static final String SPREADSHEET_KEY = "spreadsheet_key";
    public static final String SIGNOUT_KEY = "signout_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new MySettingsFragment())
                .commit();
    }

    public static class MySettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
        private SharedPreferences mPref;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);

            Preference pref = findPreference(SIGNOUT_KEY);

            if (pref != null) {
                pref.setOnPreferenceClickListener(this);
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            mPref = getPreferenceManager().getSharedPreferences();
            mPref.registerOnSharedPreferenceChangeListener(this);

            updateSummary(EMAIL_KEY);
            updateSummary(SPREADSHEET_KEY);
        }

        @Override
        public void onPause() {
            mPref.unregisterOnSharedPreferenceChangeListener(this);

            super.onPause();
        }


        @Override
        public boolean onPreferenceClick(Preference preference) {
//             TODO: change spreadsheet?
//            String key = preference.getKey();
//            if (SPREADSHEET_KEY.equals(key)) {
//                return true;
//            }
            return false;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (EMAIL_KEY.equals(key) || SPREADSHEET_KEY.equals(key)) {
                updateSummary(key);
            }
        }

        private void updateSummary(String key) {
            Preference pref = findPreference(key);

            if (pref != null) {
                pref.setSummary(mPref.getString(key, ""));
            }
        }
    }
}
