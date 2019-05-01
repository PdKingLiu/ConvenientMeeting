package com.pdking.convenientmeeting.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pdking.convenientmeeting.R;

public class ScanMeetingMemberListActivity extends AppCompatActivity {

    private String meetingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scan_meeting_member_list);
        meetingId = getIntent().getStringExtra("meetingId");
    }
}
