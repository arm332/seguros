package com.arm332.seguros2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MasterActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "MasterActivity";
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("onItemClick");
    }
}
