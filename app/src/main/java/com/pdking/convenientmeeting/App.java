package com.pdking.convenientmeeting;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.pdking.convenientmeeting.common.Constant;

import org.litepal.LitePal;

/**
 * @author liupeidong
 * Created on 2019/1/29 12:40
 */
public class App extends Application {

    @Override
    public void onCreate() {


        /**
         * 初始化 BmobSDK
         *
         *  */
        LitePal.initialize(this);
        super.onCreate();

    }
}
