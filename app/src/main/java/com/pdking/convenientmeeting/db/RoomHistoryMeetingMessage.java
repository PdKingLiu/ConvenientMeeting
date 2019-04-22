package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

/**
 * @author liupeidong
 * Created on 2019/4/22 21:38
 */
public class RoomHistoryMeetingMessage {
    @SerializedName("id")
    public int id;
    @SerializedName("meetingName")
    public String meetingName;
    @SerializedName("meetingIntro")
    public String meetingIntro;
    @SerializedName("roomId")
    public int roomId;
    @SerializedName("status")
    public int status;
    @SerializedName("masterId")
    public int masterId;
    @SerializedName("startTime")
    public long startTime;
    @SerializedName("endTime")
    public long endTime;
    @SerializedName("createTime")
    public long createTime;
    @SerializedName("updateTime")
    public long updateTime;
}
