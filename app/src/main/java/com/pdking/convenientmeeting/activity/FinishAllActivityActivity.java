package com.pdking.convenientmeeting.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pdking.convenientmeeting.R;

public class FinishAllActivityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_finish_all_activity);
        finish();
    }
}
