package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

/**
 * @author liupeidong
 * Created on 2019/5/10 22:47
 */
public class EnterLiveVideoBean {

    @SerializedName("status")
    public int status;
    @SerializedName("msg")
    public String msg;
    @SerializedName("data")
    public DataBean data;

    public static class DataBean {
        @SerializedName("userId")
        public int userId;
        @SerializedName("username")
        public String username;
        @SerializedName("avatarUrl")
        public String avatarUrl;
    }
}
