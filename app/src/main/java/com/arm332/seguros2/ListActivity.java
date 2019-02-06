package com.arm332.seguros2;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
    // private static final String TAG = "ListActivity";
    private static final Integer RC_SYNC = 1;
    private ListAdapter mAdapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        String[] list = {"Chelsea Morse", "Adrienne Larson", "Eldon Boyd", "Glenna Marshall", "Ora Vaughan"};
        mAdapter = new ListAdapter(this, list);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
        listView.setEmptyView(findViewById(R.id.textView));
        listView.setOnItemClickListener(this);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:

                return true;
            case R.id.action_sync:
                 Intent intent = new Intent(this, SyncActivity.class);
                 startActivityForResult(intent, RC_SYNC);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SYNC) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String result = data.getStringExtra(SyncActivity.EXTRA_RESULT);
                    // mListView.setAdapter(new ListAdapter(this, result));
                    System.out.println(result);
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
         Intent intent = new Intent(this, ItemActivity.class);
         intent.putExtra(ItemActivity.EXTRA_ITEM, item);
         startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
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
}
