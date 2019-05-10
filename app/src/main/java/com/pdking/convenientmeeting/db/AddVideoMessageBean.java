package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

/**
 * @author liupeidong
 * Created on 2019/5/10 14:50
 */
public class AddVideoMessageBean {

    @SerializedName("status")
    public int status;
    @SerializedName("msg")
    public String msg;
    @SerializedName("data")
    public DataBean data;

    public static class DataBean {
        @SerializedName("id")
        public int id;
        @SerializedName("liveName")
        public String liveName;
        @SerializedName("livePwd")
        public String livePwd;
        @SerializedName("createId")
        public int createId;
        @SerializedName("onlineNum")
        public int onlineNum;
        @SerializedName("status")
        public int status;
        @SerializedName("startTime")
        public long startTime;
        @SerializedName("endTime")
        public long endTime;
    }
}
