package com.arm332.seguros2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // private static final String TAG = "MainActivity";
    private SharedPreferences mPrefs = null;
    private String mPasswordHash = null;
    private EditText mPassword1;
    private EditText mPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
        mPasswordHash = mPrefs.getString("password_hash", null);
        mPassword1 = findViewById(R.id.password1);
        mPassword2 = findViewById(R.id.password2);
        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(this);

        if (mPasswordHash != null) {
            // mPassword1.setImeOptions(EditorInfo.IME_ACTION_DONE);
            mPassword2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
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
            editor.putString("password_hash", passwordHash);
            editor.apply();
        }

        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
        finish();
    }
}
