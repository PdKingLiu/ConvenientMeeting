package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/3/20 15:16
 */
public class OneMeetingRoomMessage extends LitePalSupport {
    @SerializedName("roomNumber")
    public String roomNumber;
    @SerializedName("content")
    public int content;
    @SerializedName("machineNumber")
    public String machineNumber;
    @SerializedName("status")
    public int status;
    @SerializedName("id")
    public int meetingRoomId;
    @SerializedName("meetingLists")
    public List<RoomOfMeetingMessage> meetingLists;
    @SerializedName("recentlyMeetings")
    public List<RoomOfMeetingMessage> recentlyMeetings;
}
