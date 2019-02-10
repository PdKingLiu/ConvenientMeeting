package com.pdking.convenientmeeting.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.ActivityContainer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    /**
     * 注册按钮
     * */
    @BindView(R.id.bt_login_register)
    Button btRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        ButterKnife.bind(this);
        ActivityContainer.addActivity(this);
        applyPermission();
    }

    @OnClick(R.id.bt_login_register)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login_register:
                startActivity(new Intent(this,RegisterActivityOne.class));
                break;
        }
    }

    private void applyPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager
                .PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
                    .CAMERA, Manifest.permission.READ_PHONE_STATE}, 1);
        }
    }

}
