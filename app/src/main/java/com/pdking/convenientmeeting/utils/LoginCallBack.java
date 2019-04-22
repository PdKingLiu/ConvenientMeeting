package com.pdking.convenientmeeting.utils;

import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;

/**
 * @author liupeidong
 * Created on 2019/4/18 22:26
 */
public interface LoginCallBack {
    void newMessageCallBack(UserInfo newInfo, UserToken newToken);
}
