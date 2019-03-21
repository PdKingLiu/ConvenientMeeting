package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/3/20 15:15
 */
public class OneMeetingRoomMessageBean {
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public OneMeetingRoomMessage data;
}
