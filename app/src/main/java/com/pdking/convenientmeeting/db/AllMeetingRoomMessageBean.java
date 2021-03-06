package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/3/20 15:19
 */
public class AllMeetingRoomMessageBean {
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public List<OneMeetingRoomMessage> data;
}
