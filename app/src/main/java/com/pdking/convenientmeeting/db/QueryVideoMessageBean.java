package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/5/10 14:52
 */
public class QueryVideoMessageBean {

    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public List<DataBean> data;

    public static class DataBean {
        @SerializedName("id")
        public int id;
        @SerializedName("liveName")
        public String liveName;
        @SerializedName("livePwd")
        public String livePwd;
        @SerializedName("userAvatarInfo")
        public UserAvatarInfoBean userAvatarInfo;
        @SerializedName("onlineNum")
        public int onlineNum;
        @SerializedName("status")
        public int status;
        @SerializedName("startTime")
        public long startTime;
        @SerializedName("endTime")
        public long endTime;

        public static class UserAvatarInfoBean {
            @SerializedName("userId")
            public int userId;
            @SerializedName("username")
            public String username;
            @SerializedName("avatarUrl")
            public String avatarUrl;
        }
    }
}
