package com.arm332.seguros2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.DriveScopes;

import java.util.Collections;

public class SyncActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SyncActivity";
    private static final Integer RC_SIGN_IN = 1;
    private SharedPreferences mPrefs;
    private TextView mTextView;
    private EditText mEditText;
    private SignInButton mSignInButton;
    private ProgressBar mProgressBar;
    private GoogleSignInClient mGoogleSignInClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String spreadsheetName = mPrefs.getString(SettingsActivity.SPREADSHEET_KEY,
                "Seguros - editar somente esse aqui Adriana");

        mTextView = findViewById(R.id.textView);
//        mTextView.setVisibility(View.GONE);

        mEditText = findViewById(R.id.editText);
        mEditText.setText(spreadsheetName);
//        mEditText.setVisibility(View.GONE);

        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(DriveScopes.DRIVE_READONLY))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        mSignInButton = findViewById(R.id.sign_in_button);
//        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mSignInButton.setOnClickListener(this);
//        mSignInButton.setVisibility(View.GONE);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            String spreadsheetName = mEditText.getText().toString().trim();

            if (!spreadsheetName.isEmpty()) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
            else {
                mEditText.setError(getString(R.string.spreadsheet_error));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Signed in successfully, show authenticated UI.
                updateUI(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                //
                // Status code = 12500
                //
                // Update Google Play Services to the latest version. See
                // <https://stackoverflow.com/a/47645136>.
                //
                // Check SHA-1 fingerprint certificate. Android Studio/Gradle/app/Tasks/android/signingReport.
                // See <https://stackoverflow.com/a/34223470>
                //
                // Or usint keytool. See <https://developers.google.com/android/guides/client-auth>.
                //
                // Can't find keytool. Use cd /Applications/Android Studio.app/Contents/jre/jdk/Contents/Home/bin
                // and ./keytool to run it. See <https://stackoverflow.com/a/2998451>.
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                updateUI(null);
            }
        }
    }

    public void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            String spreadsheetName = mEditText.getText().toString().trim();

            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(SettingsActivity.EMAIL_KEY, account.getEmail());
            editor.putString(SettingsActivity.SPREADSHEET_KEY, spreadsheetName);
            editor.apply();

            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(),
                    Collections.singleton(DriveScopes.DRIVE_READONLY));
            credential.setSelectedAccount(account.getAccount());

            new SyncTask(this, credential).execute(spreadsheetName);

            mTextView.setVisibility(View.GONE);
            mEditText.setVisibility(View.GONE);
            mSignInButton.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
        else {
            mTextView.setVisibility(View.VISIBLE);
            mEditText.setVisibility(View.VISIBLE);
            mSignInButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    // Used on SyncTask

    public void onSyncTaskComplete(String result) {
        if (result != null) {
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            updateUI(null);
        }
        else {
            Toast.makeText(this, R.string.update_successfully, Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        }
    }
}
