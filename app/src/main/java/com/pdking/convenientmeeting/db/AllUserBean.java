package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/5/11 17:31
 */
public class AllUserBean {

    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public List<DataBean> data;

    public static class DataBean {
        @SerializedName("id")
        public int id;
        @SerializedName("username")
        public String username;
        @SerializedName("password")
        public String password;
        @SerializedName("sex")
        public String sex;
        @SerializedName("role")
        public int role;
        @SerializedName("phone")
        public String phone;
        @SerializedName("email")
        public String email;
        @SerializedName("avatarUrl")
        public String avatarUrl;
        @SerializedName("faceUrl")
        public String faceUrl;
        @SerializedName("createTime")
        public long createTime;
        @SerializedName("updateTime")
        public long updateTime;
        @SerializedName("faceData")
        public String faceData;
    }
}
