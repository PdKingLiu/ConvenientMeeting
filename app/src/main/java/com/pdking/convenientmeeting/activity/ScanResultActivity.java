package com.pdking.convenientmeeting.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.pdking.convenientmeeting.R;

public class ScanResultActivity extends AppCompatActivity {

    private String QRresult;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scan_result);
        textView = findViewById(R.id.tv_result);
        QRresult = getIntent().getStringExtra("data");
        textView.setText(QRresult);
    }
}
