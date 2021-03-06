package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.utils.ActivityUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountAndSafetyActivity extends AppCompatActivity {

    @BindView(R.id.rl_update_phone)
    RelativeLayout rlUpdatePhone;
    @BindView(R.id.rl_update_password)
    RelativeLayout rlUpdatePassword;
    @BindView(R.id.rl_out_login)
    RelativeLayout rlOutLogin;
    @BindView(R.id.title)
    TitleView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_account_and_safety);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        title.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void OnLeftButtonClick() {
                finish();
            }
        });
    }

    @OnClick({R.id.rl_update_password, R.id.rl_update_phone, R.id.rl_out_login})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_update_password:
                startActivity(new Intent(this, UpdatePasswordActivity.class));
                break;
            case R.id.rl_update_phone:
                startActivity(new Intent(this, UpdatePhoneActivity.class));
                break;
            case R.id.rl_out_login:
                ActivityUtils.removeAllActivity(this, LoginActivity.class);
                break;
        }
    }
}
