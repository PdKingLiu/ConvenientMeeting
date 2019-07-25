package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.livemeeting.openlive.ui.SettingsActivity;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeetingSettingActivity extends AppCompatActivity implements TitleView.LeftClickListener {

    @BindView(R.id.rl_live_setting)
    RelativeLayout rlLiveSetting;
    @BindView(R.id.title)
    TitleView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_meeting_setting);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        title.setLeftClickListener(this);
    }

    @OnClick(R.id.rl_live_setting)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_live_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }
}
