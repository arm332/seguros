package com.arm332.seguros2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;

import java.io.File;

// https://stackoverflow.com/q/7275015
// https://stackoverflow.com/q/18509369
// https://stackoverflow.com/q/5298370
// https://stackoverflow.com/a/5457349

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
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
        private GoogleSignInClient mGoogleSignInClient = null;

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
            String key = preference.getKey();

            if (SIGNOUT_KEY.equals(key)) {
                final FragmentActivity activity = getActivity();

                if (activity != null) {
                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.signout)
                            .setMessage(R.string.signout_message)
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .requestScopes(new Scope(DriveScopes.DRIVE_READONLY))
                                            .requestEmail()
                                            .build();
                                    mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
                                    mGoogleSignInClient.signOut()
                                            .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    File file = new File(activity.getFilesDir(), "data.csv");

                                                    if(file.exists()) {
                                                        if (file.delete()) {
                                                            Log.d(TAG, "Data deleted.");
                                                        }
                                                    }

                                                    // Reset preferences.
                                                    SharedPreferences.Editor editor = mPref.edit();
                                                    editor.clear();
                                                    editor.apply();

                                                    // Restart the app https://stackoverflow.com/a/46071063>.
                                                    // NOT WORKING...
//                                                    Intent intent = new Intent(activity, MainActivity.class);
//                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                    startActivity(intent);

                                                    // Close the app. See <https://stackoverflow.com/a/27765687>.
                                                    activity.finishAffinity();
                                                }
                                            });
                                }
                            })
                            .show();
                }
                return true;
            }

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
