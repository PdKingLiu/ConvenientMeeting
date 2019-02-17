package com.pdking.convenientmeeting.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.ActivityContainer;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.utils.SystemUtil;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    /**
     * 注册按钮
     */
    @BindView(R.id.bt_login_register)
    Button btRegister;
    @BindView(R.id.bt_login_find_password)
    Button btFindPassword;
    private UserInfo userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
        ActivityContainer.addActivity(this);
        applyPermission();
        isFirst();
    }


    @OnClick({R.id.bt_login_register,R.id.bt_login_find_password})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login_register:
                startActivity(new Intent(this, RegisterActivityOne.class));
                break;
            case R.id.bt_login_find_password:
                startActivity(new Intent(this, FindPasswordActivity.class));
                break;
        }
    }

    private void applyPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager
                .PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
                    .CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    public void isFirst() {
        LitePal.getDatabase();
        if (LitePal.findAll(UserInfo.class).size() != 0) {
            userInfo = LitePal.findAll(UserInfo.class).get(0);
            Log.d("Lpp", "isFirst: "+userInfo);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", userInfo);
            startActivity(intent);
            ActivityContainer.removeAllActivity();
        }
    }
}
