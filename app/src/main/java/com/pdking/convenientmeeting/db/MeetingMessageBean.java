package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

/**
 * @author liupeidong
 * Created on 2019/3/27 20:40
 */
public class MeetingMessageBean {
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public MeetingMessage data;
}
