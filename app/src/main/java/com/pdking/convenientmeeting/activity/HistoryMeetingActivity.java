package com.pdking.convenientmeeting.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.utils.SystemUtil;

import butterknife.ButterKnife;

public class HistoryMeetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_history_meeting);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());

    }
}
