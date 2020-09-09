package com.arm332.seguros2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;

import java.io.File;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
    private static final String TAG = "ListActivity";
    private static final Integer RC_SYNC = 1;
    private ListView mListView;
    private SearchView mSearchView;
    private GoogleSignInClient mGoogleSignInClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mListView = findViewById(R.id.listView);
        mListView.setAdapter(new ListAdapter(this));
        mListView.setEmptyView(findViewById(R.id.textView));
        mListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        System.out.println("count: " + mListView.getCount());
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible((mListView.getCount() != 0));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if (item.getItemId() == R.id.action_search) {
        //        return true;
        //}

        if (item.getItemId() == R.id.action_sync) {
            Intent intent = new Intent(this, SyncActivity.class);
            startActivityForResult(intent, RC_SYNC);
            return true;
        }

        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (item.getItemId() == R.id.action_signout) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SYNC) {
            if (resultCode == RESULT_OK) {
                // TODO: Check for error messages
                //if (data != null) {
                //    String result = data.getStringExtra(SyncActivity.EXTRA_RESULT);
                //    mListView.setAdapter(new ListAdapter(this, result));
                //    System.out.println(result);
                //}
                mListView.setAdapter(new ListAdapter(this));
                invalidateOptionsMenu();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra(ItemActivity.EXTRA_ID, id);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ListAdapter adapter = (ListAdapter) mListView.getAdapter();
        adapter.getFilter().filter(newText);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mSearchView.isIconified()) {
            //mSearchView.setIconified(true);
            mSearchView.onActionViewCollapsed();
        } else {
            super.onBackPressed();
        }
    }

    // Sign user out

    private void signOut() {
        new AlertDialog.Builder(this)
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

                        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
                        mGoogleSignInClient.signOut()
                                .addOnCompleteListener(ListActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        File file = new File(ListActivity.this.getFilesDir(), "data.csv");

                                        if (file.exists()) {
                                            if (!file.delete()) {
                                                Log.d(TAG, "Could NOT delete data file.");
                                            }
                                        }

                                        // Reset preferences.
                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.clear();
                                        editor.apply();

                                        // Clear memory.
                                        mListView.setAdapter(new ListAdapter(getApplicationContext()));

                                        // Restart the app https://stackoverflow.com/a/46071063>.
                                        // NOT WORKING...
                                        //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        //startActivity(intent);

                                        // Close the app. See <https://stackoverflow.com/a/27765687>.
                                        //finishAffinity();
                                        finish();
                                    }
                                });
                    }
                })
                .show();
    }
}
