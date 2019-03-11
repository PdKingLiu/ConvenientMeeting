package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

/**
 * @author liupeidong
 * Created on 2019/3/10 22:28
 */
public class UserDataBean {
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public UserInfo data;
}
