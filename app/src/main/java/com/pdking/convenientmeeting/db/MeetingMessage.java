package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

/**
 * @author liupeidong
 * Created on 2019/3/20 15:12
 */
public class MeetingMessage {
    @SerializedName("id")
    public int meetingId;
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
