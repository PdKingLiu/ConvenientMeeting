package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

/**
 * @author liupeidong
 * Created on 2019/3/20 15:12
 */
public class MeetingMessage extends LitePalSupport{
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

    @Override
    public String toString() {
        return "MeetingMessage{" +
                "meetingId=" + meetingId +
                ", meetingName='" + meetingName + '\'' +
                ", meetingIntro='" + meetingIntro + '\'' +
                ", roomId=" + roomId +
                ", status=" + status +
                ", masterId=" + masterId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
