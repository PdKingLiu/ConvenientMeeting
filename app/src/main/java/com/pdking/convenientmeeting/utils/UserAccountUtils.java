package com.pdking.convenientmeeting.utils;

import android.app.Application;

import com.pdking.convenientmeeting.App;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;

/**
 * @author liupeidong
 * Created on 2019/4/26 21:30
 */
public class UserAccountUtils {

    public static UserInfo getUserInfo(Application app) {
        return ((App) app).getUserInfo();
    }

    public static UserToken getUserToken(Application app) {
        return ((App) app).getUserToken();
    }

    public static void setUserToken(UserToken userToken, Application app) {
        ((App) app).setUserToken(userToken);
    }

    public static void setUserInfo(UserInfo userInfo, Application app) {
        ((App) app).setUserInfo(userInfo);
    }

    public static boolean accountIsValid(Application app) {
        return getUserInfo(app) != null && getUserToken(app) != null;
    }

}
