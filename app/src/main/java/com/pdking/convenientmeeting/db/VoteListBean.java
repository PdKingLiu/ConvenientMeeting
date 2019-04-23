package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/4/23 14:45
 */
public class VoteListBean {

    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public List<VoteBean> data;

    public static class VoteBean {
        @SerializedName("voteId")
        public int voteId;
        @SerializedName("topic")
        public String topic;
        @SerializedName("selectWay")
        public int selectWay;
        @SerializedName("userInfo")
        public UserInfoBean userInfo;
        @SerializedName("createTime")
        public long createTime;
        @SerializedName("endTime")
        public long endTime;
        @SerializedName("optionList")
        public List<OptionListBean> optionList;
        @SerializedName("userSelectList")
        public List<Integer> userSelectList;

        public static class UserInfoBean {
            @SerializedName("userId")
            public int userId;
            @SerializedName("username")
            public String username;
            @SerializedName("avatarUrl")
            public String avatarUrl;
        }

        public static class OptionListBean {
            @SerializedName("id")
            public int id;
            @SerializedName("voteId")
            public int voteId;
            @SerializedName("optionName")
            public String optionName;
            @SerializedName("num")
            public int num;
        }
    }
}
