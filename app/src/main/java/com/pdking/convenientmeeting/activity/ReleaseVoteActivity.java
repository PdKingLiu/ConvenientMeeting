package com.pdking.convenientmeeting.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.utils.SystemUtil;

import butterknife.ButterKnife;

public class ReleaseVoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_release_vote);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
    }
}
