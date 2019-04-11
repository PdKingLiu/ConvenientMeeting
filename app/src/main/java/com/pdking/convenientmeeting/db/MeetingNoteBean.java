package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

/**
 * @author liupeidong
 * Created on 2019/4/11 22:44
 */
public class MeetingNoteBean {

    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public String data;
}
