package com.arm332.seguros2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {
    // private static final String TAG = "MainActivity";
    private SharedPreferences mPrefs = null;
    private String mPasswordHash = null;
    private EditText mPassword1;
    private EditText mPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // mPrefs = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPasswordHash = mPrefs.getString(SettingsActivity.PASSWORD_KEY, null);

        mPassword1 = findViewById(R.id.textView1);
        mPassword1.setOnEditorActionListener(this);

        mPassword2 = findViewById(R.id.textView2);
        mPassword2.setOnEditorActionListener(this);

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(this);

        TextView textView3 = findViewById(R.id.textView3);
        textView3.setOnClickListener(this);

        if (mPasswordHash != null) {
//            mPassword1.setImeOptions(EditorInfo.IME_ACTION_DONE);
            mPassword2.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onActionDone();
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                onActionDone();
                break;

            case R.id.textView3:
                Uri uri = Uri.parse(getString(R.string.version_url));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
    }

    private void onActionDone() {
        String password = mPassword1.getText().toString().trim();

        if (password.isEmpty()) {
            mPassword1.setError(getString(R.string.password_error));
            return;
        }

        String passwordHash = Utils.str2hex(password);

        if (mPasswordHash != null) {
            if (!mPasswordHash.equals(passwordHash)) {
                mPassword1.setError(getString(R.string.password_error));
                return;
            }
        }
        else {
            String confirmation = mPassword2.getText().toString().trim();

            if (!confirmation.equals(password)) {
                mPassword2.setError(getString(R.string.password_error));
                return;
            }

            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(SettingsActivity.PASSWORD_KEY, passwordHash);
            editor.apply();
        }

        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
        finish();
    }
}
