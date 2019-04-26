package com.pdking.convenientmeeting;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.pdking.convenientmeeting.common.Constant;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.tencent.smtt.sdk.QbSdk;

import org.litepal.LitePal;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/1/29 12:40
 */
public class App extends Application {

    private UserToken userToken;

    private UserInfo userInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        LitePal.getDatabase();
        List<UserToken> token = LitePal.findAll(UserToken.class);
        List<UserInfo> info = LitePal.findAll(UserInfo.class);
        userToken = token.size() == 0 ? null : token.get(0);
        userInfo = info.size() == 0 ? null : info.get(0);
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
            }

            @Override
            public void onViewInitFinished(boolean b) {
            }
        });

    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public UserToken getUserToken() {
        return userToken;
    }

    public void setUserToken(UserToken userToken) {
        this.userToken = userToken;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

}
