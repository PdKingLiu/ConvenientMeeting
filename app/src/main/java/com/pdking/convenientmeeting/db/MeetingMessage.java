package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/4/1 16:35
 */
public class MeetingMessage extends LitePalSupport {
    public int isPoll = -1;
    public int meetingType;
    @SerializedName("meetingId")
    public int meetingId;
    @SerializedName("meetingName")
    public String meetingName;
    @SerializedName("meetingIntro")
    public String meetingIntro;
    @SerializedName("peopleNum")
    public int peopleNum;
    @SerializedName("startTime")
    public String startTime;
    @SerializedName("endTime")
    public String endTime;
    @SerializedName("status")
    public int status;
    @SerializedName("userStatus")
    public int userStatus;
    @SerializedName("roomId")
    public int roomId;
    @SerializedName("roomName")
    public String roomName;
    @SerializedName("masterId")
    public int masterId;
    @SerializedName("masterName")
    public String masterName;
    @SerializedName("memberStatus")
    public List<MemberStatusBean> memberStatus;

    public static class MemberStatusBean {
        @SerializedName("userId")
        public int userId;
        @SerializedName("username")
        public String username;
        @SerializedName("userStatus")
        public int userStatus;
    }
}
