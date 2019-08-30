package com.arm332.seguros2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.arm332.seguros2.extra.ID";
    private static final String TAG = "ItemActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Long id = getIntent().getLongExtra(EXTRA_ID, 0);
        String html = Utils.getItem(this, id.intValue());
        TextView textView = findViewById(R.id.textView);
        textView.setText(Utils.fromHtml(html));
    }
}
