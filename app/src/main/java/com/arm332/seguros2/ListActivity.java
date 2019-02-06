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

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "ListActivity";
    private static final Integer RC_SYNC = 1;
    private ListView mListView;

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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
}
