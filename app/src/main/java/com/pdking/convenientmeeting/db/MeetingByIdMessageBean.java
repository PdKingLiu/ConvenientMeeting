package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/4/10 21:46
 */
public class MeetingByIdMessageBean {
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public MeetingByIdMessage data;
}
