package com.pdking.convenientmeeting;

import android.app.Application;

import com.pdking.convenientmeeting.common.Constant;

import cn.bmob.v3.Bmob;

/**
 * @author liupeidong
 * Created on 2019/1/29 12:40
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 初始化 BmobSDK
         * */
        Bmob.initialize(this, Constant.APP_ID_Bmob);
    }
}
