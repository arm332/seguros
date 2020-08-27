package com.arm332.seguros2;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
//    private static final String TAG = "ListActivity";
    private static final Integer RC_SYNC = 1;
    private ListView mListView;
    private SearchView mSearchView;

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
        //            case R.id.action_search:
        //                return true;
        //            case R.id.action_settings:
        //                startActivity(new Intent(this, SettingsActivity.class));
        //                return true;
        if (item.getItemId() == R.id.action_sync) {
            Intent intent = new Intent(this, SyncActivity.class);
            startActivityForResult(intent, RC_SYNC);
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
//                if (data != null) {
//                    String result = data.getStringExtra(SyncActivity.EXTRA_RESULT);
//                    mListView.setAdapter(new ListAdapter(this, result));
//                    System.out.println(result);
//                }
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
//            mSearchView.setIconified(true);
            mSearchView.onActionViewCollapsed();
        } else {
            super.onBackPressed();
        }

    }
}
