package com.arm332.seguros2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity {
    public static final String EXTRA_ITEM = "com.arm332.seguros2.EXTRA_ITEM";

    private static final String TAG = "ItemActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        String text = getIntent().getStringExtra(EXTRA_ITEM);
        TextView textView = findViewById(R.id.textView);
        textView.setText(text);
    }
}
