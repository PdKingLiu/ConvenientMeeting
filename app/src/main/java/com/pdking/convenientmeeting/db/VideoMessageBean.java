package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

/**
 * @author liupeidong
 * Created on 2019/7/25 10:51
 */
public class VideoMessageBean extends LitePalSupport {
    public int kind;
    @SerializedName("id")
    public int id;
    @SerializedName("liveName")
    public String liveName;
    @SerializedName("livePwd")
    public String livePwd;
    @SerializedName("userAvatarInfo")
    public VideoMessageBean.UserAvatarInfoBean userAvatarInfo;
    @SerializedName("onlineNum")
    public int onlineNum;
    @SerializedName("status")
    public int status;
    @SerializedName("startTime")
    public long startTime;
    @SerializedName("endTime")
    public long endTime;

    public static class UserAvatarInfoBean extends LitePalSupport {
        @SerializedName("userId")
        public int userId;
        @SerializedName("username")
        public String username;
        @SerializedName("avatarUrl")
        public String avatarUrl;
    }
}
