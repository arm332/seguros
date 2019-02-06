package com.arm332.seguros2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SyncActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_RESULT = "com.arm332.seguros2.EXTRA_RESULT";

    private static final String TAG = "SyncActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT, "RESULT");

        setResult(RESULT_OK, intent);
        finish();
    }
}
