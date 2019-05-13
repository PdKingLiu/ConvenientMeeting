package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/5/13 13:35
 */
public class LiveDetailBean {

    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public DataBean data;

    public static class DataBean {
        @SerializedName("id")
        public int id;
        @SerializedName("liveName")
        public String liveName;
        @SerializedName("status")
        public int status;
        @SerializedName("onlineNum")
        public int onlineNum;
        @SerializedName("startTime")
        public long startTime;
        @SerializedName("endTime")
        public long endTime;
        @SerializedName("meetingMembers")
        public List<MeetingMembersBean> meetingMembers;

        public static class MeetingMembersBean {
            @SerializedName("userId")
            public int userId;
            @SerializedName("username")
            public String username;
            @SerializedName("avatarUrl")
            public String avatarUrl;
        }
    }
}
