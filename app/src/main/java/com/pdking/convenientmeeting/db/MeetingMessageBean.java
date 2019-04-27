package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/4/1 16:34
 */
public class MeetingMessageBean {
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public List<MeetingMessage> data;
}
