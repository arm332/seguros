package com.arm332.seguros2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.arm332.seguros2.extra.ID";
    //private static final String TAG = "ItemActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        long id = getIntent().getLongExtra(EXTRA_ID, 0);
        String html = Utils.getItem(this, (int) id);
        TextView textView = findViewById(R.id.textView);
        textView.setText(Utils.fromHtml(html));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
